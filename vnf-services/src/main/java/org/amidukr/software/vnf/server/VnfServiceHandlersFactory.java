package org.amidukr.software.vnf.server;

import org.amidukr.software.vnf.server.servicehandlers.MessageBrokerHandler;
import org.amidukr.software.vnf.server.servicehandlers.StoreServiceHandlers;
import org.amidukr.software.vnf.server.core.servicefactory.ServiceConfigurationFactory;

/**
 * Created by Dmytro Brazhnyk on 6/17/2017.
 */
public class VnfServiceHandlersFactory extends ServiceConfigurationFactory {
    public VnfServiceHandlersFactory() {

        addService(new MessageBrokerHandler());
        addService(new StoreServiceHandlers());
    }
}
