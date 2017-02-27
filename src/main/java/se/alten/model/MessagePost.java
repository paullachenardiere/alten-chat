package se.alten.model;

/**
 * Created by pl3731 on 2017-02-23.
 */
public class MessagePost {

    private int id;
    private String message;
    private int userId;

    public MessagePost () {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "MessagePost{" +
                "id=" + id +
                ", message='" + message + '\'' +
                ", userId=" + userId +
                '}';
    }
}
