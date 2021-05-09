package org.amidukr.software.vnf.server.endpoint;

import org.amidukr.software.vnf.server.core.commandprocessor.EndpointConnection;

import javax.websocket.RemoteEndpoint;

/**
 * Created by Dmytro Brazhnyk on 6/17/2017.
 */
public class WebSocketProcessorEndpointConnection extends EndpointConnection {

    private final RemoteEndpoint.Async remoteAsyncEndpoint;

    public WebSocketProcessorEndpointConnection(RemoteEndpoint.Async remoteAsyncEndpoint) {
        this.remoteAsyncEndpoint = remoteAsyncEndpoint;
    }

    @Override
    protected void sendMessage(String message) {
        remoteAsyncEndpoint.sendText(message);
    }
}
