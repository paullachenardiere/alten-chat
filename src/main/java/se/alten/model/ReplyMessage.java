package se.alten.model;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * Created by pl3731 on 2017-02-02.
 */
@Entity
public class ReplyMessage extends BaseMessage {

    @NotNull
    private int parentId;

    public ReplyMessage() {
    }

    public ReplyMessage(String message, User user, int parentId) {
        super(message, user);
        this.parentId = parentId;
    }

    public int getParentId() {
        return parentId;
    }

    @Override
    public String toString() {
        return "ReplyMessage {" +
                super.toString() +
                ", parentId=" + parentId +
                '}';
    }
}
