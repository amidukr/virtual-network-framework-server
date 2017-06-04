package org.vnf.server.commandprocessor;

/**
 * Created by qik on 6/4/2017.
 */
public abstract class ConnectionLostHandler {
    public abstract void onConnectionLost(ConnectionLostEvent connectionLostEvent);
}
