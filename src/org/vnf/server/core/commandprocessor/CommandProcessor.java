package org.vnf.server.core.commandprocessor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.vnf.server.utils.CollectionUtils.emptyIfNull;

/**
 * Created by qik on 6/3/2017.
 */
public class CommandProcessor {
    public static final String CALL_FAILED_COMMAND_HEADER_MALFORMED = "CALL_FAILED_COMMAND_HEADER_MALFORMED";
    public static final String CALL_FAILED_UNKNOWN_METHOD           = "CALL_FAILED_UNKNOWN_METHOD";
    public static final String CALL_FAILED_UNEXPECTED_EXCEPTION     = "CALL_FAILED_UNEXPECTED_EXCEPTION";
    public static final String CALL_FAILED_AUTHENTICATION_REQUIRED  = "CALL_FAILED_AUTHENTICATION_REQUIRED";

    private final Map<String, InvokeHandler> invokeHandlers = new HashMap<>();
    private final List<ConnectionLostHandler> connectionLostHandlers = new ArrayList<>();

    private final ConcurrentHashMap<String, EndpointConnection> endpointConnections = new ConcurrentHashMap<>();


    public void addInvokeHandler(InvokeHandler invokeHandler) {
        String commandName = invokeHandler.getCommandName();

        if(invokeHandlers.containsKey(commandName)) {
            throw new IllegalStateException("InvokeHandler for command: '" + commandName +"' already exists");
        }

        invokeHandlers.put(commandName, invokeHandler);
    }

    public void addConnectionLostHandler(ConnectionLostHandler connectionLostHandler) {
        connectionLostHandlers.add(connectionLostHandler);
    }

    public void addServiceHandlers(ServiceConfiguration serviceConfiguration) {
        Collection<InvokeHandler> serviceInvokeHandlers = emptyIfNull(serviceConfiguration.getInvokeHandlers());
        Collection<ConnectionLostHandler> serviceConnectionLostHandlers = emptyIfNull(serviceConfiguration.getConnectionLostHandlers());

        serviceInvokeHandlers.stream().forEach(this::addInvokeHandler);
        serviceConnectionLostHandlers.stream().forEach(this::addConnectionLostHandler);
    }

    public boolean authenticate(EndpointConnection remoteConnection, String endpointId) {

        if(remoteConnection.isAuthenticated()) {
            return false;
        }

        if(endpointConnections.putIfAbsent(endpointId, remoteConnection) != null){
            return false;
        }

        remoteConnection.authenticate(endpointId);

        return true;
    }

    public void pushMessage(String endpointId, String commandName, String argument) {
        EndpointConnection endpointConnection = endpointConnections.get(endpointId);

        if(endpointConnection == null) return;

        if(argument != null) {
            endpointConnection.sendMessage(commandName + "\n" + argument);
        }else{
            endpointConnection.sendMessage(commandName);
        }

    }

    public void remoteInvoke(EndpointConnection endpointConnection, String message) {
        int headerEolIndex = message.indexOf('\n');
        int headerSpaceSplitIndex = message.indexOf(' ');


        String commandArgument;
        String header;

        if(headerEolIndex != -1) {
            header = message.substring(0, headerEolIndex);
            commandArgument = message.substring(headerEolIndex + 1);
        }else{
            header = message;
            commandArgument = null;
        }

        if(headerSpaceSplitIndex == -1) {
            sendErrorMessage(endpointConnection, header, CALL_FAILED_COMMAND_HEADER_MALFORMED);
            return;
        }

        try {
            String commandName;
            if(headerEolIndex != -1) {
                commandName = message.substring(headerSpaceSplitIndex + 1, headerEolIndex);
            }else{
                commandName = message.substring(headerSpaceSplitIndex + 1);
            }


            InvokeHandler invokeHandler = invokeHandlers.get(commandName);

            if(invokeHandler == null) {
                sendErrorMessage(endpointConnection, header, CALL_FAILED_UNKNOWN_METHOD);
                return;
            }

            if(!endpointConnection.isAuthenticated() && invokeHandler.getAuthorizationType() == AuthorizationType.AUTHENTICATED_ONLY) {
                sendErrorMessage(endpointConnection, header, CALL_FAILED_AUTHENTICATION_REQUIRED);
                return;
            }

            String response = invokeHandler.handleCommand(new CommandEvent(this, endpointConnection, commandArgument));
            sendSuccessMessage(endpointConnection, header, response);

        } catch (CommandException e) {
            sendErrorMessage(endpointConnection, header, e.getMessage());
        }catch (RuntimeException e) {
            sendErrorMessage(endpointConnection, header, CALL_FAILED_UNEXPECTED_EXCEPTION);
            e.printStackTrace();
        }
    }

    private void sendSuccessMessage(EndpointConnection endpointConnection, String header, String response) {
        if(response != null) {
            endpointConnection.sendMessage(header + "\n" + response);
        }else{
            endpointConnection.sendMessage(header);
        }
    }

    private void sendErrorMessage(EndpointConnection endpointConnection, String header, String errorReason) {
        endpointConnection.sendMessage("CALL_ERROR\n" + header + "\n" + errorReason);
    }

    public void connectionLost(EndpointConnection endpointConnection) {
        boolean authenticated = endpointConnection.isAuthenticated();

        for (ConnectionLostHandler connectionLostHandler : connectionLostHandlers) {
            if(!authenticated && connectionLostHandler.getAuthorizationType() == AuthorizationType.AUTHENTICATED_ONLY) {
                continue;
            }

            try{
                connectionLostHandler.onConnectionLost(new ConnectionLostEvent(this, endpointConnection));
            }catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }
}
