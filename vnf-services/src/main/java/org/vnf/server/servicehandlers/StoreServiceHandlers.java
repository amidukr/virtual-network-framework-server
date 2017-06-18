package org.vnf.server.servicehandlers;

import org.vnf.server.core.commandprocessor.CommandEvent;
import org.vnf.server.core.commandprocessor.ConnectionLostEvent;
import org.vnf.server.core.commandprocessor.InvocationResult;
import org.vnf.server.core.servicefactory.Invoke;
import org.vnf.server.core.servicefactory.OnConnectionLost;
import org.vnf.server.core.servicefactory.ServiceObject;
import org.vnf.server.repository.entities.StoreEntry;
import org.vnf.server.service.StoreServiceErrorCode;
import org.vnf.server.service.StoreService;
import org.vnf.server.utils.CommandParserUtils;

import java.util.Map;

/**
 * Created by qik on 6/10/2017.
 */
public class StoreServiceHandlers implements ServiceObject {
    private static final String STORE_SERVICE_COMMAND_FAILED_MALFORMED_FORMAT = "STORE_SERVICE_COMMAND_FAILED_MALFORMED_FORMAT";

    private final StoreService storeService = new StoreService();

    private InvocationResult wrapResponse(StoreServiceErrorCode storeServiceErrorCode) {
        return  storeServiceErrorCode == StoreServiceErrorCode.OK ?
                InvocationResult.succeed(storeServiceErrorCode.toString()) : InvocationResult.failed(storeServiceErrorCode.toString());
    }

    @Invoke("CREATE-ENTRY")
    public InvocationResult createEntry(CommandEvent event) {
        String[] tokens = CommandParserUtils.parseCommand(event.getCommandArgument(), 3, true);

        if(tokens == null) {
            return InvocationResult.failed(STORE_SERVICE_COMMAND_FAILED_MALFORMED_FORMAT);
        }

        return wrapResponse(storeService.createEntry(event.getEndpointId(), tokens[0], tokens[1], tokens[2]));
    }



    @Invoke("CREATE-OR-UPDATE-ENTRY")
    public InvocationResult createOrUpdateEntry(CommandEvent event) {
        String[] tokens = CommandParserUtils.parseCommand(event.getCommandArgument(), 3, true);

        if(tokens == null) {
            return InvocationResult.failed(STORE_SERVICE_COMMAND_FAILED_MALFORMED_FORMAT);
        }

        return wrapResponse(storeService.createOrUpdateEntry(event.getEndpointId(), tokens[0], tokens[1], tokens[2]));
    }

    @Invoke("GET-ENTRY")
    public InvocationResult getEntry(CommandEvent event) {
        String[] tokens = CommandParserUtils.parseCommand(event.getCommandArgument(), 2, false);

        if(tokens == null) {
            return InvocationResult.failed(STORE_SERVICE_COMMAND_FAILED_MALFORMED_FORMAT);
        }

        StoreEntry entry = storeService.getEntry(tokens[0], tokens[1]);

        if(entry == null) {
            return InvocationResult.failed(StoreServiceErrorCode.GET_FAILED_ENTRY_NOT_FOUND.toString());
        }


        return InvocationResult.succeed(entry.getValue());
    }

    @Invoke("GET-ENTRIES-WITH-BODY")
    public InvocationResult getEntriesWithBody(CommandEvent event) {
        String[] tokens = CommandParserUtils.parseCommand(event.getCommandArgument(), 1, false);

        if(tokens == null) {
            return InvocationResult.failed(STORE_SERVICE_COMMAND_FAILED_MALFORMED_FORMAT);
        }

        Map<String, StoreEntry> entryMap = storeService.getEntries(tokens[0]);

        StringBuilder result = new StringBuilder();



        int count = entryMap.size();
        for (Map.Entry<String, StoreEntry> mapEntry : entryMap.entrySet()) {
            String key = mapEntry.getKey();
            String entryValue = mapEntry.getValue().getValue();

            count--;
            result.append(entryValue.length());
            result.append(' ');
            result.append(key);
            result.append('\n');
            result.append(entryValue);

            if(count > 0) {
                result.append('\n');
            }
        }

        return InvocationResult.succeed(result.toString());
    }

    @Invoke("DELETE-ENTRY")
    public InvocationResult deleteEntry(CommandEvent event) {
        String[] tokens = CommandParserUtils.parseCommand(event.getCommandArgument(), 2, false);

        if(tokens == null) {
            return InvocationResult.failed(STORE_SERVICE_COMMAND_FAILED_MALFORMED_FORMAT);
        }

        return wrapResponse(storeService.deleteEntry(event.getEndpointId(), tokens[0], tokens[1]));
    }

    @OnConnectionLost
    public void onConnectionLost(ConnectionLostEvent event) {
        storeService.dropEntriesByOwner(event.getEndpointId());
    }
}
