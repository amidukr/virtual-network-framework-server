package org.vnf.server.core.servicefactory;

import org.vnf.server.core.commandprocessor.AuthorizationType;
import org.vnf.server.core.commandprocessor.CommandEvent;
import org.vnf.server.core.commandprocessor.InvocationResult;
import org.vnf.server.core.commandprocessor.InvokeHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by qik on 6/9/2017.
 */
public class MethodInvokeHandler extends InvokeHandler{
    private final Object target;
    private final Method method;

    public MethodInvokeHandler(String commandName, AuthorizationType authorizationType, Object target, Method method) {
        super(commandName);
        this.target = target;
        this.method = method;

        setAuthorizationType(authorizationType);
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public InvocationResult handleCommand(CommandEvent event) {
        try {
            Object result = method.invoke(target, event);

            if(result instanceof String) {
                return InvocationResult.succeed((String)result);
            }

            return (InvocationResult) result;

        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
