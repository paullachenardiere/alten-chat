package se.alten.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
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
    @NotNull
    private int userId;
    @Lob
    private String message;
    private Boolean isEdited = false;

    public BaseMessage() {
    }

    public BaseMessage(String message, int userId) {
        super();
        this.message = message;
        this.userId = userId;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    @Override
    public String toString() {
        return "id=" + id +
                ", timestamp=" + timestamp +
                ", userId=" + userId +
                ", message='" + message +
                ", isEdited=" + isEdited;
    }
}
