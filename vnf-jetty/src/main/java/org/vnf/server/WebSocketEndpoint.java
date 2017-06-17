package org.vnf.server;

import javax.websocket.*;

/**
 * Created by qik on 6/11/2017.
 */
//@ServerEndpoint("/ws")
public class WebSocketEndpoint extends Endpoint {

    @OnOpen
    public void onOpenAnnotation(Session session, EndpointConfig config) {
        System.out.println("New websocket: annotation");

        //getUserProperties() - session scope

        session.addMessageHandler(new MessageHandler.Whole<String>(){

            @Override
            public void onMessage(String message) {
                System.out.println("JS356 Annotation On message: " + message);
            }
        });
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        System.out.println("New websocket");


        session.addMessageHandler(new MessageHandler.Whole<String>(){

            @Override
            public void onMessage(String message) {
                System.out.println("JS356 On message: " + message);
            }
        });
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {

    }

    @Override
    public void onError(Session session, Throwable thr) {

    }
}
