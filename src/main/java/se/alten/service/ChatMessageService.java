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


    public void addNewChatMessage(Message message) {
        message.setTimestamp(LocalDateTime.now());
        messageRepo.addMessage(message);
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

    public void updateMessage(Message message) {
        messageRepo.updateMessage(message);
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

    public void replyMessage(Message msg, int parentId) {
        ReplyMessage replyMessage = new ReplyMessage(msg.getMessage(), msg.getUserId(), parentId);
        replyMessage.setTimestamp(LocalDateTime.now());
        Message message = getMessage(parentId);
        message.addReply(replyMessage);
        updateMessage(message);
    }
}
