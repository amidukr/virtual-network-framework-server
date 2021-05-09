package org.amidukr.software.vnf.server.core.commonservice;

import org.amidukr.software.vnf.server.core.commandprocessor.CommandEvent;
import org.amidukr.software.vnf.server.core.commandprocessor.InvocationResult;
import org.amidukr.software.vnf.server.core.commandprocessor.InvokeHandler;

/**
 * Created by Dmytro Brazhnyk on 6/4/2017.
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
