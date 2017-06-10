package org.vnf.server.services;

import org.junit.Assert;
import org.junit.Test;
import org.vnf.server.core.commandprocessor.CommandProcessor;
import org.vnf.server.core.commandprocessor.EndpointConnectionCaptor;
import org.vnf.server.core.servicefactory.ServiceConfigurationFactory;

import java.util.Arrays;

/**
 * Created by qik on 6/10/2017.
 */
public class MessageBrokerServiceTest {

    @Test
    public void testSendToEndpointCommand() {
        CommandProcessor processor = ServiceConfigurationFactory.createCommandProcessor(new MessageBrokerService());

        EndpointConnectionCaptor endpointSender    = new EndpointConnectionCaptor();
        EndpointConnectionCaptor endpointRecipient = new EndpointConnectionCaptor();

        processor.remoteInvoke(endpointSender,    "0 LOGIN\nsender");
        processor.remoteInvoke(endpointRecipient, "0 LOGIN\nrecipient");

        processor.remoteInvoke(endpointSender, "1 SEND_TO_ENDPOINT\nrecipient\nmessage\nany\nlong");

        Assert.assertEquals("Verifying sender message log", Arrays.asList(
                        "0 LOGIN\nOK",
                        "1 SEND_TO_ENDPOINT"),
                endpointSender.getCapturedMessages());

        Assert.assertEquals("Verifying recipient message log", Arrays.asList(
                        "0 LOGIN\nOK",
                        "ENDPOINT_MESSAGE\nsender\nmessage\nany\nlong"),
                endpointRecipient.getCapturedMessages());
    }

    @Test
    public void testSendToSelf() {
        CommandProcessor processor = ServiceConfigurationFactory.createCommandProcessor(new MessageBrokerService());

        EndpointConnectionCaptor endpoint = new EndpointConnectionCaptor();

        processor.remoteInvoke(endpoint, "0 LOGIN\nendpoint");
        processor.remoteInvoke(endpoint, "1 SEND_TO_ENDPOINT\nendpoint\nmessage");

        Assert.assertEquals("Verifying endpoint message log", Arrays.asList(
                        "0 LOGIN\nOK",
                        "ENDPOINT_MESSAGE\nendpoint\nmessage",
                        "1 SEND_TO_ENDPOINT"),
                endpoint.getCapturedMessages());
    }

    @Test
    public void testMalformedMessage() {
        CommandProcessor processor = ServiceConfigurationFactory.createCommandProcessor(new MessageBrokerService());

        EndpointConnectionCaptor endpoint = new EndpointConnectionCaptor();

        processor.remoteInvoke(endpoint, "0 LOGIN\nendpoint");
        processor.remoteInvoke(endpoint, "1 SEND_TO_ENDPOINT");
        processor.remoteInvoke(endpoint, "2 SEND_TO_ENDPOINT\nrecipient only");

        Assert.assertEquals("Verifying endpoint message log", Arrays.asList(
                        "0 LOGIN\nOK",
                        "CALL_ERROR\n1 SEND_TO_ENDPOINT\nSEND_TO_ENDPOINT_ARGUMENT_CANNOT_BE_NULL",
                        "CALL_ERROR\n2 SEND_TO_ENDPOINT\nSEND_TO_ENDPOINT_MALFORMED_ARGUMENT"),
                endpoint.getCapturedMessages());
    }
}
