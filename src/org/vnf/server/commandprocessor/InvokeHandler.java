package org.vnf.server.commandprocessor;

/**
 * Created by qik on 6/3/2017.
 */
public abstract class InvokeHandler {
    private AuthorizationType authorizationType = AuthorizationType.AUTHENTICATED_ONLY;

    public InvokeHandler(String commandName) {

    }

    public boolean isAuthenticatedOnly() {
        return true;
    }

    public abstract String handleCommand(CommandEvent event) throws CommandException;

    public void setAuthorizationType(AuthorizationType authorizationType) {
        this.authorizationType = authorizationType;
    }
}
