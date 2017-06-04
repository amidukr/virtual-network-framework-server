package org.vnf.server.commandprocessor.handlers;

import org.vnf.server.commandprocessor.CommandEvent;
import org.vnf.server.commandprocessor.InvokeHandler;

/**
 * Created by qik on 6/4/2017.
 */
public class PingCommandHandler extends InvokeHandler {

    public PingCommandHandler() {
        super("PING");
    }

    @Override
    public String handleCommand(CommandEvent event) {
        return null;
    }
}
