package org.vnf.server.core.commandprocessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qik on 6/3/2017.
 */
public class EndpointConnectionCaptor extends EndpointConnection {
    private final List<String> messages = new ArrayList<>();

    public List<String> getCapturedMessages() {
        return messages;
    }

    @Override
    protected void sendMessage(String message) {
        messages.add(message);
    }
}
