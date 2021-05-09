package org.amidukr.software.vnf.server.core.commonservice;

import org.amidukr.software.vnf.server.core.commandprocessor.*;
import org.amidukr.software.vnf.server.core.commandprocessor.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Dmytro Brazhnyk on 6/18/2017.
 */
public class HelpCommandHandler extends InvokeHandler {

    public HelpCommandHandler() {
        super("HELP");

        setAuthorizationType(AuthorizationType.ANY);
    }

    @Override
    public InvocationResult handleCommand(CommandEvent event) {
        CommandProcessor commandProcessor = event.getCommandProcessor();

        List<InvokeHandler> commandList = commandProcessor.getInvokeHandlers().stream()
                .filter(x -> !x.getCommandName().equals("HELP"))
                .sorted(Comparator.comparing(InvokeHandler::getCommandName))
                .collect(Collectors.toList());

        StringBuilder stringBuilder = new StringBuilder();

        if(!"no-header".equals(event.getCommandArgument())) {
            stringBuilder.append("To get help use HELP command\n");
            stringBuilder.append("----------------------------\n");
            stringBuilder.append("List of available commands:\n");
        }

        String commandNames = commandList.stream()
                .map(InvokeHandler::getCommandName)
                .collect(Collectors.joining("\n"));

        stringBuilder.append(commandNames);

        return InvocationResult.succeed(stringBuilder.toString());
    }
}
