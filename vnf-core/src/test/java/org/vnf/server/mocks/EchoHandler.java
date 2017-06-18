package org.vnf.server.mocks;

import org.vnf.server.core.commandprocessor.AuthorizationType;
import org.vnf.server.core.commandprocessor.CommandEvent;
import org.vnf.server.core.commandprocessor.InvocationResult;
import org.vnf.server.core.commandprocessor.InvokeHandler;

/**
* Created by qik on 6/18/2017.
*/
public class EchoHandler extends InvokeHandler {

    public EchoHandler(String commandName, AuthorizationType authorizationType) {
        super(commandName);

        setAuthorizationType(authorizationType);
    }

    @Override
    public InvocationResult handleCommand(CommandEvent event) {
        return InvocationResult.succeed("RESPONSE-TO-" + event.getCommandArgument() + "; endpointId = " + event.getEndpointId());
    }
}
