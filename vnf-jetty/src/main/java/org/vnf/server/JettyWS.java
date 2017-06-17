package org.vnf.server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.servlet.*;

/**
 * Created by qik on 6/11/2017.
 */
public class JettyWS extends WebSocketServlet implements WebSocketCreator {


    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.setCreator(this);
    }

    @Override
    public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
        return new WebSocketAdapter(){
            @Override
            public void onWebSocketConnect(Session sess) {
                super.onWebSocketConnect(sess);

                System.out.println("Jetty Open");
            }

            @Override
            public void onWebSocketText(String message) {
                System.out.println("Jetty Message: " + message);
            }
        };
    }
}
