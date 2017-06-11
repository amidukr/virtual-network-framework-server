package org.vnf.server.core.servicefactory;

import org.vnf.server.core.commandprocessor.AuthorizationType;
import org.vnf.server.core.commandprocessor.ConnectionLostEvent;
import org.vnf.server.core.commandprocessor.ConnectionLostHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by qik on 6/9/2017.
 */
public class MethodConnectionLostHandler extends ConnectionLostHandler{

    private final Object target;
    private final Method method;


    public MethodConnectionLostHandler(AuthorizationType authorizationType, Object target, Method method) {
        this.target = target;
        this.method = method;

        setAuthorizationType(authorizationType);
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public void onConnectionLost(ConnectionLostEvent connectionLostEvent) {
        try {
            method.invoke(target, connectionLostEvent);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
