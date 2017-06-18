package org.vnf.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.vnf.server.core.commandprocessor.CommandProcessor;
import org.vnf.server.core.commonservice.CommonServiceHandlersConfiguration;
import org.vnf.server.endpoint.ProcessorWebSocketEndpoint;

import javax.websocket.server.ServerEndpointConfig;


/**
 * Created by qik on 6/11/2017.
 */
public class JettyServerLauncher {
    private static ServerEndpointConfig createVnfWsEndpointConfig() {
        CommandProcessor commandProcessor = new CommandProcessor();
        commandProcessor.addServiceHandlers(new CommonServiceHandlersConfiguration());
        commandProcessor.addServiceHandlers(new VnfServiceHandlersFactory().create());

        return ProcessorWebSocketEndpoint.createEndpointConfig(commandProcessor, "/vnf-ws");
    }

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(ClassLoaderResourceServlet.class, "/ui/*");

        ServerContainer container = WebSocketServerContainerInitializer.configureContext(context);

        container.addEndpoint(createVnfWsEndpointConfig());

        server.start();
        server.join();
    }
}
