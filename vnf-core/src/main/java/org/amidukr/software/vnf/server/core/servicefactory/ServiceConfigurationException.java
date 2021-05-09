package org.amidukr.software.vnf.server.core.servicefactory;

import java.io.Serial;

/**
 * Created by Dmytro Brazhnyk on 6/9/2017.
 */
@SuppressWarnings("serial")
public class ServiceConfigurationException extends RuntimeException {
    public ServiceConfigurationException(String message) {
        super(message);
    }
}
