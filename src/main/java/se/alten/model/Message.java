package se.alten.model;


import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pl3731 on 2017-01-26.
 */

@Entity
public class Message extends BaseMessage {

    @OneToMany
    @Cascade(CascadeType.ALL)
    private List<ReplyMessage> replies = new ArrayList<>();

    public Message() {
    }

    public Message(String message, User user) {
        super(message, user);
    }

    public List<ReplyMessage> getReplies() {
        return replies;
    }

    public void setReplies(List<ReplyMessage> replies) {
        this.replies = replies;
    }

    public void addReply(ReplyMessage reply) {
        this.replies.add(reply);
    }

    @Override
    public String toString() {
        return "Message {" +
                super.toString() +
                ", replies=" + replies +
                '}';
    }
}
