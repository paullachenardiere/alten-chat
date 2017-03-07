package se.alten.sockets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.server.standard.SpringConfigurator;
import se.alten.model.Message;
import se.alten.model.MessagePost;
import se.alten.model.User;
import se.alten.service.ChatMessageService;

import javax.persistence.NoResultException;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by pl3731 on 2017-01-26.
 */
@Controller
@ServerEndpoint(value = "/chat", configurator = SpringConfigurator.class)
public class ChatSocketController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ChatMessageService service;

    @Autowired
    public ChatSocketController(ChatMessageService service) {
        this.service = service;
    }


//    @SendTo("/chat/messages")
//    @RequestMapping("/chat")
    public ResponseEntity handshake() {
        log.info("Websockets handshake");
        return new ResponseEntity(HttpStatus.valueOf(101));
    }


    @SuppressWarnings("Duplicates")
//    @MessageMapping("/messages")
    @MessageMapping("/chat")
    public ResponseEntity<List<Message>> getMessages() {
        List<Message> messages = service.getAllChatMessages().stream().map(m -> service.transformToPresentationMessage(m)).collect(Collectors.toList());
        log.info("Get all messages. Amount = " + messages.size());
        return new ResponseEntity<List<Message>>(messages, HttpStatus.OK);
    }


    @SuppressWarnings("Duplicates")
    public ResponseEntity postMessage(@RequestBody MessagePost msg) {
        ResponseEntity responseEntity;
        Message message = null;
        try {
            User user = service.getUser(msg.getUserId());
            message = service.transformToPersistentMessage(msg, user);
            message = service.addNewChatMessage(message);
            message = service.transformToPresentationMessage(message);
            log.info("Incoming message = " + message.toString());

            responseEntity = new ResponseEntity<Message>(message, HttpStatus.CREATED);
        } catch (NoResultException nre) {
            log.warn("Can't add message because the user don't exists. (UserId=" + message.getUser().getUserId() + ") " + nre);
            responseEntity = new ResponseEntity<>(nre.getMessage(), HttpStatus.CONFLICT);
        }

        return responseEntity;
    }
}



