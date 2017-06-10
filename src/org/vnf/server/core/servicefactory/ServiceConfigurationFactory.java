package org.vnf.server.core.servicefactory;

import org.vnf.server.core.commandprocessor.*;
import org.vnf.server.core.commonservice.CommonServiceConfiguration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qik on 6/9/2017.
 */
public class ServiceConfigurationFactory {

    private final List<InvokeHandler> invokeHandlers = new ArrayList<>();
    private final List<ConnectionLostHandler> connectionLostHandlers = new ArrayList<>();


    public ServiceConfiguration create(){
        return new ImmutableServiceConfiguration(invokeHandlers, connectionLostHandlers);
    }

    private void addMethodInvoke(Object target, Method method) {

        if(method.getReturnType() != String.class && method.getReturnType() != InvocationResult.class ) {
            throw new ServiceConfigurationException(String.format("Method %s::%s should return String or InvocationResult", method.getDeclaringClass().getName(), method.getName()));
        }

        if(method.getParameterCount() != 1 || method.getParameterTypes()[0] != CommandEvent.class) {
            throw new ServiceConfigurationException(String.format("Method %s::%s should have only one CommandEvent argument", method.getDeclaringClass().getName(), method.getName()));
        }

        Invoke invokeAnnotation = method.getAnnotation(Invoke.class);

        invokeHandlers.add(new MethodInvokeHandler(invokeAnnotation.value(), invokeAnnotation.authorizationType(),target, method));
    }

    private void addOnConnectionLost(Object target, Method method) {
        if(method.getParameterCount() != 1 || method.getParameterTypes()[0] != ConnectionLostEvent.class) {
            throw new ServiceConfigurationException(String.format("Method %s::%s should have only one ConnectionLostEvent argument", method.getDeclaringClass().getName(), method.getName()));
        }

        OnConnectionLost invokeAnnotation = method.getAnnotation(OnConnectionLost.class);

        connectionLostHandlers.add(new MethodConnectionLostHandler(invokeAnnotation.authorizationType(),target, method));
    }


    public void addService(Object serviceObject) {
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

    public static CommandProcessor createCommandProcessor(Object ... services) {
        CommandProcessor commandProcessor = new CommandProcessor();

        ServiceConfigurationFactory factory = new ServiceConfigurationFactory();

        for (Object service : services) {
            factory.addService(service);
        }

        commandProcessor.addServiceHandlers(new CommonServiceConfiguration());
        commandProcessor.addServiceHandlers(factory.create());

        return commandProcessor;
    }
}
