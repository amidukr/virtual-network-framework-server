package org.vnf.server.core.servicefactory;

import org.junit.Assert;
import org.junit.Test;
import org.vnf.server.core.commandprocessor.*;
import org.vnf.server.core.commonservice.CommonServiceConfiguration;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by qik on 6/9/2017.
 */
public class ServiceConfigurationFactoryTest {
    public class MockService {

        private final List<String> capturedMessages = new ArrayList<>();

        public List<String> getCapturedMessages() {
            return capturedMessages;
        }

        @Invoke("commandInvokeAuthDefault")
        public String invokeAuthDefault(CommandEvent event){
            return "result-invokeAuthDefault";
        }

        @Invoke(value = "commandInvokeAuthOnly", authorizationType = AuthorizationType.AUTHENTICATED_ONLY)
        public String invokeAuthOnly(CommandEvent event){
            return "result-invokeAuthOnly";
        }

        @Invoke(value = "commandInvokeAny", authorizationType = AuthorizationType.ANY)
        public String invokeAny(CommandEvent event){
            return "result-invokeAny-" + event.getCommandArgument();
        }

        @Invoke(value = "commandThrowRuntime", authorizationType = AuthorizationType.ANY)
        public String throwRuntime(CommandEvent event) {
            throw new RuntimeException("Runtime Exception");
        }

        @Invoke(value = "commandThrowCommandException", authorizationType = AuthorizationType.ANY)
        public String throwCommandException(CommandEvent event) throws CommandException {
            throw new CommandException("THROW_ERROR_REASON");
        }

        @OnConnectionLost
        public void onConnectionLostDefault(ConnectionLostEvent event){
            capturedMessages.add("onConnectionLostDefault: " + event.getEndpointId());
            throw new RuntimeException("Unexpected error");
        }

        @OnConnectionLost(authorizationType = AuthorizationType.AUTHENTICATED_ONLY)
        public void onConnectionLostAuth(ConnectionLostEvent event){
            capturedMessages.add("onConnectionLostAuth: " + event.getEndpointId());
            throw new RuntimeException("Unexpected error");
        }

        @OnConnectionLost(authorizationType = AuthorizationType.ANY)
        public void onConnectionLostAny(ConnectionLostEvent event){
            capturedMessages.add("onConnectionLostAny: " + event.getEndpointId());
        }
    }

    @Test
    public void testCollectedInvokeHandlers() {
        ServiceConfigurationFactory factory = new ServiceConfigurationFactory();

        factory.addService(new MockService());

        ServiceConfiguration serviceConfiguration = factory.create();


        List<MethodInvokeHandler> invokeHandlers = convertToList(MethodInvokeHandler.class, serviceConfiguration.getInvokeHandlers());

        Map<String, MethodInvokeHandler> invokeMap = invokeHandlers.stream().collect(Collectors.toMap(item -> item.getMethod().getName(), item -> item));

        List<String> methods = new ArrayList<>(invokeMap.keySet());
        methods.sort(Comparator.comparing(String::toString));

        Assert.assertEquals("Found methods",
                Arrays.asList("invokeAny", "invokeAuthDefault", "invokeAuthOnly", "throwCommandException", "throwRuntime"),
                new ArrayList(methods));

        Assert.assertEquals(AuthorizationType.AUTHENTICATED_ONLY, invokeMap.get("invokeAuthDefault").getAuthorizationType());
        Assert.assertEquals(AuthorizationType.AUTHENTICATED_ONLY, invokeMap.get("invokeAuthOnly").getAuthorizationType());
        Assert.assertEquals(AuthorizationType.ANY, invokeMap.get("invokeAny").getAuthorizationType());
        Assert.assertEquals(AuthorizationType.ANY, invokeMap.get("throwRuntime").getAuthorizationType());
        Assert.assertEquals(AuthorizationType.ANY, invokeMap.get("throwCommandException").getAuthorizationType());

        Assert.assertEquals("commandInvokeAuthDefault", invokeMap.get("invokeAuthDefault").getCommandName());
        Assert.assertEquals("commandInvokeAuthOnly", invokeMap.get("invokeAuthOnly").getCommandName());
        Assert.assertEquals("commandInvokeAny", invokeMap.get("invokeAny").getCommandName());
        Assert.assertEquals("commandThrowRuntime", invokeMap.get("throwRuntime").getCommandName());
        Assert.assertEquals("commandThrowCommandException", invokeMap.get("throwCommandException").getCommandName());
    }

    @Test
    public void testCollectedConnectionLostHandlers() {
        ServiceConfigurationFactory factory = new ServiceConfigurationFactory();

        factory.addService(new MockService());

        ServiceConfiguration serviceConfiguration = factory.create();



        List<MethodConnectionLostHandler> connectionLostHandlers = convertToList(MethodConnectionLostHandler.class, serviceConfiguration.getConnectionLostHandlers());

        Map<String, MethodConnectionLostHandler> handlerMap = connectionLostHandlers.stream().collect(Collectors.toMap(item -> item.getMethod().getName(), item -> item));

        List<String> methods = new ArrayList<>(handlerMap.keySet());
        methods.sort(Comparator.comparing(String::toString));

        Assert.assertEquals("Found methods",
                Arrays.asList("onConnectionLostAny", "onConnectionLostAuth", "onConnectionLostDefault"),
                methods);

        Assert.assertEquals(AuthorizationType.ANY, handlerMap.get("onConnectionLostAny").getAuthorizationType());
        Assert.assertEquals(AuthorizationType.AUTHENTICATED_ONLY, handlerMap.get("onConnectionLostAuth").getAuthorizationType());
        Assert.assertEquals(AuthorizationType.AUTHENTICATED_ONLY, handlerMap.get("onConnectionLostDefault").getAuthorizationType());
    }


    @Test(expected = ServiceConfigurationException.class)
    public void testInvokeReturnTypeInitializationException() {
        Object serviceObject = new Object() {
            @Invoke("invokeCommand")
            public void invokeCommand(CommandEvent event) {

            }
        };

        ServiceConfigurationFactory factory = new ServiceConfigurationFactory();

        try{
            factory.addService(serviceObject);
        }catch (ServiceConfigurationException e) {
            Assert.assertEquals("Method org.vnf.server.core.servicefactory.ServiceConfigurationFactoryTest$1::invokeCommand should return String", e.getMessage());
            throw e;
        }
    }

    @Test(expected = ServiceConfigurationException.class)
         public void testInvokeArgumentInitializationException() {
        Object serviceObject = new Object() {
            @Invoke("invokeCommand")
            public String invokeCommand(Object event) {
                return null;
            }
        };

        ServiceConfigurationFactory factory = new ServiceConfigurationFactory();

        try{
            factory.addService(serviceObject);
        }catch (ServiceConfigurationException e) {
            Assert.assertEquals("Method org.vnf.server.core.servicefactory.ServiceConfigurationFactoryTest$2::invokeCommand should have only one CommandEvent argument", e.getMessage());
            throw e;
        }

    }

    @Test(expected = ServiceConfigurationException.class)
    public void testConnectionLostArgumentInitializationException() {
        Object serviceObject = new Object() {
            @OnConnectionLost
            public void onConnectionLost(Object event) {}
        };

        ServiceConfigurationFactory factory = new ServiceConfigurationFactory();

        try{
            factory.addService(serviceObject);
        }catch (ServiceConfigurationException e) {
            Assert.assertEquals("Method org.vnf.server.core.servicefactory.ServiceConfigurationFactoryTest$3::onConnectionLost should have only one ConnectionLostEvent argument", e.getMessage());
            throw e;
        }
    }

    @Test
    public void testCommandEngineProcessorIntegration(){
        MockService serviceObject = new MockService();

        ServiceConfigurationFactory factory = new ServiceConfigurationFactory();
        factory.addService(serviceObject);

        CommandProcessor processor = new CommandProcessor();

        processor.addServiceHandlers(factory.create());

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        processor.remoteInvoke(endpointConnection, "1 commandInvokeAny\nargument");

        Assert.assertEquals(Arrays.asList("1 commandInvokeAny\nresult-invokeAny-argument"), endpointConnection.getCapturedMessages());
    }

    @Test
    public void testRuntimeExceptionIntercept(){
        MockService serviceObject = new MockService();

        ServiceConfigurationFactory factory = new ServiceConfigurationFactory();
        factory.addService(serviceObject);

        CommandProcessor processor = new CommandProcessor();

        processor.addServiceHandlers(factory.create());

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        processor.remoteInvoke(endpointConnection, "1 commandThrowRuntime\nargument");

        Assert.assertEquals(Arrays.asList("CALL_ERROR\n1 commandThrowRuntime\nCALL_FAILED_UNEXPECTED_EXCEPTION"), endpointConnection.getCapturedMessages());
    }

    @Test
    public void testCommandExceptionIntercept(){
        MockService serviceObject = new MockService();

        ServiceConfigurationFactory factory = new ServiceConfigurationFactory();
        factory.addService(serviceObject);

        CommandProcessor processor = new CommandProcessor();

        processor.addServiceHandlers(factory.create());

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        processor.remoteInvoke(endpointConnection, "1 commandThrowCommandException\nargument");

        Assert.assertEquals(Arrays.asList("CALL_ERROR\n1 commandThrowCommandException\nTHROW_ERROR_REASON"), endpointConnection.getCapturedMessages());
    }

    @Test
    public void testConnectionLost(){
        MockService serviceObject = new MockService();

        ServiceConfigurationFactory factory = new ServiceConfigurationFactory();
        factory.addService(serviceObject);

        CommandProcessor processor = new CommandProcessor();

        processor.addServiceHandlers(factory.create());
        processor.addServiceHandlers(new CommonServiceConfiguration());

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        processor.remoteInvoke(endpointConnection, "0 LOGIN\nendpoint");

        processor.connectionLost(endpointConnection);

        List<String> capturedMessages = serviceObject.getCapturedMessages();
        capturedMessages.sort(Comparator.comparing(String::toString));

        Assert.assertEquals(Arrays.asList("onConnectionLostAny: endpoint", "onConnectionLostAuth: endpoint", "onConnectionLostDefault: endpoint"), capturedMessages);
    }

    private static <T> List<T> convertToList(Class<T> clazz, Collection<? super T> sourceList) {
        List<T> result = new ArrayList<>();

        for (Object item : sourceList) {
            if(clazz.isAssignableFrom(item.getClass())) {
                result.add(clazz.cast(item));
            }else{
                throw new IllegalArgumentException("Unexpected type in source list: " + sourceList.getClass());
            }
        }

        return result;
    }
}
