package se.alten.sockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import se.alten.controller.ChatController;

import se.alten.model.Message;
import se.alten.model.MessagePost;

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

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //TODO Check so that the client doesn't already exists... (if that's even possible...)
        sessions.put(session.getId(), session);
        log.info("afterConnectionEstablished called. Session = " + session);
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        boolean containsKey = sessions.containsKey(session.getId());

        if (containsKey) {
            log.info("Connection closed. Id = " + session.getId() + " CloseStatus = " + status);
            sessions.remove(session.getId());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage messageIn) throws Exception {
        if ("CLOSE".equalsIgnoreCase(messageIn.getPayload())) {
            session.close();
        } else {
            log.info("RAW textMessage in: " + messageIn.getPayload());
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(messageIn.getPayload());
            JSONObject jsonObject = (JSONObject) obj;

            MessagePost messagePost = new MessagePost();
            if (jsonObject.get("message") != null) {
                messagePost.setMessage((String) jsonObject.get("message"));
            }
            if (jsonObject.get("userId") != null) {
                messagePost.setUserId(Integer.valueOf(jsonObject.get("userId").toString()));
            }

            // TODO on replies this is not null
            if (jsonObject.get("id") != null) {
                messagePost.setId(Integer.valueOf(jsonObject.get("id").toString()));
            }


            Message messageOut = ChatController.postMessageHelper(messagePost);
            log.info("Received TextMessage:" + messageIn.toString());
            log.info("Rebuild message:" + messageOut);

            ObjectMapper mapper = new ObjectMapper();
            for (WebSocketSession s : sessions.values()) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(mapper.writeValueAsString(messageOut)));
                }
            }


        }
    }

    public Map<String, WebSocketSession> getAllSessions() {
        //TODO create a "stripped down" wrapper class for the client?
        return sessions;
    }
    public int getCurrentActiveSessions() {
        return sessions.size();
    }


    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
