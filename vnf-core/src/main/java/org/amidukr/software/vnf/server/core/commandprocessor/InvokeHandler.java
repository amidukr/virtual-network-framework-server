package org.amidukr.software.vnf.server.core.commandprocessor;

/**
 * Created by Dmytro Brazhnyk on 6/3/2017.
 */
public abstract class InvokeHandler {
    private final String commandName;
    private AuthorizationType authorizationType = AuthorizationType.AUTHENTICATED_ONLY;

    public InvokeHandler(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    public AuthorizationType getAuthorizationType() {
        return authorizationType;
    }

    public void setAuthorizationType(AuthorizationType authorizationType) {
        this.authorizationType = authorizationType;
    }

    public abstract InvocationResult handleCommand(CommandEvent event);
}
