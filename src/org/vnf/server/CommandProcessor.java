package org.vnf.server;

import org.vnf.server.commandprocessor.ConnectionLostHandler;
import org.vnf.server.commandprocessor.EndpointConnection;
import org.vnf.server.commandprocessor.EndpointConnectionCaptor;
import org.vnf.server.commandprocessor.InvokeHandler;
import org.vnf.server.commandprocessor.handlers.CommonServiceHandlers;

/**
 * Created by qik on 6/3/2017.
 */
public class CommandProcessor {
    public static final String CALL_FAILED_UNKNOWN_METHOD          = "CALL_FAILED_UNKNOWN_METHOD";
    public static final String CALL_FAILED_UNEXPECTED_EXCEPTION    = "CALL_FAILED_UNEXPECTED_EXCEPTION";
    public static final String CALL_FAILED_AUTHENTICATION_REQUIRED = "CALL_FAILED_AUTHENTICATION_REQUIRED";

    public void addInvokeHandler(InvokeHandler invokeHandler) {

    }

    public void addConnectionLostHandler(ConnectionLostHandler connectionLostHandler) {

    }

    public void addServiceHandlers(CommonServiceHandlers commonServiceHandlers) {

    }

    public boolean authenticate(EndpointConnection remoteConnection, String endpointId) {

        return false;
    }

    public void pushMessage(String endpointId, String commandName, String argument) {

    }

    public void remoteInvoke(EndpointConnection remoteConnection, String message) {

    }

    public void connectionLost(EndpointConnection endpointConnection) {

    }
}
