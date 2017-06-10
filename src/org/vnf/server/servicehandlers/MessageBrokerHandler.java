package org.vnf.server.servicehandlers;

import org.vnf.server.core.commandprocessor.CommandEvent;
import org.vnf.server.core.commandprocessor.CommandProcessor;
import org.vnf.server.core.commandprocessor.InvocationResult;
import org.vnf.server.core.servicefactory.Invoke;

import static org.vnf.server.core.commandprocessor.InvocationResult.failed;

/**
 * Created by qik on 6/10/2017.
 */
public class MessageBrokerHandler {

    @Invoke("SEND_TO_ENDPOINT")
    public InvocationResult sendToEndpoint(CommandEvent event) {
        CommandProcessor commandProcessor = event.getCommandProcessor();
        String commandArgument = event.getCommandArgument();

        if(commandArgument == null) {
            return failed("SEND_TO_ENDPOINT_ARGUMENT_CANNOT_BE_NULL");
        }

        int endOfLine = commandArgument.indexOf('\n');

        if(endOfLine == -1) {
            return failed("SEND_TO_ENDPOINT_MALFORMED_ARGUMENT");
        }

        String recipientEndpoint = commandArgument.substring(0, endOfLine);
        String message = commandArgument.substring(endOfLine + 1);

        commandProcessor.pushMessage(recipientEndpoint, "ENDPOINT_MESSAGE", event.getEndpointId() + "\n" + message);

        return null;
    }
}
