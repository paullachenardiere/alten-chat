package se.alten.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

/**
 * Created by pl3731 on 2017-01-26.
 */
@Entity
public class User {


    @Id
    @GeneratedValue
    private int userId;
    @NotNull
    private String userName;
    @NotNull
    private String password;

    //TODO Implements fields below =>
    // Email
    // Avatar



    public User() {
    }


    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
