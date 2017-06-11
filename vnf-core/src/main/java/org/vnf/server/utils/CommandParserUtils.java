package org.vnf.server.utils;

/**
 * Created by qik on 6/10/2017.
 */
public interface CommandParserUtils {
    public static String[] parseCommand(String message, int tokens, boolean withTail) {
        if(message == null) return null;
        if(tokens == 0) return new String[0];
        if(tokens < 0 ) throw new IllegalArgumentException("Tokens should zero or more");

        String[] result = new String[tokens];

        int eolIndex = -1;
        for(int i = 0; i < tokens - 1; i++) {
            int nextEol = message.indexOf('\n', eolIndex + 1);

            if(nextEol == -1) {
                return null;
            }

            result[i] = message.substring(eolIndex+1, nextEol);

            eolIndex = nextEol;
        }

        if(!withTail) {
            if(message.indexOf('\n', eolIndex + 1) != -1) {
                return null;
            }
        }

        result[result.length - 1] = message.substring(eolIndex + 1);

        return result;
    }
}
