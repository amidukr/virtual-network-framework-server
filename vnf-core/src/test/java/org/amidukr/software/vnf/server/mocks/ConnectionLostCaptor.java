package org.amidukr.software.vnf.server.mocks;

import org.amidukr.software.vnf.server.core.commandprocessor.*;
import org.amidukr.software.vnf.server.core.commandprocessor.*;
import org.amidukr.software.vnf.server.utils.Captor;

/**
* Created by Dmytro Brazhnyk on 6/18/2017.
*/
public class ConnectionLostCaptor extends ConnectionLostHandler {

    private final Captor<Boolean> eventFiredCaptor = new Captor<>(false);

    private final Captor<CommandProcessor> commandProcessorCaptor = new Captor<>();
    private final Captor<EndpointConnection> endpointConnectionCaptor = new Captor<>();
    private final Captor<String> endpointIdCaptor = new Captor<>();
    private String handlerId;


    public ConnectionLostCaptor(AuthorizationType authorizationType) {
        setAuthorizationType(authorizationType);
    }

    public ConnectionLostCaptor(String handlerId, AuthorizationType authorizationType) {
        this(authorizationType);

        this.handlerId = handlerId;
    }

    public String getHandlerId() {
        return handlerId;
    }

    public Boolean getEventFired() {
        return eventFiredCaptor.getValue();
    }

    public CommandProcessor getCapturedCommandProcessor() {
        return commandProcessorCaptor.getValue();
    }

    public EndpointConnection getCapturedEndpointConnection() {
        return endpointConnectionCaptor.getValue();
    }

    public String getCapturedEndpointId() {
        return endpointIdCaptor.getValue();
    }

    @Override
    public void onConnectionLost(ConnectionLostEvent connectionLostEvent) {
        eventFiredCaptor.capture(true);

        commandProcessorCaptor.capture(connectionLostEvent.getCommandProcessor());
        endpointConnectionCaptor.capture(connectionLostEvent.getEndpointConnection());
        endpointIdCaptor.capture(connectionLostEvent.getEndpointId());
    }
}
