package org.vnf.server.servicehandlers;

import org.junit.Assert;
import org.junit.Test;
import org.vnf.server.core.commandprocessor.CommandProcessor;
import org.vnf.server.core.commandprocessor.EndpointConnectionCaptor;
import org.vnf.server.core.commonservice.CommonServiceHandlersConfiguration;
import org.vnf.server.core.servicefactory.ServiceConfigurationFactory;
import org.vnf.server.repository.StoreRepository;

import java.util.Arrays;

/**
 * Created by qik on 6/10/2017.
 */
public class StoreServiceHandlersTest {

    private CommandProcessor createCommandProcessor() {
        return ServiceConfigurationFactory.createCommandProcessor(new CommonServiceHandlersConfiguration(), new StoreServiceHandlers());
    }


    @Test
    public void testCreateAndGet() {
        CommandProcessor commandProcessor = createCommandProcessor();

        EndpointConnectionCaptor endpoint = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpoint, "0 LOGIN\nendpoint");

        commandProcessor.remoteInvoke(endpoint, "1 CREATE-ENTRY\ncollection1\nentry-name1\nentry\nvalue\nany\nlong");
        commandProcessor.remoteInvoke(endpoint, "2 GET-ENTRY\ncollection1\nentry-name1");

        Assert.assertEquals("Verifying retrieved messages", Arrays.asList(
                        "0 LOGIN\nOK",
                        "1 CREATE-ENTRY\nOK",
                        "2 GET-ENTRY\nentry\nvalue\nany\nlong"),
                endpoint.getCapturedMessages());
    }

    @Test
    public void testCreateOrUpdate() {
        CommandProcessor commandProcessor = createCommandProcessor();

        EndpointConnectionCaptor endpoint = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpoint, "0 LOGIN\nendpoint");

        commandProcessor.remoteInvoke(endpoint, "1 CREATE-ENTRY\ncollection1\nentry-name1\nentry-value1");
        commandProcessor.remoteInvoke(endpoint, "2 CREATE-OR-UPDATE-ENTRY\ncollection1\nentry-name1\nentry-value2");
        commandProcessor.remoteInvoke(endpoint, "3 GET-ENTRY\ncollection1\nentry-name1");

        Assert.assertEquals("Verifying retrieved messages", Arrays.asList(
                        "0 LOGIN\nOK",
                        "1 CREATE-ENTRY\nOK",
                        "2 CREATE-OR-UPDATE-ENTRY\nOK",
                        "3 GET-ENTRY\nentry-value2"),
                endpoint.getCapturedMessages());
    }

    @Test
    public void testCreateAndDelete() {
        CommandProcessor commandProcessor = createCommandProcessor();

        EndpointConnectionCaptor endpoint = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpoint, "0 LOGIN\nendpoint");

        commandProcessor.remoteInvoke(endpoint, "1 CREATE-ENTRY\ncollection1\nentry-name1\nentry-value1");
        commandProcessor.remoteInvoke(endpoint, "2 DELETE-ENTRY\ncollection1\nentry-name1");
        commandProcessor.remoteInvoke(endpoint, "3 GET-ENTRY\ncollection1\nentry-name1");
        commandProcessor.remoteInvoke(endpoint, "4 DELETE-ENTRY\ncollection1\nentry-name1");

        Assert.assertEquals("Verifying retrieved messages", Arrays.asList(
                        "0 LOGIN\nOK",
                        "1 CREATE-ENTRY\nOK",
                        "2 DELETE-ENTRY\nOK",
                        "CALL_ERROR\n3 GET-ENTRY\nGET_FAILED_ENTRY_NOT_FOUND",
                        "CALL_ERROR\n4 DELETE-ENTRY\nDELETE_FAILED_ENTRY_NOT_FOUND"),
                endpoint.getCapturedMessages());
    }

    @Test
    public void testGetFailedEntryNotFound() {
        CommandProcessor commandProcessor = createCommandProcessor();

        EndpointConnectionCaptor endpoint = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpoint, "0 LOGIN\nendpoint");

        commandProcessor.remoteInvoke(endpoint, "1 GET-ENTRY\ncollection1\nentry-name1");

        Assert.assertEquals("Verifying retrieved messages", Arrays.asList(
                        "0 LOGIN\nOK",
                        "CALL_ERROR\n1 GET-ENTRY\nGET_FAILED_ENTRY_NOT_FOUND"),
                endpoint.getCapturedMessages());
    }

    @Test
    public void testCreateFailedEntryAlreadyExists() {
        CommandProcessor commandProcessor = createCommandProcessor();

        EndpointConnectionCaptor endpoint = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpoint, "0 LOGIN\nendpoint");

        commandProcessor.remoteInvoke(endpoint, "1 CREATE-ENTRY\ncollection1\nentry-name1\nentry-value1");
        commandProcessor.remoteInvoke(endpoint, "2 CREATE-ENTRY\ncollection1\nentry-name1\nentry-value2");
        commandProcessor.remoteInvoke(endpoint, "3 GET-ENTRY\ncollection1\nentry-name1");

        Assert.assertEquals("Verifying retrieved messages", Arrays.asList(
                        "0 LOGIN\nOK",
                        "1 CREATE-ENTRY\nOK",
                        "CALL_ERROR\n2 CREATE-ENTRY\nCREATE_FAILED_ENTRY_ALREADY_EXISTS",
                        "3 GET-ENTRY\nentry-value1"),
                endpoint.getCapturedMessages());
    }

    @Test
    public void testDeleteFailedEntryNotExistYet() {
        CommandProcessor commandProcessor = createCommandProcessor();

        EndpointConnectionCaptor endpoint = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpoint, "0 LOGIN\nendpoint");

        commandProcessor.remoteInvoke(endpoint, "1 DELETE-ENTRY\ncollection1\nentry-name1");

        Assert.assertEquals("Verifying retrieved messages", Arrays.asList(
                        "0 LOGIN\nOK",
                        "CALL_ERROR\n1 DELETE-ENTRY\nDELETE_FAILED_ENTRY_NOT_FOUND"),
                endpoint.getCapturedMessages());
    }

    @Test
    public void testCreateAndDeleteMultipleTime() {
        CommandProcessor commandProcessor = createCommandProcessor();

        EndpointConnectionCaptor endpoint = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpoint, "0 LOGIN\nendpoint");

        commandProcessor.remoteInvoke(endpoint, "1 CREATE-ENTRY\ncollection1\nentry-name1\nentry-value1");
        commandProcessor.remoteInvoke(endpoint, "2 GET-ENTRY\ncollection1\nentry-name1");
        commandProcessor.remoteInvoke(endpoint, "3 DELETE-ENTRY\ncollection1\nentry-name1");
        commandProcessor.remoteInvoke(endpoint, "4 GET-ENTRY\ncollection1\nentry-name1");
        commandProcessor.remoteInvoke(endpoint, "5 DELETE-ENTRY\ncollection1\nentry-name1");

        commandProcessor.remoteInvoke(endpoint, "6 CREATE-ENTRY\ncollection1\nentry-name1\nentry-value1");
        commandProcessor.remoteInvoke(endpoint, "7 DELETE-ENTRY\ncollection1\nentry-name1");
        commandProcessor.remoteInvoke(endpoint, "8 GET-ENTRY\ncollection1\nentry-name1");
        commandProcessor.remoteInvoke(endpoint, "9 DELETE-ENTRY\ncollection1\nentry-name1");

        commandProcessor.remoteInvoke(endpoint, "10 CREATE-ENTRY\ncollection1\nentry-name1\nentry-value3");
        commandProcessor.remoteInvoke(endpoint, "11 DELETE-ENTRY\ncollection1\nentry-name1");
        commandProcessor.remoteInvoke(endpoint, "12 GET-ENTRY\ncollection1\nentry-name1");
        commandProcessor.remoteInvoke(endpoint, "13 DELETE-ENTRY\ncollection1\nentry-name1");

        Assert.assertEquals("Verifying retrieved messages", Arrays.asList(
                        "0 LOGIN\nOK",
                        "1 CREATE-ENTRY\nOK",
                        "2 GET-ENTRY\nentry-value1",
                        "3 DELETE-ENTRY\nOK",
                        "CALL_ERROR\n4 GET-ENTRY\nGET_FAILED_ENTRY_NOT_FOUND",
                        "CALL_ERROR\n5 DELETE-ENTRY\nDELETE_FAILED_ENTRY_NOT_FOUND",

                        "6 CREATE-ENTRY\nOK",
                        "7 DELETE-ENTRY\nOK",
                        "CALL_ERROR\n8 GET-ENTRY\nGET_FAILED_ENTRY_NOT_FOUND",
                        "CALL_ERROR\n9 DELETE-ENTRY\nDELETE_FAILED_ENTRY_NOT_FOUND",

                        "10 CREATE-ENTRY\nOK",
                        "11 DELETE-ENTRY\nOK",
                        "CALL_ERROR\n12 GET-ENTRY\nGET_FAILED_ENTRY_NOT_FOUND",
                        "CALL_ERROR\n13 DELETE-ENTRY\nDELETE_FAILED_ENTRY_NOT_FOUND"),
                endpoint.getCapturedMessages());
    }

    @Test
    public void testCreateOrUpdateFailedDueToOwnerConflict() {
        CommandProcessor commandProcessor = createCommandProcessor();

        EndpointConnectionCaptor endpoint1 = new EndpointConnectionCaptor();
        EndpointConnectionCaptor endpoint2 = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpoint1, "0 LOGIN\nendpoint1");
        commandProcessor.remoteInvoke(endpoint2, "1 LOGIN\nendpoint2");

        commandProcessor.remoteInvoke(endpoint1, "2 CREATE-ENTRY\ncollection1\nentry-name1\nentry-value1");
        commandProcessor.remoteInvoke(endpoint2, "3 CREATE-OR-UPDATE-ENTRY\ncollection1\nentry-name1\nentry-value2");
        commandProcessor.remoteInvoke(endpoint1, "4 GET-ENTRY\ncollection1\nentry-name1");
        commandProcessor.remoteInvoke(endpoint2, "5 GET-ENTRY\ncollection1\nentry-name1");

        Assert.assertEquals("Verifying endpoint1 retrieved messages", Arrays.asList(
                        "0 LOGIN\nOK",
                        "2 CREATE-ENTRY\nOK",
                        "4 GET-ENTRY\nentry-value1"),
                endpoint1.getCapturedMessages());

        Assert.assertEquals("Verifying endpoint1 retrieved messages", Arrays.asList(
                        "1 LOGIN\nOK",
                        "CALL_ERROR\n3 CREATE-OR-UPDATE-ENTRY\nUPDATE_FAILED_DUE_TO_OWNERSHIP_CHECK",
                        "5 GET-ENTRY\nentry-value1"),
                endpoint2.getCapturedMessages());
    }

    @Test
    public void testDeleteFailedDueToOwnerConflict() {
        CommandProcessor commandProcessor = createCommandProcessor();

        EndpointConnectionCaptor endpoint1 = new EndpointConnectionCaptor();
        EndpointConnectionCaptor endpoint2 = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpoint1, "0 LOGIN\nendpoint1");
        commandProcessor.remoteInvoke(endpoint2, "1 LOGIN\nendpoint2");

        commandProcessor.remoteInvoke(endpoint1, "2 CREATE-ENTRY\ncollection1\nentry-name1\nentry-value1");
        commandProcessor.remoteInvoke(endpoint2, "3 DELETE-ENTRY\ncollection1\nentry-name1");
        commandProcessor.remoteInvoke(endpoint1, "4 GET-ENTRY\ncollection1\nentry-name1");
        commandProcessor.remoteInvoke(endpoint2, "5 GET-ENTRY\ncollection1\nentry-name1");

        Assert.assertEquals("Verifying endpoint1 retrieved messages", Arrays.asList(
                        "0 LOGIN\nOK",
                        "2 CREATE-ENTRY\nOK",
                        "4 GET-ENTRY\nentry-value1"),
                endpoint1.getCapturedMessages());

        Assert.assertEquals("Verifying endpoint1 retrieved messages", Arrays.asList(
                        "1 LOGIN\nOK",
                        "CALL_ERROR\n3 DELETE-ENTRY\nDELETE_FAILED_DUE_TO_OWNERSHIP_CHECK",
                        "5 GET-ENTRY\nentry-value1"),
                endpoint2.getCapturedMessages());
    }

    @Test
    public void testGetEntriesWithBody() {
        CommandProcessor commandProcessor = createCommandProcessor();

        EndpointConnectionCaptor endpoint = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpoint, "0 LOGIN\nendpoint");

        commandProcessor.remoteInvoke(endpoint, "1 CREATE-ENTRY\ncollection1\nentry-name1\nentry1-value");
        commandProcessor.remoteInvoke(endpoint, "2 CREATE-ENTRY\ncollection1\nentry-name2\nentry2-value-1");
        commandProcessor.remoteInvoke(endpoint, "3 CREATE-ENTRY\ncollection2\nentry-name3\nentry3-value-555");
        commandProcessor.remoteInvoke(endpoint, "4 CREATE-ENTRY\ncollection2\nentry-name4\nentry4-value\nend-of-line");
        commandProcessor.remoteInvoke(endpoint, "5 CREATE-ENTRY\ncollection2\nentry-name5\nentry5-value");

        commandProcessor.remoteInvoke(endpoint, "6 GET-ENTRIES-WITH-BODY\ncollection1");
        commandProcessor.remoteInvoke(endpoint, "7 GET-ENTRIES-WITH-BODY\ncollection2");

        Assert.assertEquals("Verifying retrieved messages", Arrays.asList(
                        "0 LOGIN\nOK",

                        "1 CREATE-ENTRY\nOK",
                        "2 CREATE-ENTRY\nOK",
                        "3 CREATE-ENTRY\nOK",
                        "4 CREATE-ENTRY\nOK",
                        "5 CREATE-ENTRY\nOK",


                        "6 GET-ENTRIES-WITH-BODY\n12 entry-name1\nentry1-value\n14 entry-name2\nentry2-value-1",
                        "7 GET-ENTRIES-WITH-BODY\n16 entry-name3\nentry3-value-555\n12 entry-name5\nentry5-value\n24 entry-name4\nentry4-value\nend-of-line"),
                endpoint.getCapturedMessages());
    }

    @Test
    public void testGetEmptyEntryList() {
        CommandProcessor commandProcessor = createCommandProcessor();

        EndpointConnectionCaptor endpoint = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpoint, "0 LOGIN\nendpoint");
        commandProcessor.remoteInvoke(endpoint, "1 GET-ENTRIES-WITH-BODY\ncollection1");

        Assert.assertEquals("Verifying retrieved messages", Arrays.asList(
                        "0 LOGIN\nOK",
                        "1 GET-ENTRIES-WITH-BODY\n"),
                endpoint.getCapturedMessages());
    }

    @Test
    public void testConnectionLost(){
        CommandProcessor commandProcessor = createCommandProcessor();

        EndpointConnectionCaptor endpoint1 = new EndpointConnectionCaptor();
        EndpointConnectionCaptor endpoint2 = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpoint1, "0 LOGIN\nendpoint1");
        commandProcessor.remoteInvoke(endpoint2, "1 LOGIN\nendpoint2");

        commandProcessor.remoteInvoke(endpoint1, "3 CREATE-ENTRY\ncollection1\nentry-name1\nentry1-value");
        commandProcessor.remoteInvoke(endpoint1, "4 CREATE-ENTRY\ncollection1\nentry-name2\nentry2-value");
        commandProcessor.remoteInvoke(endpoint2, "5 CREATE-ENTRY\ncollection1\nentry-name3\nentry3-value");

        commandProcessor.remoteInvoke(endpoint2, "6 GET-ENTRIES-WITH-BODY\ncollection1");
        commandProcessor.remoteInvoke(endpoint2, "7 GET-ENTRY\ncollection1\nentry-name1");

        commandProcessor.connectionLost(endpoint1);

        commandProcessor.remoteInvoke(endpoint2, "8 GET-ENTRIES-WITH-BODY\ncollection1");
        commandProcessor.remoteInvoke(endpoint2, "9 GET-ENTRY\ncollection1\nentry-name1");

        Assert.assertEquals("Verifying endpoint1 retrieved messages", Arrays.asList(
                        "0 LOGIN\nOK",
                        "3 CREATE-ENTRY\nOK",
                        "4 CREATE-ENTRY\nOK"),
                endpoint1.getCapturedMessages());

        Assert.assertEquals("Verifying endpoint2 retrieved messages", Arrays.asList(
                        "1 LOGIN\nOK",
                        "5 CREATE-ENTRY\nOK",
                        "6 GET-ENTRIES-WITH-BODY\n12 entry-name1\nentry1-value\n12 entry-name3\nentry3-value\n12 entry-name2\nentry2-value",
                        "7 GET-ENTRY\nentry1-value",
                        "8 GET-ENTRIES-WITH-BODY\n12 entry-name3\nentry3-value",
                        "CALL_ERROR\n9 GET-ENTRY\nGET_FAILED_ENTRY_NOT_FOUND"),
                endpoint2.getCapturedMessages());
    }

    @Test
    public void testMalformedCommands(){
        CommandProcessor commandProcessor = createCommandProcessor();

        EndpointConnectionCaptor endpoint = new EndpointConnectionCaptor();

        commandProcessor.remoteInvoke(endpoint, "0 LOGIN\nendpoint1");

        commandProcessor.remoteInvoke(endpoint, "1 CREATE-ENTRY");
        commandProcessor.remoteInvoke(endpoint, "2 CREATE-ENTRY\ncollection");
        commandProcessor.remoteInvoke(endpoint, "3 CREATE-ENTRY\ncollection\nentry");

        commandProcessor.remoteInvoke(endpoint, "4 CREATE-OR-UPDATE-ENTRY");
        commandProcessor.remoteInvoke(endpoint, "5 CREATE-OR-UPDATE-ENTRY\ncollection");
        commandProcessor.remoteInvoke(endpoint, "6 CREATE-OR-UPDATE-ENTRY\ncollection\nentry");

        commandProcessor.remoteInvoke(endpoint, "7 GET-ENTRY");
        commandProcessor.remoteInvoke(endpoint, "8 GET-ENTRY\ncollection");
        commandProcessor.remoteInvoke(endpoint, "9 GET-ENTRY\ncollection\nentry-name\nunexpected-argument");

        commandProcessor.remoteInvoke(endpoint, "10 GET-ENTRIES-WITH-BODY");
        commandProcessor.remoteInvoke(endpoint, "11 GET-ENTRIES-WITH-BODY\ncollection\nunexpected-argument");

        commandProcessor.remoteInvoke(endpoint, "12 DELETE-ENTRY");
        commandProcessor.remoteInvoke(endpoint, "13 DELETE-ENTRY\ncollection");
        commandProcessor.remoteInvoke(endpoint, "14 DELETE-ENTRY\ncollection\nentry-name\nunexpected-argument");

        Assert.assertEquals("Verifying endpoint2 retrieved messages", Arrays.asList(
                        "0 LOGIN\nOK",

                        "CALL_ERROR\n1 CREATE-ENTRY\nSTORE_SERVICE_COMMAND_FAILED_MALFORMED_FORMAT",
                        "CALL_ERROR\n2 CREATE-ENTRY\nSTORE_SERVICE_COMMAND_FAILED_MALFORMED_FORMAT",
                        "CALL_ERROR\n3 CREATE-ENTRY\nSTORE_SERVICE_COMMAND_FAILED_MALFORMED_FORMAT",

                        "CALL_ERROR\n4 CREATE-OR-UPDATE-ENTRY\nSTORE_SERVICE_COMMAND_FAILED_MALFORMED_FORMAT",
                        "CALL_ERROR\n5 CREATE-OR-UPDATE-ENTRY\nSTORE_SERVICE_COMMAND_FAILED_MALFORMED_FORMAT",
                        "CALL_ERROR\n6 CREATE-OR-UPDATE-ENTRY\nSTORE_SERVICE_COMMAND_FAILED_MALFORMED_FORMAT",

                        "CALL_ERROR\n7 GET-ENTRY\nSTORE_SERVICE_COMMAND_FAILED_MALFORMED_FORMAT",
                        "CALL_ERROR\n8 GET-ENTRY\nSTORE_SERVICE_COMMAND_FAILED_MALFORMED_FORMAT",
                        "CALL_ERROR\n9 GET-ENTRY\nSTORE_SERVICE_COMMAND_FAILED_MALFORMED_FORMAT",

                        "CALL_ERROR\n10 GET-ENTRIES-WITH-BODY\nSTORE_SERVICE_COMMAND_FAILED_MALFORMED_FORMAT",
                        "CALL_ERROR\n11 GET-ENTRIES-WITH-BODY\nSTORE_SERVICE_COMMAND_FAILED_MALFORMED_FORMAT",

                        "CALL_ERROR\n12 DELETE-ENTRY\nSTORE_SERVICE_COMMAND_FAILED_MALFORMED_FORMAT",
                        "CALL_ERROR\n13 DELETE-ENTRY\nSTORE_SERVICE_COMMAND_FAILED_MALFORMED_FORMAT",
                        "CALL_ERROR\n14 DELETE-ENTRY\nSTORE_SERVICE_COMMAND_FAILED_MALFORMED_FORMAT"),
                endpoint.getCapturedMessages());
    }
}
