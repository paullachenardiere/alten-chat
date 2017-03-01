package se.alten.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import se.alten.model.Message;
import se.alten.model.ReplyMessage;
import se.alten.model.User;

import javax.persistence.*;
import java.util.List;

/**
 * Created by pl3731 on 2017-01-31.
 */
@Repository
@Transactional
public class ChatMessageRepo {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager em;

    /**
     * message persistance
     */
    @SuppressWarnings("JpaQlInspection")
    public List<Message> getAll() {
        TypedQuery<Message> query = em.createQuery("SELECT m FROM Message m", Message.class);
        return query.getResultList();
    }

    public Message addMessage(Message message) {
        em.persist(message);
        em.flush();
        return message;
    }

    public ReplyMessage updateReplyMessage(ReplyMessage replyMessage) {
        ReplyMessage message = getReplyMessage(replyMessage.getId());

        if (message != null) {
            if (!message.getMessage().equals(replyMessage.getMessage())) {
                message.setMessage(replyMessage.getMessage());
                message.setEdited(true);
            }
            em.merge(message);
            em.flush();
        }
        return message;
    }


    public Message updateMessage(Message message) {
        return em.merge(message);
    }

    public Message updateMessageReplies(Message editedMessage) throws NoResultException {

        Message message = getMessage(editedMessage.getId());

        if (message != null) {
            message.setReplies(editedMessage.getReplies());
            if (!message.getMessage().equals(editedMessage.getMessage())) {
                message.setMessage(editedMessage.getMessage());
                message.setEdited(true);
            }
            em.merge(message);
            em.flush();
        }
        return message;
    }

    public Message getMessage(int id) throws NoResultException {
        Message message;
        //noinspection JpaQlInspection
        Query query = em.createQuery("SELECT m FROM Message m WHERE m.id = :id");
        query.setParameter("id", id);

        String customErrorMessage = "Message don't exists in the database.";

        try {
            message = (Message) query.getSingleResult();

        } catch (NoResultException | EmptyResultDataAccessException e) {
            throw new NoResultException(customErrorMessage);
        }
        return message;
    }

    public ReplyMessage getReplyMessage(int id) throws NoResultException {
        ReplyMessage replyMessage;
        //noinspection JpaQlInspection
        Query query = em.createQuery("SELECT m FROM ReplyMessage m WHERE m.id = :id");
        query.setParameter("id", id);

        String customErrorMessage = "ReplyMessage don't exists in the database.";

        try {
            replyMessage = (ReplyMessage) query.getSingleResult();

        } catch (NoResultException | EmptyResultDataAccessException e) {
            throw new NoResultException(customErrorMessage);
        }
        return replyMessage;
    }

    public void deleteMessage(int id) throws NoResultException {
        Message message = getMessage(id);
        if (message != null) {
            try {
                em.remove(message);
            } catch (NoResultException e) {
                throw new NoResultException("Message don't exists in the database.");
            }
        }
    }

    /**
     * user persistance
     */
    public void createUser(User user) {
        em.persist(user);
    }

    public User getUser(String userName) throws NoResultException {
        User user = null;
        //noinspection JpaQlInspection
        Query query = em.createQuery("SELECT u FROM User u WHERE u.userName = :userName");
        query.setParameter("userName", userName);

        try {
            user = (User) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoResultException("User don't exists in the database.");
        }

        return user;

    }

    public User getUser(int userId) throws NoResultException {
        User user = null;
        //noinspection JpaQlInspection
        Query query = em.createQuery("SELECT u FROM User u WHERE u.userId = :userId");
        query.setParameter("userId", userId);

        String customErrorMessage = "User don't exists in the database.";
        try {
            user = (User) query.getSingleResult();
        } catch (NoResultException | EmptyResultDataAccessException e) {
            throw new NoResultException(customErrorMessage);
        }

        return user;

    }

    @SuppressWarnings("JpaQlInspection")
    public List<User> getUsers() {
        TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
        return query.getResultList();

    }



}
