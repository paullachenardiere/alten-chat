package se.alten.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import se.alten.model.*;
import se.alten.service.ChatMessageService;
import se.alten.sockets.MessageHandler;

import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by pl3731 on 2017-01-26.
 */

@EnableWebMvc
@RestController
@RequestMapping("altenchat")
//@Controller
//@ServerEndpoint(value = "/chat", configurator = SpringConfigurator.class)
public class ChatController extends WebMvcConfigurerAdapter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ChatMessageService service;
    @Autowired
    private MessageHandler messageHandler;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> getMessages() {
        List<Message> messages = service.getAllChatMessages().stream().map(m -> service.transformToPresentationMessage(m)).collect(Collectors.toList());
        log.info("Get all messages. Amount = " + messages.size());
        return new ResponseEntity<List<Message>>(messages, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity getMessage(@PathVariable("id") int id) {
        ResponseEntity responseEntity;
        Message message;
        try {
            message = service.transformToPresentationMessage(service.getMessage(id));
            responseEntity = new ResponseEntity<Message>(message, HttpStatus.OK);
        } catch (NoResultException nre) {
            responseEntity = new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return responseEntity;
    }

    //    @MessageMapping("/chat")
//    @SendTo("/chat")
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity postMessage(@RequestBody MessagePost msg) {
        ResponseEntity responseEntity;
        Message message = null;
        try {
            message = postMessageHelper(msg);
            responseEntity = new ResponseEntity<Message>(message, HttpStatus.CREATED);
        } catch (NoResultException nre) {
            log.warn("Can't add message because the user don't exists. (UserId=" + message.getUser().getUserId() + ") " + nre);
            responseEntity = new ResponseEntity<>(nre.getMessage(), HttpStatus.CONFLICT);
        }

//        myHandler.myHandler().notifyAll();

        return responseEntity;
    }

    public Message postMessageHelper(MessagePost msg) {
        Message message = null;
        User user = service.getUser(msg.getUserId());
        message = service.transformToPersistentMessage(msg, user);
        message = service.addNewChatMessage(message);
        message = service.transformToPresentationMessage(message);
        log.info("Incoming message = " + message.toString());
        return message;
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public ResponseEntity updateMessage(@RequestBody MessagePost msg) {

        log.info("updateMessage " + msg.toString());

        return update(msg);
    }

//    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
//    public ResponseEntity updateReplyMessage(@RequestBody ReplyMessage msg, @PathVariable("id") int id) {
//        return update(msg);
//    }

    private ResponseEntity update(MessagePost msg) {
        ResponseEntity responseEntity;
        String type = null;
        Message message = null;
        ReplyMessage replyMessage = null;

        User user = service.getUser(msg.getUserId());
        message = service.updateMessage(msg);

        try {
//            service.getUser(msg.getUser().getUserId());
            //TODO Check if user exists before update.
//            if (msg instanceof ReplyMessage) {
//                type = "replyMessage";
//                replyMessage = service.updateMessage((ReplyMessage) msg);
//            }
//            if (msg instanceof Message) {
//                type = "message";
//            }
            message = service.transformToPresentationMessage(message);

//            log.info("Update " + type + " " + message.toString());

            responseEntity = new ResponseEntity<Message>(message, HttpStatus.CREATED);

        } catch (NoResultException | EmptyResultDataAccessException nre) {
            log.warn("Can't update message because the user don't exists. (UserId=" + message.getUser().getUserId() + ") " + nre);
            responseEntity = new ResponseEntity<>(nre.getMessage(), HttpStatus.CONFLICT);
        }

        return responseEntity;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ResponseEntity<Message> replyMessage(@RequestBody MessagePost msg, @PathVariable("id") int parentId) {
        ResponseEntity responseEntity;
        Message message = null;

        try {
            User user = service.getUser(msg.getUserId());
            message = service.transformToPersistentMessage(msg, user);
            message = service.replyMessage(message, parentId);
            message = service.transformToPresentationMessage(message);
            log.info("Reply Message: " + msg.getMessage());
            responseEntity = new ResponseEntity<Message>(message, HttpStatus.CREATED);

        } catch (NoResultException nre) {
            responseEntity = new ResponseEntity<>(nre.getMessage(), HttpStatus.CONFLICT);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        for (WebSocketSession session : messageHandler.getAllSessions().values()) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return responseEntity;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteMessage(@PathVariable("id") int id) {
        ResponseEntity responseEntity;

        try {
            service.deleteMessage(id);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch (NoResultException nre) {
            log.warn("Can't delete message because the message don't exists. (MessageId=" + id + ") " + nre);
            responseEntity = new ResponseEntity<>(nre.getMessage(), HttpStatus.CONFLICT);
        }

        return responseEntity;
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity createUser(@RequestBody User usr) {
        ResponseEntity responseEntity;

        User user = service.validateUser(usr);
        if (user != null) {
            service.createUser(user);
            log.info("Created new User = " + user.toString());
            responseEntity = new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            log.info("Create new User FAILED = " + usr.toString());
            responseEntity = new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return responseEntity;
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<List<User>> getUsers() {

        List<User> users = service.getUsers();
        return new ResponseEntity<List<User>>(users, HttpStatus.OK);
    }

    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public ResponseEntity<Integer> getSessionsAmount() {

        int currentActiveSessions = messageHandler.getCurrentActiveSessions();

        return new ResponseEntity<Integer>(currentActiveSessions, HttpStatus.OK);
    }

}
