package org.vnf.server.commandprocessor;

import java.util.List;

/**
 * Created by qik on 6/3/2017.
 */
public class EndpointConnectionCaptor extends EndpointConnection {
    private List<String> responseList;

    public List<String> getCapturedMessages() {
        return responseList;
    }
}
