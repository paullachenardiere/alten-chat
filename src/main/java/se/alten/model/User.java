package se.alten.model;

import org.hibernate.validator.constraints.Email;

import javax.annotation.Generated;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.UniqueConstraint;

/**
 * Created by pl3731 on 2017-01-26.
 */
@Entity
public class User {


    @Id
    @GeneratedValue
    private int userId;
    private String userName;
    private String password;

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
