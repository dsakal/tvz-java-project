package com.tvz.java.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Changes<S, T> implements Serializable {
    private S before;
    private T after;
    private User user;
    private LocalDateTime dateTime;

    public Changes(S before, T after) {
        this.before = before;
        this.after = after;
    }

    public Changes(S before, T after, User user, LocalDateTime dateTime) {
        this.before = before;
        this.after = after;
        this.user = user;
        this.dateTime = dateTime;
    }

    public S getBefore() {
        return before;
    }

    public void setBefore(S before) {
        this.before = before;
    }

    public T getAfter() {
        return after;
    }

    public void setAfter(T after) {
        this.after = after;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
