package org.amidukr.software.vnf.server.endpoint.mock;

import javax.websocket.RemoteEndpoint;
import javax.websocket.SendHandler;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by Dmytro Brazhnyk on 6/17/2017.
 */
public class RemoteAsyncEndpointMock implements RemoteEndpoint.Async {

    private final List<String> capturedMessages = new ArrayList<>();

    @Override
    public Future<Void> sendText(String text) {
        capturedMessages.add(text);
        return null;
    }

    public List<String> getCapturedMessages() {
        return capturedMessages;
    }

    // Unsupported methods

    @Override
    public long getSendTimeout() {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public void setSendTimeout(long timeoutmillis) {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public void sendText(String text, SendHandler handler) {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public Future<Void> sendBinary(ByteBuffer data) {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public void sendBinary(ByteBuffer data, SendHandler handler) {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public Future<Void> sendObject(Object data) {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public void sendObject(Object data, SendHandler handler) {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public void setBatchingAllowed(boolean allowed) throws IOException {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public boolean getBatchingAllowed() {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public void flushBatch() throws IOException {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public void sendPing(ByteBuffer applicationData) throws IOException, IllegalArgumentException {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }

    @Override
    public void sendPong(ByteBuffer applicationData) throws IOException, IllegalArgumentException {
        throw new UnsupportedOperationException("Unsupported - mock object is used");
    }
}
