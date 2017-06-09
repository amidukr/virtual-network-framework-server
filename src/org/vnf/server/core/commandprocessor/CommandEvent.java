package org.vnf.server.core.commandprocessor;

/**
 * Created by qik on 6/3/2017.
 */
public class CommandEvent {

    private final CommandProcessor commandProcessor;
    private final EndpointConnection endpointConnection;
    private final String commandArgument;

    public CommandEvent(CommandProcessor commandProcessor, EndpointConnection endpointConnection, String commandArgument) {
        this.endpointConnection = endpointConnection;
        this.commandProcessor = commandProcessor;
        this.commandArgument = commandArgument;
    }

    public String getCommandArgument() {
        return commandArgument;
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
