package org.amidukr.software.vnf.server.core.commandprocessor;

/**
 * Created by Dmytro Brazhnyk on 6/10/2017.
 */
public class InvocationResult {
    private final boolean succeed;
    private final String result;
    private final String errorReason;

    private InvocationResult(boolean succeed, String result, String errorReason) {
        this.succeed = succeed;
        this.result = result;
        this.errorReason = errorReason;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public String getResult() {
        if(!succeed) throw new RuntimeException("Invocation failed");

        return result;
    }

    public String getErrorReason() {
        if(succeed) throw new RuntimeException("Invocation succeed");

        return errorReason;
    }

    public static InvocationResult succeed(String result) {
        return new InvocationResult(true, result, null);
    }

    public static InvocationResult failed(String errorReason) {
        return new InvocationResult(false, null, errorReason);
    }
}
