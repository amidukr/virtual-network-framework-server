package org.vnf.server.endpoint;

import org.junit.Assert;
import org.junit.Test;
import org.vnf.server.core.commandprocessor.AuthorizationType;
import org.vnf.server.core.commandprocessor.CommandProcessor;
import org.vnf.server.core.commandprocessor.ConnectionLostEvent;
import org.vnf.server.core.commandprocessor.ConnectionLostHandler;
import org.vnf.server.core.commonservice.CommonServiceHandlersConfiguration;
import org.vnf.server.endpoint.mock.WebSocketSessionMock;
import org.vnf.server.utils.Captor;

import javax.websocket.EndpointConfig;
import java.util.Arrays;

/**
 * Created by qik on 6/17/2017.
 */
public class ProcessorWebSocketEndpointTest {

    private final ProcessorWebSocketEndpoint processorWebSocketEndpoint = new ProcessorWebSocketEndpoint();

    @Test
    public void webSocketPingTest() {
        CommandProcessor commandProcessor = new CommandProcessor();

        commandProcessor.addServiceHandlers(new CommonServiceHandlersConfiguration());

        EndpointConfig endpointConfig = ProcessorWebSocketEndpoint.createEndpointConfig(commandProcessor, "/vnf-ws");

        WebSocketSessionMock session = new WebSocketSessionMock();

        processorWebSocketEndpoint.onOpen(session, endpointConfig);

        session.fireMessage("0 LOGIN\nendpoint-1");
        session.fireMessage("1 PING");

        Assert.assertEquals("Verifying retrieved messages", Arrays.asList("0 LOGIN\nOK", "1 PING"), session.getCapturedMessages());
    }

    @Test
    public void webSocketCloseTest() {
        CommandProcessor commandProcessor = new CommandProcessor();

        final Captor<Boolean> connectionLostCaptor = new Captor<>(false);

        commandProcessor.addConnectionLostHandler(new ConnectionLostHandler() {
            {
                setAuthorizationType(AuthorizationType.ANY);
            }

            @Override
            public void onConnectionLost(ConnectionLostEvent connectionLostEvent) {
                connectionLostCaptor.capture(true);
            }
        });

        EndpointConfig endpointConfig = ProcessorWebSocketEndpoint.createEndpointConfig(commandProcessor, "/vnf-ws");

        WebSocketSessionMock session = new WebSocketSessionMock();

        processorWebSocketEndpoint.onOpen(session, endpointConfig);
        processorWebSocketEndpoint.onClose(session, null);

        Assert.assertTrue("Verifying connection close captured", connectionLostCaptor.getValue());
    }
}
