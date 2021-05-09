package org.amidukr.software.vnf.server.mocks;

import org.amidukr.software.vnf.server.core.commandprocessor.AuthorizationType;
import org.amidukr.software.vnf.server.core.commandprocessor.CommandEvent;
import org.amidukr.software.vnf.server.core.commandprocessor.InvocationResult;
import org.amidukr.software.vnf.server.core.commandprocessor.InvokeHandler;

/**
* Created by Dmytro Brazhnyk on 6/18/2017.
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
