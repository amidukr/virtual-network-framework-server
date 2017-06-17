package org.vnf.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;

import javax.websocket.server.ServerEndpointConfig;


/**
 * Created by qik on 6/11/2017.
 */
public class ServerLauncher {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        ServerContainer container = WebSocketServerContainerInitializer.configureContext(context);



        //jsr356 - less overhead in stacktrace - should be faster

        ServerEndpointConfig endpointConfig = ServerEndpointConfig.Builder.create(WebSocketEndpoint.class, "/jsr356").build();

        //endpointConfig.getUserProperties()

        container.addEndpoint(endpointConfig);

        context.addServlet(JettyWS.class, "/jetty");

        server.start();
        server.join();
    }
}
