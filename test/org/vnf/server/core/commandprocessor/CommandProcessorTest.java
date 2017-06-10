package org.vnf.server.core.commandprocessor;

import org.junit.Assert;
import org.junit.Test;
import org.vnf.server.Captor;
import org.vnf.server.core.commonservice.CommonServiceHandlersConfiguration;

import java.util.Arrays;

/**
 * Created by qik on 6/3/2017.
 */
public class CommandProcessorTest {

    private static class EchoHandler extends InvokeHandler {

        public EchoHandler(String commandName, AuthorizationType authorizationType) {
            super(commandName);

            setAuthorizationType(authorizationType);
        }

        @Override
        public InvocationResult handleCommand(CommandEvent event) {
            return InvocationResult.succeed("RESPONSE-TO-" + event.getCommandArgument() + "; endpointId = " + event.getEndpointId());
        }
    }

    private static class ConnectionLostCaptor extends ConnectionLostHandler {

        private final Captor<Boolean> eventFiredCaptor = new Captor<>(false);

        private final Captor<CommandProcessor> commandProcessorCaptor = new Captor<>();
        private final Captor<EndpointConnection> endpointConnectionCaptor = new Captor<>();
        private final Captor<String> endpointIdCaptor = new Captor<>();


        public ConnectionLostCaptor(AuthorizationType authorizationType) {
            setAuthorizationType(authorizationType);
        }

        public Boolean getEventFired() {
            return eventFiredCaptor.getValue();
        }

        public CommandProcessor getCapturedCommandProcessor() {
            return commandProcessorCaptor.getValue();
        }

        public EndpointConnection getCapturedEndpointConnection() {
            return endpointConnectionCaptor.getValue();
        }

        public String getCapturedEndpointId() {
            return endpointIdCaptor.getValue();
        }

        @Override
        public void onConnectionLost(ConnectionLostEvent connectionLostEvent) {
            eventFiredCaptor.capture(true);

            commandProcessorCaptor.capture(connectionLostEvent.getCommandProcessor());
            endpointConnectionCaptor.capture(connectionLostEvent.getEndpointConnection());
            endpointIdCaptor.capture(connectionLostEvent.getEndpointId());
        }
    }
    
    @Test
    public void testCommandInvoke(){
        CommandProcessor commandProcessor = new CommandProcessor();

        commandProcessor.addInvokeHandler(new EchoHandler("TEST-AUTH-ANY-COMMAND", AuthorizationType.ANY));

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpointConnection, "1 TEST-AUTH-ANY-COMMAND\nREQUEST-COMMAND");

        Assert.assertEquals(endpointConnection.getCapturedMessages(),
                Arrays.asList("1 TEST-AUTH-ANY-COMMAND\nRESPONSE-TO-REQUEST-COMMAND; endpointId = null"));
    }

    @Test
    public void testFailAuthorizationRequired(){
        CommandProcessor commandProcessor = new CommandProcessor();

        commandProcessor.addInvokeHandler(new EchoHandler("TEST-AUTHENTICATED-ONLY-COMMAND", AuthorizationType.AUTHENTICATED_ONLY));

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpointConnection, "1 TEST-AUTHENTICATED-ONLY-COMMAND\nREQUEST-COMMAND");
        Assert.assertEquals(endpointConnection.getCapturedMessages(),
                Arrays.asList("CALL_ERROR\n1 TEST-AUTHENTICATED-ONLY-COMMAND\nCALL_FAILED_AUTHENTICATION_REQUIRED"));
    }

    @Test
    public void testLoginRequest(){
        CommandProcessor commandProcessor = new CommandProcessor();

        commandProcessor.addServiceHandlers(new CommonServiceHandlersConfiguration());

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpointConnection, "0 LOGIN\nendpoint-1");

        Assert.assertEquals(endpointConnection.getCapturedMessages(), Arrays.asList("0 LOGIN\nOK"));
    }

    @Test
    public void testDoubleLoginRequest(){
        CommandProcessor commandProcessor = new CommandProcessor();

        commandProcessor.addServiceHandlers(new CommonServiceHandlersConfiguration());

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpointConnection, "0 LOGIN\nendpoint-1");
        commandProcessor.remoteInvoke(endpointConnection, "1 LOGIN\nendpoint-2");
        commandProcessor.remoteInvoke(endpointConnection, "2 LOGIN\nendpoint-1");

        Assert.assertEquals(endpointConnection.getCapturedMessages(), Arrays.asList(
                "0 LOGIN\nOK",
                "1 LOGIN\nNOT-OK-ALREADY-AUTHORIZED",
                "2 LOGIN\nOK"));
    }

    @Test
    public void testLoginInUse(){
        CommandProcessor commandProcessor = new CommandProcessor();

        commandProcessor.addServiceHandlers(new CommonServiceHandlersConfiguration());

        EndpointConnectionCaptor endpointConnection1 = new EndpointConnectionCaptor();
        EndpointConnectionCaptor endpointConnection2 = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpointConnection1, "0 LOGIN\nendpoint");
        commandProcessor.remoteInvoke(endpointConnection2, "0 LOGIN\nendpoint");

        Assert.assertEquals(endpointConnection1.getCapturedMessages(), Arrays.asList("0 LOGIN\nOK"));
        Assert.assertEquals(endpointConnection2.getCapturedMessages(), Arrays.asList("0 LOGIN\nNOT-OK-ALREADY-IN-USE"));
    }

    @Test
    public void testAuthorizationRequired(){
        CommandProcessor commandProcessor = new CommandProcessor();


        commandProcessor.addServiceHandlers(new CommonServiceHandlersConfiguration());
        commandProcessor.addInvokeHandler(new EchoHandler("TEST-AUTHENTICATED-ONLY-COMMAND", AuthorizationType.AUTHENTICATED_ONLY));

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpointConnection, "0 LOGIN\nendpoint-1");
        commandProcessor.remoteInvoke(endpointConnection, "1 TEST-AUTHENTICATED-ONLY-COMMAND\nREQUEST-COMMAND");

        Assert.assertEquals(endpointConnection.getCapturedMessages(), Arrays.asList(
                "0 LOGIN\nOK",
                "1 TEST-AUTHENTICATED-ONLY-COMMAND\nRESPONSE-TO-REQUEST-COMMAND; endpointId = endpoint-1"));
    }

    @Test
    public void testPingCommand(){
        CommandProcessor commandProcessor = new CommandProcessor();


        commandProcessor.addServiceHandlers(new CommonServiceHandlersConfiguration());

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpointConnection, "0 LOGIN\nendpoint-1");
        commandProcessor.remoteInvoke(endpointConnection, "1 PING");

        Assert.assertEquals(endpointConnection.getCapturedMessages(), Arrays.asList(
                "0 LOGIN\nOK",
                "1 PING"));
    }

    @Test
    public void testPush(){
        CommandProcessor commandProcessor = new CommandProcessor();


        commandProcessor.addServiceHandlers(new CommonServiceHandlersConfiguration());

        EndpointConnectionCaptor endpoint1 = new EndpointConnectionCaptor();
        EndpointConnectionCaptor endpoint2 = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpoint1, "0 LOGIN\nendpoint-1");
        commandProcessor.remoteInvoke(endpoint2, "0 LOGIN\nendpoint-2");

        commandProcessor.pushMessage("endpoint-1", "COMMAND-1", "message-for-endpoint1");
        commandProcessor.pushMessage("endpoint-2", "COMMAND-2", "message-for-endpoint2");

        Assert.assertEquals(endpoint1.getCapturedMessages(), Arrays.asList(
                "0 LOGIN\nOK",
                "COMMAND-1\nmessage-for-endpoint1"));

        Assert.assertEquals(endpoint2.getCapturedMessages(), Arrays.asList(
                "0 LOGIN\nOK",
                "COMMAND-2\nmessage-for-endpoint2"));
    }

    @Test
    public void testInvokeHandlerEvent(){
        CommandProcessor commandProcessor = new CommandProcessor();

        Captor<String> endpointIdCaptor = new Captor<>();
        Captor<String> commandArgumentCaptor = new Captor<>();
        Captor<CommandProcessor> commandProcessorCaptor = new Captor<>();
        Captor<EndpointConnection> endpointConnectionCaptor = new Captor<>();

        commandProcessor.addServiceHandlers(new CommonServiceHandlersConfiguration());
        commandProcessor.addInvokeHandler(new InvokeHandler("COMMAND-NAME") {

            {
                setAuthorizationType(AuthorizationType.ANY);
            }

            @Override
            public InvocationResult handleCommand(CommandEvent event) {
                endpointIdCaptor.capture(event.getEndpointId());
                commandArgumentCaptor.capture(event.getCommandArgument());
                commandProcessorCaptor.capture(event.getCommandProcessor());
                endpointConnectionCaptor.capture(event.getEndpointConnection());

                return InvocationResult.succeed("command-result");
            }
        });

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpointConnection, "0 LOGIN\nendpoint-1");
        commandProcessor.remoteInvoke(endpointConnection, "1 COMMAND-NAME\ncommand-argument");

        commandProcessor.connectionLost(endpointConnection);


        Assert.assertEquals("Asserting endpoint id", endpointIdCaptor.getValue(), "endpoint-1");
        Assert.assertEquals("Asserting command argument", commandArgumentCaptor.getValue(), "command-argument");
        Assert.assertEquals("Asserting command processor", commandProcessorCaptor.getValue(), commandProcessor);
        Assert.assertEquals("Asserting endpoint connection", endpointConnectionCaptor.getValue(), endpointConnection);

        Assert.assertEquals(endpointConnection.getCapturedMessages(), Arrays.asList(
                "0 LOGIN\nOK",
                "1 COMMAND-NAME\ncommand-result"));
    }

    @Test
    public void testConnectionLost(){
        CommandProcessor commandProcessor = new CommandProcessor();

        commandProcessor.addServiceHandlers(new CommonServiceHandlersConfiguration());

        final Captor<CommandProcessor> commandProcessorCaptor = new Captor<>();
        final Captor<EndpointConnection> endpointConnectionCaptor = new Captor<>();
        final Captor<String> endpointIdCaptor = new Captor<>();

        ConnectionLostCaptor handlerCaptor = new ConnectionLostCaptor(AuthorizationType.AUTHENTICATED_ONLY);

        commandProcessor.addConnectionLostHandler(handlerCaptor);

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpointConnection, "0 LOGIN\nendpoint-1");

        commandProcessor.connectionLost(endpointConnection);

        Assert.assertEquals(endpointConnection.getCapturedMessages(), Arrays.asList("0 LOGIN\nOK"));
        Assert.assertTrue("Verifying is handler captured event", handlerCaptor.getEventFired());
        Assert.assertEquals("Asserting event command processor", handlerCaptor.getCapturedCommandProcessor(), commandProcessor);
        Assert.assertEquals("Asserting endpoint connection captor", handlerCaptor.getCapturedEndpointConnection(), endpointConnection);
        Assert.assertEquals("Asserting endpoint id", handlerCaptor.getCapturedEndpointId(), "endpoint-1");
    }

    @Test
    public void testConnectionLostAuthType(){
        CommandProcessor commandProcessor = new CommandProcessor();

        ConnectionLostCaptor handlerAuthRequired = new ConnectionLostCaptor(AuthorizationType.AUTHENTICATED_ONLY);
        ConnectionLostCaptor handlerAuthNonRequired = new ConnectionLostCaptor(AuthorizationType.ANY);

        commandProcessor.addConnectionLostHandler(handlerAuthRequired);
        commandProcessor.addConnectionLostHandler(handlerAuthNonRequired);

        commandProcessor.connectionLost(new EndpointConnectionCaptor());

        Assert.assertFalse("Verifying auth required handler haven't caught event", handlerAuthRequired.getEventFired());
        Assert.assertTrue("Verifying auth non-required handler have caught event", handlerAuthNonRequired.getEventFired());
    }

    @Test
    public void testNullCommandArgument() {
        CommandProcessor commandProcessor = new CommandProcessor();

        final Captor<String> commandArgumentCaptor = new Captor<>();

        commandProcessor.addInvokeHandler(new InvokeHandler("TEST-COMMAND") {

            {
                setAuthorizationType(AuthorizationType.ANY);
            }

            @Override
            public InvocationResult handleCommand(CommandEvent event)  {
                commandArgumentCaptor.capture(event.getCommandArgument());

                return InvocationResult.succeed("response");
            }
        });

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpointConnection, "1 TEST-COMMAND");

        Assert.assertEquals("Verifying response", Arrays.asList("1 TEST-COMMAND\nresponse"), endpointConnection.getCapturedMessages());
        Assert.assertNull("Verifying null command argument", commandArgumentCaptor.getValue());
    }

    @Test
    public void testNullCommandResponse() {
        CommandProcessor commandProcessor = new CommandProcessor();

        final Captor<String> commandArgumentCaptor = new Captor<>();

        commandProcessor.addInvokeHandler(new InvokeHandler("TEST-COMMAND") {

            {
                setAuthorizationType(AuthorizationType.ANY);
            }

            @Override
            public InvocationResult handleCommand(CommandEvent event) {
                commandArgumentCaptor.capture(event.getCommandArgument());

                return null;
            }
        });

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpointConnection, "1 TEST-COMMAND\ncommand-argument");

        Assert.assertEquals("Verifying response", Arrays.asList("1 TEST-COMMAND"), endpointConnection.getCapturedMessages());
        Assert.assertEquals("Verifying command argument", commandArgumentCaptor.getValue(), "command-argument");
    }

    @Test
    public void testPushNull() {

        CommandProcessor commandProcessor = new CommandProcessor();

        commandProcessor.addServiceHandlers(new CommonServiceHandlersConfiguration());

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpointConnection, "0 LOGIN\nendpoint-1");

        commandProcessor.pushMessage("endpoint-1", "PUSH-COMMAND", null);

        Assert.assertEquals("Verifying response", Arrays.asList("0 LOGIN\nOK", "PUSH-COMMAND"), endpointConnection.getCapturedMessages());
    }

    @Test
    public void testMalformedHeader() {
        CommandProcessor commandProcessor = new CommandProcessor();

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpointConnection, "TEST-COMMAND\ncommand-argument");

        Assert.assertEquals(endpointConnection.getCapturedMessages(), Arrays.asList(
                "CALL_ERROR\nTEST-COMMAND\nCALL_FAILED_COMMAND_HEADER_MALFORMED"));
    }

    @Test
    public void testCheckedException() {
        CommandProcessor commandProcessor = new CommandProcessor();

        commandProcessor.addInvokeHandler(new InvokeHandler("TEST-COMMAND") {

            {
                setAuthorizationType(AuthorizationType.ANY);
            }

            @Override
            public InvocationResult handleCommand(CommandEvent event) {
                return InvocationResult.failed("TEST-EXCEPTION");
            }
        });

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpointConnection, "1 TEST-COMMAND\ncommand-argument");

        Assert.assertEquals(endpointConnection.getCapturedMessages(), Arrays.asList(
                "CALL_ERROR\n1 TEST-COMMAND\nTEST-EXCEPTION"));
    }

    @Test
    public void testRuntimeException() {
        CommandProcessor commandProcessor = new CommandProcessor();

        commandProcessor.addInvokeHandler(new InvokeHandler("TEST-COMMAND") {

            {
                setAuthorizationType(AuthorizationType.ANY);
            }

            @Override
            public InvocationResult handleCommand(CommandEvent event) {
                throw new RuntimeException("TEST-EXCEPTION");
            }
        });

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpointConnection, "1 TEST-COMMAND\ncommand-argument");

        Assert.assertEquals(endpointConnection.getCapturedMessages(), Arrays.asList(
                "CALL_ERROR\n1 TEST-COMMAND\nCALL_FAILED_UNEXPECTED_EXCEPTION"));
    }

    @Test
    public void testUnknownFailCommand() {
        CommandProcessor commandProcessor = new CommandProcessor();

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpointConnection, "1 TEST-COMMAND\ncommand-argument");

        Assert.assertEquals(endpointConnection.getCapturedMessages(), Arrays.asList(
                "CALL_ERROR\n1 TEST-COMMAND\nCALL_FAILED_UNKNOWN_METHOD"));
    }
}
