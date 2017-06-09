package org.vnf.server.core.servicefactory;

import org.vnf.server.core.commandprocessor.AuthorizationType;
import org.vnf.server.core.commandprocessor.CommandEvent;
import org.vnf.server.core.commandprocessor.CommandException;
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
    public String handleCommand(CommandEvent event) throws CommandException {
        try {
            return (String) method.invoke(target, event);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            if(e.getCause() instanceof CommandException) {
                throw (CommandException)e.getCause();
            }

            if(e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }

            throw new RuntimeException(e.getCause());
        }
    }
}
