package se.alten.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import se.alten.model.Message;
import se.alten.model.ReplyMessage;
import se.alten.model.User;
import se.alten.service.ChatMessageService;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.logging.Logger;


/**
 * Created by pl3731 on 2017-01-26.
 */

@EnableWebMvc
@RestController
@RequestMapping("altenchat")
public class ChatController extends WebMvcConfigurerAdapter {

    private Logger log = Logger.getLogger(ChatController.class.toString());

    @Autowired
    private ChatMessageService service;


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity<List<Message>> getMessages() {
        List messages = service.getAllChatMessages();
        return new ResponseEntity<List<Message>>(messages, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Message> getMessage(@PathVariable("id") int id) {
        ResponseEntity responseEntity;
        Message message;
        try {
            message = service.getMessage(id);
            responseEntity = new ResponseEntity<Message>(message, HttpStatus.OK);
        } catch (NoResultException nre) {
            responseEntity = new ResponseEntity<>(nre.getMessage(), HttpStatus.NOT_FOUND);
        }

        return responseEntity;
    }


    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<Void> postMessage(@RequestBody Message msg) {
        ResponseEntity responseEntity;

        try {
            service.getUser(msg.getUserId());
            Message message = new Message(msg.getMessage(), msg.getUserId());
            service.addNewChatMessage(message);
            log.info("Incoming message = " + message.toString());
            responseEntity = new ResponseEntity<>(HttpStatus.CREATED);
        } catch (NoResultException nre) {
            log.warning("Can't add message because the user don't exists. (UserId=" + msg.getUserId() + ") " + nre);
            responseEntity = new ResponseEntity<>(nre.getMessage(), HttpStatus.CONFLICT);
        }

        return responseEntity;
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public ResponseEntity updateMessage(@RequestBody Message msg) {
        ResponseEntity responseEntity;

        try {
            service.getUser(msg.getUserId());
            service.updateMessage(msg);
            log.info("Update message = " + msg.toString());
            responseEntity = new ResponseEntity<>(HttpStatus.CREATED);

        } catch (NoResultException nre) {
            log.warning("Can't update message because the user don't exists. (UserId=" + msg.getUserId() + ") " + nre);
            responseEntity = new ResponseEntity<>(nre.getMessage(), HttpStatus.CONFLICT);
        }

        return responseEntity;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity updateReplyMessage(@RequestBody ReplyMessage msg, @PathVariable("id") int id) {
        ResponseEntity responseEntity;


        //TODO
        try {
            service.getUser(msg.getUserId());
            service.updateMessage(msg);
            log.info("Update reply message = " + msg.toString());
            responseEntity = new ResponseEntity<>(HttpStatus.CREATED);

        } catch (NoResultException nre) {
            log.warning("Can't update reply message because the user don't exists. (UserId=" + msg.getUserId() + ") " + nre);
            responseEntity = new ResponseEntity<>(nre.getMessage(), HttpStatus.CONFLICT);
        }

        return responseEntity;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ResponseEntity<Void> replyMessage(@RequestBody Message msg, @PathVariable("id") int parentId) {
        ResponseEntity responseEntity;

        try {
            service.replyMessage(msg, parentId);
            responseEntity = new ResponseEntity<>(HttpStatus.CREATED);

        } catch (NoResultException nre) {
            responseEntity = new ResponseEntity<>(nre.getMessage(), HttpStatus.CONFLICT);
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
            log.warning("Can't delete message because the message don't exists. (MessageId=" + id + ") " + nre);
            responseEntity = new ResponseEntity<>(nre.getMessage(), HttpStatus.CONFLICT);
        }

        return responseEntity;
    }


    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<Void> createUser(@RequestBody User usr) {

        //TODO Validate user
        User user = new User(usr.getUserName(), usr.getPassword());
        service.createUser(user);
        log.info("Created new User = " + user.toString());


        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<List<User>> getUsers() {

        List<User> users = service.getUsers();
        return new ResponseEntity<List<User>>(users, HttpStatus.OK);
    }


}
