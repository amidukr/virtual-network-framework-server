package org.vnf.server.core.commandprocessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static java.util.Collections.unmodifiableCollection;

/**
 * Created by qik on 6/9/2017.
 */
public class ImmutableServiceConfiguration implements ServiceConfiguration{

    private final Collection<InvokeHandler> invokeHandlers;
    private final Collection<ConnectionLostHandler> connectionLostHandlers;

    public ImmutableServiceConfiguration(Collection<InvokeHandler> invokeHandlers, Collection<ConnectionLostHandler> connectionLostHandlers) {
        this.invokeHandlers = unmodifiableCollection(new ArrayList<>(invokeHandlers));
        this.connectionLostHandlers = unmodifiableCollection(new ArrayList<>(connectionLostHandlers));
    }

    @Override
    public Collection<InvokeHandler> getInvokeHandlers() {
        return invokeHandlers;
    }

    @Override
    public Collection<ConnectionLostHandler> getConnectionLostHandlers() {
        return connectionLostHandlers;
    }
}
