package org.amidukr.software.vnf.server.core.commandprocessor;

/**
 * Created by Dmytro Brazhnyk on 6/3/2017.
 */
public abstract class EndpointConnection {

    private boolean authenticated;
    private String endpointId;

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getEndpointId() {
        return endpointId;
    }

    void authenticate(String endpointId) {
        authenticated = true;
        this.endpointId = endpointId;
    }

    protected abstract void sendMessage(String message);
}
