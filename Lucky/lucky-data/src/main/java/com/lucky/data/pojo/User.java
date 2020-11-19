package com.lucky.data.pojo;

import com.lucky.framework.annotation.Component;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/16 18:43
 */
@Component
public class User {

    private Integer id=1;
    private String username="JACK";
    private String password="PA$$W0RD";

    public User() {
    }

    public User(Integer id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("id=").append(id);
        sb.append(", username='").append(username).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
