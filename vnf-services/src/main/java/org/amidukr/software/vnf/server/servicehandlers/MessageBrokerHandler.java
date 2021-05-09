package org.amidukr.software.vnf.server.servicehandlers;

import org.amidukr.software.vnf.server.core.commandprocessor.InvocationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.amidukr.software.vnf.server.core.commandprocessor.CommandEvent;
import org.amidukr.software.vnf.server.core.commandprocessor.CommandProcessor;
import org.amidukr.software.vnf.server.core.commandprocessor.EndpointConnection;
import org.amidukr.software.vnf.server.core.servicefactory.Invoke;
import org.amidukr.software.vnf.server.core.servicefactory.ServiceObject;

/**
 * Created by Dmytro Brazhnyk on 6/10/2017.
 */
public class MessageBrokerHandler implements ServiceObject {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageBrokerHandler.class);

    @Invoke("SEND_TO_ENDPOINT")
    public InvocationResult sendToEndpoint(CommandEvent event) {
        CommandProcessor commandProcessor = event.getCommandProcessor();
        String commandArgument = event.getCommandArgument();

        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("from: " + event.getEndpointId() + " SEND_TO_ENDPOINT " + commandArgument);
        }

        if(commandArgument == null) {
            return InvocationResult.failed("SEND_TO_ENDPOINT_ARGUMENT_CANNOT_BE_NULL");
        }

        int endOfLine = commandArgument.indexOf('\n');

        if(endOfLine == -1) {
            return InvocationResult.failed("SEND_TO_ENDPOINT_MALFORMED_ARGUMENT");
        }

        String recipientEndpoint = commandArgument.substring(0, endOfLine);
        String message = commandArgument.substring(endOfLine + 1);

        EndpointConnection recipientEndpointConnection = commandProcessor.getEndpointConnection(recipientEndpoint);

        if(recipientEndpointConnection == null) {
            return InvocationResult.succeed("SEND_TO_ENDPOINT_RECIPIENT_ENDPOINT_CANNOT_BE_FOUND");
        }

        commandProcessor.pushMessage(recipientEndpoint, "ENDPOINT_MESSAGE", event.getEndpointId() + "\n" + message);

        return null;
    }
}
