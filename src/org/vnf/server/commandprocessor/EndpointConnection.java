package org.vnf.server.commandprocessor;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Created by qik on 6/3/2017.
 */
public class EndpointConnection {

    private boolean authenticated;
    private String endpointId;

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getEndpointId() {
        return endpointId;
    }
}
