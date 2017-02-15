package se.alten.service;

import org.springframework.stereotype.Service;
import se.alten.model.Message;
import se.alten.model.ReplyMessage;
import se.alten.model.User;
import se.alten.repository.ChatMessageRepo;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by pl3731 on 2017-01-31.
 */
@Service
public class ChatMessageService {

    @Resource
    private ChatMessageRepo messageRepo;


    public Message addNewChatMessage(Message message) {
        message.setTimestamp(LocalDateTime.now());
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

    public Message updateMessage(Message message) {
        return messageRepo.updateMessage(message);
    }

    public void updateMessage(ReplyMessage replyMessage) {
        messageRepo.updateReplyMessage(replyMessage);
    }

    public void deleteMessage(int id) {
        messageRepo.deleteMessage(id);
    }

    public Message getMessage(int id) {
        return messageRepo.getMessage(id);
    }

    public Message replyMessage(Message msg, int parentId) {
        ReplyMessage replyMessage = new ReplyMessage(msg.getMessage(), msg.getUserId(), parentId);
        replyMessage.setTimestamp(LocalDateTime.now());
        Message message = getMessage(parentId);
        message.addReply(replyMessage);
        return updateMessage(message);
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
}
