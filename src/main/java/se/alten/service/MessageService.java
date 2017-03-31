package se.alten.service;

import org.springframework.web.socket.WebSocketSession;
import se.alten.model.*;

import java.util.List;
import java.util.Map;

/**
 * Created by pl3731 on 2017-03-19.
 */
public interface MessageService {
    Message addNewChatMessage(Message message);

    List<Message> getAllChatMessages();

    void createUser(User user);

    List<User> getUsers();

    User getUser(int userId);

    Message updateMessage(MessagePost messagePost);

    ReplyMessage updateMessage(ReplyMessage replyMessage);

    Message deleteMessage(int id, String sessionId);

    Message getMessage(int id);

    Message replyMessage(Message msg, int parentId);

    Message transformToPresentationMessage(BaseMessage message);

    Message transformToPersistentMessage(MessagePost msg, User user);

    User validateUser(User user);

    void notifySubscribers(Message message);

    Map<String, WebSocketSession> getCurrentActiveSessions();
}
