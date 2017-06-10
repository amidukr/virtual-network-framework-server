package org.vnf.server.services;

import org.vnf.server.core.commandprocessor.CommandEvent;
import org.vnf.server.core.commandprocessor.CommandException;
import org.vnf.server.core.commandprocessor.CommandProcessor;
import org.vnf.server.core.servicefactory.Invoke;

/**
 * Created by qik on 6/10/2017.
 */
public class MessageBrokerService {

    @Invoke("SEND_TO_ENDPOINT")
    public String sendToEndpoint(CommandEvent event) throws CommandException {
        CommandProcessor commandProcessor = event.getCommandProcessor();
        String commandArgument = event.getCommandArgument();

        if(commandArgument == null) {
            throw new CommandException("SEND_TO_ENDPOINT_ARGUMENT_CANNOT_BE_NULL");
        }

        int endOfLine = commandArgument.indexOf('\n');

        if(endOfLine == -1) {
            throw new CommandException("SEND_TO_ENDPOINT_MALFORMED_ARGUMENT");
        }

        String recipientEndpoint = commandArgument.substring(0, endOfLine);
        String message = commandArgument.substring(endOfLine + 1);

        commandProcessor.pushMessage(recipientEndpoint, "ENDPOINT_MESSAGE", event.getEndpointId() + "\n" + message);

        return null;
    }
}
