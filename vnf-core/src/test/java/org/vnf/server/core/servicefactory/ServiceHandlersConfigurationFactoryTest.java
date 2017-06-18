package org.vnf.server.core.servicefactory;

import org.junit.Assert;
import org.junit.Test;
import org.vnf.server.core.commandprocessor.*;
import org.vnf.server.core.commonservice.CommonServiceHandlersConfiguration;
import org.vnf.server.mocks.ConnectionLostCaptor;
import org.vnf.server.mocks.EchoHandler;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

/**
 * Created by qik on 6/9/2017.
 */
public class ServiceHandlersConfigurationFactoryTest {

    private static class MockService implements ServiceObject{

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
            throw new RuntimeException("TEST-RUNTIME-EXCEPTION");
        }

        @Invoke(value = "commandThrowCommandException", authorizationType = AuthorizationType.ANY)
        public InvocationResult throwCommandException(CommandEvent event) {
            return InvocationResult.failed("THROW_ERROR_REASON");
        }

        @OnConnectionLost
        public void onConnectionLostDefault(ConnectionLostEvent event){
            capturedMessages.add("onConnectionLostDefault: " + event.getEndpointId());
            throw new RuntimeException("TEST-EXCEPTION");
        }

        @OnConnectionLost(authorizationType = AuthorizationType.AUTHENTICATED_ONLY)
        public void onConnectionLostAuth(ConnectionLostEvent event){
            capturedMessages.add("onConnectionLostAuth: " + event.getEndpointId());
            throw new RuntimeException("TEST-EXCEPTION");
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

        ServiceHandlersConfiguration serviceHandlersConfiguration = factory.create();


        List<MethodInvokeHandler> invokeHandlers = convertToList(MethodInvokeHandler.class, serviceHandlersConfiguration.getInvokeHandlers());

        Map<String, MethodInvokeHandler> invokeMap = invokeHandlers.stream().collect(Collectors.toMap(item -> item.getMethod().getName(), item -> item));

        List<String> methods = new ArrayList<>(invokeMap.keySet());
        methods.sort(Comparator.comparing(String::toString));

        Assert.assertEquals("Found methods",
                asList("invokeAny", "invokeAuthDefault", "invokeAuthOnly", "throwCommandException", "throwRuntime"),
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

        ServiceHandlersConfiguration serviceHandlersConfiguration = factory.create();



        List<MethodConnectionLostHandler> connectionLostHandlers = convertToList(MethodConnectionLostHandler.class, serviceHandlersConfiguration.getConnectionLostHandlers());

        Map<String, MethodConnectionLostHandler> handlerMap = connectionLostHandlers.stream().collect(Collectors.toMap(item -> item.getMethod().getName(), item -> item));

        List<String> methods = new ArrayList<>(handlerMap.keySet());
        methods.sort(Comparator.comparing(String::toString));

        Assert.assertEquals("Found methods",
                asList("onConnectionLostAny", "onConnectionLostAuth", "onConnectionLostDefault"),
                methods);

        Assert.assertEquals(AuthorizationType.ANY, handlerMap.get("onConnectionLostAny").getAuthorizationType());
        Assert.assertEquals(AuthorizationType.AUTHENTICATED_ONLY, handlerMap.get("onConnectionLostAuth").getAuthorizationType());
        Assert.assertEquals(AuthorizationType.AUTHENTICATED_ONLY, handlerMap.get("onConnectionLostDefault").getAuthorizationType());
    }


    @Test(expected = ServiceConfigurationException.class)
    public void testInvokeReturnTypeInitializationException() {
        ServiceObject serviceObject = new ServiceObject() {
            @Invoke("invokeCommand")
            public void invokeCommand(CommandEvent event) {

            }
        };

        ServiceConfigurationFactory factory = new ServiceConfigurationFactory();

        try{
            factory.addService(serviceObject);
        }catch (ServiceConfigurationException e) {
            Assert.assertEquals("Method org.vnf.server.core.servicefactory.ServiceHandlersConfigurationFactoryTest$1::invokeCommand should return String or InvocationResult", e.getMessage());
            throw e;
        }
    }

    @Test(expected = ServiceConfigurationException.class)
         public void testInvokeArgumentInitializationException() {
        ServiceObject serviceObject = new ServiceObject() {
            @Invoke("invokeCommand")
            public String invokeCommand(Object event) {
                return null;
            }
        };

        ServiceConfigurationFactory factory = new ServiceConfigurationFactory();

        try{
            factory.addService(serviceObject);
        }catch (ServiceConfigurationException e) {
            Assert.assertEquals("Method org.vnf.server.core.servicefactory.ServiceHandlersConfigurationFactoryTest$2::invokeCommand should have only one CommandEvent argument", e.getMessage());
            throw e;
        }

    }

    @Test(expected = ServiceConfigurationException.class)
    public void testConnectionLostArgumentInitializationException() {
        ServiceObject serviceObject = new ServiceObject() {
            @OnConnectionLost
            public void onConnectionLost(Object event) {}
        };

        ServiceConfigurationFactory factory = new ServiceConfigurationFactory();

        try{
            factory.addService(serviceObject);
        }catch (ServiceConfigurationException e) {
            Assert.assertEquals("Method org.vnf.server.core.servicefactory.ServiceHandlersConfigurationFactoryTest$3::onConnectionLost should have only one ConnectionLostEvent argument", e.getMessage());
            throw e;
        }
    }

    @Test
    public void testAddHandler() {
        ServiceObject mockService = new ServiceObject(){
            @Invoke("echo-handler-3")
            public String echoHandler3(CommandEvent event) {
                return null;
            }
        };

        ServiceConfigurationFactory factory = new ServiceConfigurationFactory();

        factory.addHandler(new EchoHandler("echo-handler-1", AuthorizationType.ANY));
        factory.addHandler(new ConnectionLostCaptor("connection-lost-1", AuthorizationType.ANY));

        factory.addHandler(new ImmutableHandlersConfiguration(
                asList(new EchoHandler("echo-handler-2", AuthorizationType.ANY)),
                asList(new ConnectionLostCaptor("connection-lost-2", AuthorizationType.ANY))));

        factory.addHandler(mockService);

        ServiceHandlersConfiguration serviceHandlersConfiguration = factory.create();

        List<String> invokeHandlers = serviceHandlersConfiguration.getInvokeHandlers().stream()
                .map(InvokeHandler::getCommandName)
                .sorted()
                .collect(Collectors.toList());

        List<String> connectionLostHandlers = serviceHandlersConfiguration.getConnectionLostHandlers().stream()
                .map(x -> ((ConnectionLostCaptor) x).getHandlerId())
                .sorted()
                .collect(Collectors.toList());

        Assert.assertEquals("Verifying invoke handlers list", Arrays.asList("echo-handler-1", "echo-handler-2", "echo-handler-3"), invokeHandlers);
        Assert.assertEquals("Verifying connection lost handlers list", Arrays.asList("connection-lost-1", "connection-lost-2"), connectionLostHandlers);
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

        Assert.assertEquals(asList("1 commandInvokeAny\nresult-invokeAny-argument"), endpointConnection.getCapturedMessages());
    }

    @Test
    public void tesFactoryProcessorCreateMethod(){
        CommandProcessor processor = ServiceConfigurationFactory.createCommandProcessor(new MockService());

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        processor.remoteInvoke(endpointConnection, "1 commandInvokeAny\nargument");

        Assert.assertEquals(asList("1 commandInvokeAny\nresult-invokeAny-argument"), endpointConnection.getCapturedMessages());
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

        Assert.assertEquals(asList("CALL_ERROR\n1 commandThrowRuntime\nCALL_FAILED_UNEXPECTED_EXCEPTION"), endpointConnection.getCapturedMessages());
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

        Assert.assertEquals(asList("CALL_ERROR\n1 commandThrowCommandException\nTHROW_ERROR_REASON"), endpointConnection.getCapturedMessages());
    }

    @Test
    public void testConnectionLost(){
        MockService serviceObject = new MockService();

        ServiceConfigurationFactory factory = new ServiceConfigurationFactory();
        factory.addService(serviceObject);

        CommandProcessor processor = new CommandProcessor();

        processor.addServiceHandlers(factory.create());
        processor.addServiceHandlers(new CommonServiceHandlersConfiguration());

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        processor.remoteInvoke(endpointConnection, "0 LOGIN\nendpoint");

        processor.connectionLost(endpointConnection);

        List<String> capturedMessages = serviceObject.getCapturedMessages();
        capturedMessages.sort(Comparator.comparing(String::toString));

        Assert.assertEquals(asList("onConnectionLostAny: endpoint", "onConnectionLostAuth: endpoint", "onConnectionLostDefault: endpoint"), capturedMessages);
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
