package org.vnf.server.services;

import org.vnf.server.core.commandprocessor.CommandProcessor;
import org.vnf.server.core.commonservice.CommonServiceConfiguration;
import org.vnf.server.core.servicefactory.ServiceConfigurationFactory;

/**
 * Created by qik on 6/10/2017.
 */
public interface ServiceTestingUtils {
    public static CommandProcessor createCommandProcessor(Object service) {
        CommandProcessor commandProcessor = new CommandProcessor();

        ServiceConfigurationFactory factory = new ServiceConfigurationFactory();

        factory.addService(service);

        commandProcessor.addServiceHandlers(new CommonServiceConfiguration());
        commandProcessor.addServiceHandlers(factory.create());

        return commandProcessor;
    }
}
