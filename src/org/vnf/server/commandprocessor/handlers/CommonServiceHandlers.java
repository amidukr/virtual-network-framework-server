package org.vnf.server.commandprocessor.handlers;

import org.vnf.server.commandprocessor.ConnectionLostHandler;
import org.vnf.server.commandprocessor.InvokeHandler;
import org.vnf.server.commandprocessor.ServiceCommandHandlers;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by qik on 6/4/2017.
 */
public class CommonServiceHandlers implements ServiceCommandHandlers {

    protected InvokeHandler createLoginHandler() {
        return new LoginCommandHandler();
    }

    protected InvokeHandler createPingHandler() {
        return new PingCommandHandler();
    }

    @Override
    public Collection<InvokeHandler> getInvokeHandlers() {
        return Arrays.asList(createLoginHandler(), createPingHandler());
    }

    @Override
    public Collection<ConnectionLostHandler> getConnectionLostHandlers() {
        return null;
    }
}
