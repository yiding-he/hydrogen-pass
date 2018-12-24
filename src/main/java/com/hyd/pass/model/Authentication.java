package com.hyd.pass.model;

/**
 * @author yiding.he
 */
public class Authentication implements Cloneable {

    private String username;

    private String password;

    public Authentication() {
    }

    public Authentication(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public final Authentication clone() {
        try {
            return (Authentication) super.clone();
        } catch (CloneNotSupportedException e) {
            // ignore this
            return null;
        }
    }
}
