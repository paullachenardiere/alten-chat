package se.alten.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.alten.model.*;
import se.alten.repository.ChatMessageRepo;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/**
 * Created by pl3731 on 2017-01-31.
 */
@Service
public class ChatMessageService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ChatMessageRepo messageRepo;


    public Message addNewChatMessage(Message message) {
        message.setTimestamp(getTimestampOfCurrent());
        messageRepo.addMessage(message);
        return message;
    }

    public List<Message> getAllChatMessages() {
        return messageRepo.getAll();
    }

    public void createUser(User user) {
        messageRepo.createUser(user);
    }

    public List<User> getUsers() {
        return messageRepo.getUsers();
    }

    public User getUser(int userId) {
        return messageRepo.getUser(userId);
    }

    public Message updateMessage(MessagePost messagePost) {
        Message message = getMessage(messagePost.getId());
        message.setMessage(messagePost.getMessage());
        message.setEdited(true);
        return messageRepo.updateMessage(message);
    }

    public ReplyMessage updateMessage(ReplyMessage replyMessage) {
        return messageRepo.updateReplyMessage(replyMessage);
    }

    public void deleteMessage(int id) {
        messageRepo.deleteMessage(id);
    }

    public Message getMessage(int id) {
        return messageRepo.getMessage(id);
    }

    public Message replyMessage(Message msg, int parentId) {
        ReplyMessage replyMessage = new ReplyMessage(msg.getMessage(), msg.getUser(), parentId);
        replyMessage.setTimestamp(getTimestampOfCurrent());
        Message message = getMessage(parentId);
        message.addReply(replyMessage);
        return messageRepo.updateMessage(message);
    }

    public Message transformToPresentationMessage(BaseMessage message) {
        User user = message.getUser();
        user.setPassword("");
        return (Message) message;
    }

    private Timestamp getTimestampOfCurrent() {
        Instant instant = Instant.now();
        long timeStampMillis = instant.toEpochMilli();
        return new Timestamp(timeStampMillis);
    }

    public User validateUser(User user) {
        //TODO Improve this validation. Regex on email...
        boolean valid = true;
        User validUser = null;

        if (user.getUserName() == null || user.getUserName().length() < 3) {
            valid = false;
        }

        if (user.getPassword() == null || user.getPassword().length() < 4) {
            valid = false;
        }

        if (valid) {
            validUser = new User(user.getUserName(), user.getPassword());
        }

        return validUser;
    }


    public Message transformToPersistentMessage(MessagePost msg, User user) {
        Message message = new Message(msg.getMessage(), user);
        return message;
    }
}
