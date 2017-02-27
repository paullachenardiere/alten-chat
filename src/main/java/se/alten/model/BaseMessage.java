package se.alten.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by pl3731 on 2017-02-02.
 */
@MappedSuperclass
public abstract class BaseMessage implements Serializable {

    @Id
    @GeneratedValue
    private int id;
    @NotNull
    private Timestamp timestamp;
    @ManyToOne
    private User user;
    @Lob
    private String message;
    private Boolean isEdited = false;

    public BaseMessage() {
    }

    public BaseMessage(String message, User user) {
        super();
        this.message = message;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean isEdited() {
        return isEdited;
    }

    public void setEdited(Boolean edited) {
        isEdited = edited;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", timestamp=" + timestamp +
                ", user=" + user +
                ", message='" + message + '\'' +
                ", isEdited=" + isEdited +
                '}';
    }
}
