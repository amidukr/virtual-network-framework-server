package org.vnf.server.core.commandprocessor;

/**
 * Created by qik on 6/4/2017.
 */
public class ConnectionLostEvent {
    private final CommandProcessor commandProcessor;
    private final EndpointConnection endpointConnection;

    public ConnectionLostEvent(CommandProcessor commandProcessor, EndpointConnection endpointConnection) {
        this.commandProcessor = commandProcessor;
        this.endpointConnection = endpointConnection;
    }

    public CommandProcessor getCommandProcessor() {
        return commandProcessor;
    }

    public EndpointConnection getEndpointConnection() {
        return endpointConnection;
    }

    public String getEndpointId() {
        return endpointConnection.getEndpointId();
    }
}
