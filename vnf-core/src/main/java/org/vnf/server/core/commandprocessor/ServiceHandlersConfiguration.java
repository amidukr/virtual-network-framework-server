package org.vnf.server.core.commandprocessor;

import java.util.Collection;

/**
 * Created by qik on 6/4/2017.
 */
public interface ServiceHandlersConfiguration {
    Collection<InvokeHandler> getInvokeHandlers();
    Collection<ConnectionLostHandler> getConnectionLostHandlers();
}
