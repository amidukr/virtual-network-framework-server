package org.amidukr.software.vnf.server.endpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.amidukr.software.vnf.server.core.commandprocessor.CommandProcessor;
import org.amidukr.software.vnf.server.core.commandprocessor.EndpointConnection;

import javax.websocket.*;
import javax.websocket.server.ServerEndpointConfig;

/**
 * Created by Dmytro Brazhnyk on 6/17/2017.
 */
public class ProcessorWebSocketEndpoint extends Endpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessorWebSocketEndpoint.class);

    private static final String COMMAND_PROCESSOR = "commandProcessor";
    private static final String COMMAND_PROCESSOR_ENDPOINT_CONNECTION = "commandProcessorEndpointConnection";

    @Override
    public void onOpen(Session session, EndpointConfig config) {

        final CommandProcessor commandProcessor = (CommandProcessor) config.getUserProperties().get(COMMAND_PROCESSOR);
        final EndpointConnection endpointConnection = new WebSocketProcessorEndpointConnection(session.getAsyncRemote());

        session.getUserProperties().put(COMMAND_PROCESSOR, commandProcessor);
        session.getUserProperties().put(COMMAND_PROCESSOR_ENDPOINT_CONNECTION, endpointConnection);


        session.addMessageHandler(new MessageHandler.Whole<String>(){

            @Override
            public void onMessage(String message) {
                commandProcessor.remoteInvoke(endpointConnection, message);
            }
        });
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        CommandProcessor commandProcessor = (CommandProcessor) session.getUserProperties().get(COMMAND_PROCESSOR);
        EndpointConnection endpointConnection = (EndpointConnection) session.getUserProperties().get(COMMAND_PROCESSOR_ENDPOINT_CONNECTION);

        if(commandProcessor == null || endpointConnection == null) {
            LOGGER.warn("Session:onClose: user properties do not contains command processor or endpointConnection");
            LOGGER.warn("Session:onClose: skipping connection lost sequence");
            return;
        }

        commandProcessor.connectionLost(endpointConnection);
    }

    @Override
    public void onError(Session session, Throwable thr) {
        LOGGER.error("Session:onError", thr);
    }

    public static ServerEndpointConfig createEndpointConfig(CommandProcessor commandProcessor, String path) {
        ServerEndpointConfig endpointConfig = ServerEndpointConfig.Builder.create(ProcessorWebSocketEndpoint.class, path).build();
        endpointConfig.getUserProperties().put(COMMAND_PROCESSOR, commandProcessor);

        return endpointConfig;
    }
}
