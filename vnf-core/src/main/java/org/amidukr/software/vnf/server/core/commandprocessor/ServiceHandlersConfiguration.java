package org.amidukr.software.vnf.server.core.commandprocessor;

import java.util.Collection;

/**
 * Created by Dmytro Brazhnyk on 6/4/2017.
 */
public interface ServiceHandlersConfiguration {
    Collection<InvokeHandler> getInvokeHandlers();
    Collection<ConnectionLostHandler> getConnectionLostHandlers();
}
