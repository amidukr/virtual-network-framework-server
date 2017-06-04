package org.vnf.server.commandprocessor.handlers;

import org.vnf.server.CommandProcessor;
import org.vnf.server.commandprocessor.CommandEvent;
import org.vnf.server.commandprocessor.EndpointConnection;
import org.vnf.server.commandprocessor.InvokeHandler;

/**
 * Created by qik on 6/3/2017.
 */
public class LoginCommandHandler extends InvokeHandler {

    public LoginCommandHandler() {
        super("LOGIN");
    }

    @Override
    public boolean isAuthenticatedOnly() {
        return false;
    }

    @Override
    public String handleCommand(CommandEvent event) {
        CommandProcessor commandProcessor = event.getCommandProcessor();
        EndpointConnection remoteConnection = event.getEndpointConnection();
        String endpointId = event.getCommandArgument();

        if(remoteConnection.isAuthenticated()) {
            if(remoteConnection.getEndpointId().equals(endpointId)) {
                return "OK";
            }else{
                return "NOT-OK-ALREADY-AUTHORIZED";
            }
        }

        if(!commandProcessor.authenticate(remoteConnection, endpointId)) {
            return "NOT-OK";
        }

        return "OK";
    }
}
