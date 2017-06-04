package org.vnf.server;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.vnf.server.commandprocessor.*;
import org.vnf.server.commandprocessor.handlers.CommonServiceHandlers;

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
        public String handleCommand(CommandEvent event) {
            return "RESPONSE-TO-" + event.getCommandArgument() + "; endpointId = " + event.getEndpointId();
        }
    }
    
    @Test
    public void testMethodInvoke(){
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

        commandProcessor.addServiceHandlers(new CommonServiceHandlers());

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpointConnection, "0 LOGIN\nendpoint-1");

        Assert.assertEquals(endpointConnection.getCapturedMessages(), Arrays.asList("0 LOGIN\nOK"));
    }

    @Test
    public void testDoubleLoginRequest(){
        CommandProcessor commandProcessor = new CommandProcessor();

        commandProcessor.addServiceHandlers(new CommonServiceHandlers());

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
    public void testAuthorizationRequired(){
        CommandProcessor commandProcessor = new CommandProcessor();


        commandProcessor.addServiceHandlers(new CommonServiceHandlers());
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


        commandProcessor.addServiceHandlers(new CommonServiceHandlers());

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


        commandProcessor.addServiceHandlers(new CommonServiceHandlers());

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

        commandProcessor.addServiceHandlers(new CommonServiceHandlers());
        commandProcessor.addInvokeHandler(new InvokeHandler("COMMAND-NAME") {

            {
                setAuthorizationType(AuthorizationType.ANY);
            }

            @Override
            public String handleCommand(CommandEvent event) {
                endpointIdCaptor.capture(event.getEndpointId());
                commandArgumentCaptor.capture(event.getCommandArgument());
                commandProcessorCaptor.capture(event.getCommandProcessor());
                endpointConnectionCaptor.capture(event.getEndpointConnection());

                return "command-argument";
            }
        });

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpointConnection, "0 LOGIN\nendpoint-1");
        commandProcessor.remoteInvoke(endpointConnection, "1 COMMAND-NAME\ncommand-argument");

        commandProcessor.connectionLost(endpointConnection);


        Assert.assertEquals(endpointIdCaptor.getValue(), "endpoint-1", "Asserting endpoint id");
        Assert.assertEquals(commandArgumentCaptor.getValue(), "command-argument", "Asserting command argument");
        Assert.assertEquals(commandProcessorCaptor.getValue(), commandProcessor, "Asserting command processor");
        Assert.assertEquals(endpointConnectionCaptor.getValue(), endpointConnection, "Asserting endpoint connection");

        Assert.assertEquals(endpointConnection.getCapturedMessages(), Arrays.asList(
                "0 LOGIN\nOK",
                "1 COMMAND-NAME\ncommand-result"));
    }

    @Test
    public void testConnectionLost(){
        CommandProcessor commandProcessor = new CommandProcessor();

        commandProcessor.addServiceHandlers(new CommonServiceHandlers());

        final Captor<String> endpointIdCaptor = new Captor<>();

        commandProcessor.addConnectionLostHandler(new ConnectionLostHandler(){

            @Override
            public void onConnectionLost(ConnectionLostEvent connectionLostEvent) {
                endpointIdCaptor.capture(connectionLostEvent.getEndpointId());
            }
        });

        EndpointConnectionCaptor endpointConnection = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpointConnection, "0 LOGIN\nendpoint-1");

        commandProcessor.connectionLost(endpointConnection);

        Assert.assertEquals(endpointConnection.getCapturedMessages(), Arrays.asList("0 LOGIN\nOK"));
        Assert.assertEquals(endpointIdCaptor.getValue(), "endpoint-1", "Asserting endpoint id");
    }

    @Test
    public void testNullCommandArgument() {
        throw new UnsupportedOperationException("TODO:...");
    }

    @Test
    public void testNullCommandResponse() {
        throw new UnsupportedOperationException("TODO:...");
    }

    @Test
    public void testCheckedException() {
        CommandProcessor commandProcessor = new CommandProcessor();

        commandProcessor.addInvokeHandler(new InvokeHandler("THROW-EXCEPTION-COMMAND") {

            {
                setAuthorizationType(AuthorizationType.ANY);
            }

            @Override
            public String handleCommand(CommandEvent event) throws CommandException {
                throw new CommandException("TEST-EXCEPTION");
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

        commandProcessor.addInvokeHandler(new InvokeHandler("THROW-EXCEPTION-COMMAND") {

            {
                setAuthorizationType(AuthorizationType.ANY);
            }

            @Override
            public String handleCommand(CommandEvent event) {
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
