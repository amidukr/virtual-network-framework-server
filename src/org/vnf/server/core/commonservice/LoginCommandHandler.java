package org.vnf.server.core.commonservice;

import org.vnf.server.core.commandprocessor.*;

/**
 * Created by qik on 6/3/2017.
 */
public class LoginCommandHandler extends InvokeHandler {

    public LoginCommandHandler() {
        super("LOGIN");

        setAuthorizationType(AuthorizationType.ANY);
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
            return "NOT-OK-ALREADY-IN-USE";
        }

        return "OK";
    }
}
