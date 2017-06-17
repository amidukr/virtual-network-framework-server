package org.vnf.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.vnf.server.core.commandprocessor.CommandProcessor;
import org.vnf.server.endpoint.ProcessorWebSocketEndpoint;

import javax.websocket.server.ServerEndpointConfig;


/**
 * Created by qik on 6/11/2017.
 */
public class JettyServerLauncher {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        ServerContainer container = WebSocketServerContainerInitializer.configureContext(context);

        CommandProcessor commandProcessor = new CommandProcessor();
        commandProcessor.addServiceHandlers(new VnfServiceHandlersFactory().create());

        ServerEndpointConfig endpointConfig = ProcessorWebSocketEndpoint.createEndpointConfig(commandProcessor, "/vnf-ws");

        container.addEndpoint(endpointConfig);

        server.start();
        server.join();
    }
}
