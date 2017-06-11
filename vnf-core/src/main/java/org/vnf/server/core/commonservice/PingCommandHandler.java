package org.vnf.server.core.commonservice;

import org.vnf.server.core.commandprocessor.CommandEvent;
import org.vnf.server.core.commandprocessor.InvocationResult;
import org.vnf.server.core.commandprocessor.InvokeHandler;

/**
 * Created by qik on 6/4/2017.
 */
public class PingCommandHandler extends InvokeHandler {

    public PingCommandHandler() {
        super("PING");
    }

    @Override
    public InvocationResult handleCommand(CommandEvent event) {
        return InvocationResult.succeed(null);
    }
}
