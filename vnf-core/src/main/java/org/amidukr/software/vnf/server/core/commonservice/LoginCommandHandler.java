package org.amidukr.software.vnf.server.core.commonservice;

import org.amidukr.software.vnf.server.core.commandprocessor.*;
import org.amidukr.software.vnf.server.core.commandprocessor.*;

import static org.amidukr.software.vnf.server.core.commandprocessor.InvocationResult.succeed;

/**
 * Created by Dmytro Brazhnyk on 6/3/2017.
 */
public class LoginCommandHandler extends InvokeHandler {

    public LoginCommandHandler() {
        super("LOGIN");

        setAuthorizationType(AuthorizationType.ANY);
    }

    @Override
    public InvocationResult handleCommand(CommandEvent event) {
        CommandProcessor commandProcessor = event.getCommandProcessor();
        EndpointConnection remoteConnection = event.getEndpointConnection();
        String endpointId = event.getCommandArgument();

        if(remoteConnection.isAuthenticated()) {
            if(remoteConnection.getEndpointId().equals(endpointId)) {
                return succeed("OK");
            }else{
                return succeed("NOT-OK-ALREADY-AUTHORIZED");
            }
        }

        if(!commandProcessor.authenticate(remoteConnection, endpointId)) {
            return succeed("NOT-OK-LOGIN-ALREADY-IN-USE");
        }

        return succeed("OK");
    }
}
