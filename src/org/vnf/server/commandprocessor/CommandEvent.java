package org.vnf.server.commandprocessor;

import org.vnf.server.CommandProcessor;

/**
 * Created by qik on 6/3/2017.
 */
public class CommandEvent {

    private String commandArgument;
    private CommandProcessor commandProcessor;
    private EndpointConnection endpointConnection;
    private String endpointId;

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
