package org.vnf.server.core.servicefactory;

import org.vnf.server.core.commandprocessor.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.vnf.server.utils.CollectionUtils.emptyIfNull;

/**
 * Created by qik on 6/9/2017.
 */
public class ServiceConfigurationFactory {

    private final List<InvokeHandler> invokeHandlers = new ArrayList<>();
    private final List<ConnectionLostHandler> connectionLostHandlers = new ArrayList<>();


    public ServiceHandlersConfiguration create(){
        return new ImmutableHandlersConfiguration(invokeHandlers, connectionLostHandlers);
    }

    public void addInvokeHandler(InvokeHandler invokeHandler) {
        invokeHandlers.add(invokeHandler);
    }

    public void addConnectionLostHandler(ConnectionLostHandler connectionLostHandler) {
        connectionLostHandlers.add(connectionLostHandler);
    }

    private void addMethodInvoke(Object target, Method method) {

        if(method.getReturnType() != String.class && method.getReturnType() != InvocationResult.class ) {
            throw new ServiceConfigurationException(String.format("Method %s::%s should return String or InvocationResult", method.getDeclaringClass().getName(), method.getName()));
        }

        if(method.getParameterCount() != 1 || method.getParameterTypes()[0] != CommandEvent.class) {
            throw new ServiceConfigurationException(String.format("Method %s::%s should have only one CommandEvent argument", method.getDeclaringClass().getName(), method.getName()));
        }

        Invoke invokeAnnotation = method.getAnnotation(Invoke.class);

        addInvokeHandler(new MethodInvokeHandler(invokeAnnotation.value(), invokeAnnotation.authorizationType(), target, method));
    }

    private void addOnConnectionLost(Object target, Method method) {
        if(method.getParameterCount() != 1 || method.getParameterTypes()[0] != ConnectionLostEvent.class) {
            throw new ServiceConfigurationException(String.format("Method %s::%s should have only one ConnectionLostEvent argument", method.getDeclaringClass().getName(), method.getName()));
        }

        OnConnectionLost invokeAnnotation = method.getAnnotation(OnConnectionLost.class);

        addConnectionLostHandler(new MethodConnectionLostHandler(invokeAnnotation.authorizationType(),target, method));
    }


    public void addService(ServiceObject serviceObject) {
        Method[] methods = serviceObject.getClass().getMethods();

        for (Method method : methods) {
            if(method.isAnnotationPresent(Invoke.class)) {
                addMethodInvoke(serviceObject, method);
            }

            if(method.isAnnotationPresent(OnConnectionLost.class)) {
                addOnConnectionLost(serviceObject, method);
            }
        }
    }

    public void addServiceHandlersConfiguration(ServiceHandlersConfiguration serviceHandlersConfiguration) {
        Collection<InvokeHandler> invokeHandlers = emptyIfNull(serviceHandlersConfiguration.getInvokeHandlers());
        Collection<ConnectionLostHandler> connectionLostHandlers = emptyIfNull(serviceHandlersConfiguration.getConnectionLostHandlers());

        for (InvokeHandler invokeHandler : invokeHandlers) {
            addInvokeHandler(invokeHandler);
        }

        for (ConnectionLostHandler connectionLostHandler : connectionLostHandlers) {
            addConnectionLostHandler(connectionLostHandler);
        }
    }

    public void addHandler(Object handler) {
        boolean typeExpected = false;

        if(handler instanceof ServiceHandlersConfiguration) {
            addServiceHandlersConfiguration((ServiceHandlersConfiguration) handler);
            typeExpected = true;
        }

        if(handler instanceof InvokeHandler) {
            addInvokeHandler((InvokeHandler) handler);
            typeExpected = true;
        }

        if(handler instanceof ConnectionLostHandler) {
            addConnectionLostHandler((ConnectionLostHandler) handler);
            typeExpected = true;
        }

        if(handler instanceof ServiceObject) {
            addService((ServiceObject) handler);
            typeExpected = true;
        }

        if(!typeExpected) {
            throw new RuntimeException("Handler type should be one of following types: " +
                    "ServiceObject, ServiceHandlersConfiguration, InvokeHandler, ConnectionLostHandler");
        }
    }

    public static CommandProcessor createCommandProcessor(Object ... handlers) {
        CommandProcessor commandProcessor = new CommandProcessor();

        ServiceConfigurationFactory factory = new ServiceConfigurationFactory();

        for (Object service : handlers) {
            factory.addHandler(service);
        }

        commandProcessor.addServiceHandlers(factory.create());

        return commandProcessor;
    }
}
