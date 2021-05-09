package org.amidukr.software.vnf.server.servicehandlers;

import org.junit.Assert;
import org.junit.Test;
import org.amidukr.software.vnf.server.VnfServiceHandlersFactory;
import org.amidukr.software.vnf.server.core.commandprocessor.CommandProcessor;
import org.amidukr.software.vnf.server.core.commandprocessor.EndpointConnectionCaptor;
import org.amidukr.software.vnf.server.core.commonservice.CommonServiceHandlersConfiguration;

import java.util.Arrays;

/**
 * Created by Dmytro Brazhnyk on 6/18/2017.
 */
public class VnfServiceHandlersFactoryTest {
    @Test
    public void testCommandList() {
        CommandProcessor processor = VnfServiceHandlersFactory.createCommandProcessor(
                new CommonServiceHandlersConfiguration(),
                new VnfServiceHandlersFactory().create());

        EndpointConnectionCaptor endpoint = new EndpointConnectionCaptor();

        processor.remoteInvoke(endpoint, "0 HELP\nno-header");

        Assert.assertEquals("Verifying endpoint message log", Arrays.asList(
                        "0 HELP\n" +
                        "CREATE-ENTRY\n" +
                        "CREATE-OR-UPDATE-ENTRY\n" +
                        "DELETE-ENTRY\n" +
                        "GET-ENTRIES-WITH-BODY\n" +
                        "GET-ENTRY\n" +
                        "LOGIN\n" +
                        "PING\n" +
                        "SEND_TO_ENDPOINT"),
                endpoint.getCapturedMessages());
    }
}
