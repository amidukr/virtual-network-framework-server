package org.vnf.server;

import org.vnf.server.core.commonservice.CommonServiceHandlersConfiguration;
import org.vnf.server.core.servicefactory.ServiceConfigurationFactory;
import org.vnf.server.servicehandlers.MessageBrokerHandler;
import org.vnf.server.servicehandlers.StoreServiceHandlers;

/**
 * Created by qik on 6/17/2017.
 */
public class VnfServiceHandlersFactory extends ServiceConfigurationFactory {
    public VnfServiceHandlersFactory() {

        addServiceHandlersConfiguration(new CommonServiceHandlersConfiguration());
        addService(new MessageBrokerHandler());
        addService(new StoreServiceHandlers());
    }
}
