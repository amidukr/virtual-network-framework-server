package org.vnf.server.commandprocessor;

import java.util.Collection;

/**
 * Created by qik on 6/4/2017.
 */
public interface ServiceCommandHandlers {
    Collection<InvokeHandler> getInvokeHandlers();
    Collection<ConnectionLostHandler> getConnectionLostHandlers();
}
