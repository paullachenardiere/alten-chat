package se.alten.sockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import se.alten.controller.ChatController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by pl3731 on 2017-03-08.
 */
@Component
public class MessageHandler extends TextWebSocketHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Autowired
    ChatController ChatController;

    private final static String SESSION_ID_PREFIX = "SESSION_ID=";
//    private final static String IS_WRITING_PREFIX = "IS_WRITING=";
//    private final static String HAS_STOPPED_WRITING_PREFIX = "HAS_STOPPED_WRITING=";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        //TODO Check so that the client doesn't already exists... (if that's even possible...)
        sessions.put(session.getId(), session);
        log.info("afterConnectionEstablished called. Session = " + session);
        try {
            session.sendMessage(new TextMessage(SESSION_ID_PREFIX + session.getId()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        boolean containsKey = sessions.containsKey(session.getId());

        if (containsKey) {
            log.info("Connection closed. Id = " + session.getId() + " CloseStatus = " + status);
            sessions.remove(session.getId());
            //TODO NOTIFY subscribers
        }
    }

//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage messageIn) throws IOException {
//        if ("CLOSE".equalsIgnoreCase(messageIn.getPayload())) {
//            session.close();
//        } else {
//            log.info("RAW textMessage in: " + messageIn.getPayload());
//            JSONParser parser = new JSONParser();
//            Object obj = null;
//            try {
//                obj = parser.parse(messageIn.getPayload());
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            JSONObject jsonObject = (JSONObject) obj;
//
//            MessagePost messagePost = new MessagePost();
//            if (jsonObject.get("message") != null) {
//                messagePost.setMessage((String) jsonObject.get("message"));
//            }
//            if (jsonObject.get("userId") != null) {
//                messagePost.setUserId(Integer.valueOf(jsonObject.get("userId").toString()));
//            }
//
//            // TODO on replies this is not null
//            if (jsonObject.get("id") != null) {
//                messagePost.setId(Integer.valueOf(jsonObject.get("id").toString()));
//            }
//
//
//            Message messageOut = ChatController.postMessageHelper(messagePost);
//            log.info("Received TextMessage:" + messageIn.toString());
//            log.info("Rebuild message:" + messageOut);
//
//            ObjectMapper mapper = new ObjectMapper();
//            for (WebSocketSession s : sessions.values()) {
//                if (s.isOpen()) {
//                    s.sendMessage(new TextMessage(mapper.writeValueAsString(messageOut)));
//                }
//            }
//
//
//        }
//    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage messageIn) throws IOException {
        if ("CLOSE".equalsIgnoreCase(messageIn.getPayload())) {
            session.close();
        } else {
            log.info("RAW textMessage in: " + messageIn.getPayload());
            for (WebSocketSession s : sessions.values()) {
                if (s.isOpen() && !s.getId().equals(session.getId())) {
                    s.sendMessage(new TextMessage(messageIn.getPayload()));
                }
            }
        }
    }

    public Map<String, WebSocketSession> getAllSessions() {
        //TODO create a "stripped down" wrapper class for the client?
        return sessions;
    }

    public Map<String, WebSocketSession> getCurrentActiveSessions() {
        for (WebSocketSession ws : sessions.values()) {
            log.info("Handshake headers = " + ws.getHandshakeHeaders().toString());
            log.info("Handshake attributes  = " + ws.getAttributes());
            log.info("Handshake extensions = " + ws.getExtensions());
            log.info("Handshake id = " + ws.getId());
            log.info("Handshake AcceptedProtocol = " + ws.getAcceptedProtocol());
        }
        return sessions;
    }


    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) {
        try {
            InetSocketAddress address = webSocketSession.getRemoteAddress();
            webSocketSession.close(CloseStatus.SERVER_ERROR);
            log.warn("webSocketSession closed with transport error. Remote address = " + address);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
