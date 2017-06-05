package org.vnf.server.core.commandprocessor;

/**
 * Created by qik on 6/4/2017.
 */
public abstract class ConnectionLostHandler {
    private AuthorizationType authorizationType = AuthorizationType.AUTHENTICATED_ONLY;

    public AuthorizationType getAuthorizationType() {
        return authorizationType;
    }

    public void setAuthorizationType(AuthorizationType authorizationType) {
        this.authorizationType = authorizationType;
    }

    public abstract void onConnectionLost(ConnectionLostEvent connectionLostEvent);
}
