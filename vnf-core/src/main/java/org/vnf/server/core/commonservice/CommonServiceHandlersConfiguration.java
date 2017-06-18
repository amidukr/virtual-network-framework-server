package org.vnf.server.core.commonservice;

import org.vnf.server.core.commandprocessor.ConnectionLostHandler;
import org.vnf.server.core.commandprocessor.InvokeHandler;
import org.vnf.server.core.commandprocessor.ServiceHandlersConfiguration;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by qik on 6/4/2017.
 */
public class CommonServiceHandlersConfiguration implements ServiceHandlersConfiguration {

    protected InvokeHandler createHelpHandler() {
        return new HelpCommandHandler();
    }

    protected InvokeHandler createLoginHandler() {
        return new LoginCommandHandler();
    }

    protected InvokeHandler createPingHandler() {
        return new PingCommandHandler();
    }

    @Override
    public Collection<InvokeHandler> getInvokeHandlers() {
        return Arrays.asList(
                createHelpHandler(),
                createLoginHandler(),
                createPingHandler());
    }

    @Override
    public Collection<ConnectionLostHandler> getConnectionLostHandlers() {
        return null;
    }
}
