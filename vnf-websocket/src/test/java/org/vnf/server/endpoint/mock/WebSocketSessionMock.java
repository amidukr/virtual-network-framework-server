package org.vnf.server.endpoint.mock;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.*;

/**
 * Created by qik on 6/17/2017.
 */
public class WebSocketSessionMock implements Session {

    private final List<MessageHandler.Whole> wholeMessageHandlers = new ArrayList<>();
    private final RemoteAsyncEndpointMock asyncRemoteEndpoint = new RemoteAsyncEndpointMock();
    private final Map<String, Object> userProperties = new HashMap<>();

    public void fireMessage(String message) {
        for (MessageHandler.Whole wholeMessageHandler : wholeMessageHandlers) {
            wholeMessageHandler.onMessage(message);
        }
    }

    public List<String> getCapturedMessages() {
        return asyncRemoteEndpoint.getCapturedMessages();
    }

    @Override
    public void addMessageHandler(MessageHandler handler) throws IllegalStateException {
        if(handler instanceof MessageHandler.Whole) {
            wholeMessageHandlers.add((MessageHandler.Whole) handler);
        }
    }

    @Override
    public RemoteEndpoint.Async getAsyncRemote() {
        return asyncRemoteEndpoint;
    }

    @Override
    public Map<String, Object> getUserProperties() {
        return userProperties;
    }


    // Unsupported methods

    @Override
    public WebSocketContainer getContainer() {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public <T> void addMessageHandler(Class<T> clazz, MessageHandler.Whole<T> handler) {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public <T> void addMessageHandler(Class<T> clazz, MessageHandler.Partial<T> handler) {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public Set<MessageHandler> getMessageHandlers() {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public void removeMessageHandler(MessageHandler handler) {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public String getProtocolVersion() {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public String getNegotiatedSubprotocol() {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public List<Extension> getNegotiatedExtensions() {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public boolean isSecure() {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public boolean isOpen() {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public long getMaxIdleTimeout() {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public void setMaxIdleTimeout(long milliseconds) {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public void setMaxBinaryMessageBufferSize(int length) {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public int getMaxBinaryMessageBufferSize() {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public void setMaxTextMessageBufferSize(int length) {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public int getMaxTextMessageBufferSize() {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public RemoteEndpoint.Basic getBasicRemote() {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public String getId() {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public void close(CloseReason closeReason) throws IOException {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public URI getRequestURI() {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public Map<String, List<String>> getRequestParameterMap() {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public String getQueryString() {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public Map<String, String> getPathParameters() {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public Set<Session> getOpenSessions() {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }
}
