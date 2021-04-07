// created by Vlad Savchuk 14.11.2019 9:10
package com.savchuk.app.models;


public class User implements Cloneable{
    private String username;
    private String password;
    private Access access;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.access = Access.None;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", access=" + access +
                '}';
    }

    @Override
    protected User clone() throws CloneNotSupportedException {
        return (User) super.clone();
    }
}
