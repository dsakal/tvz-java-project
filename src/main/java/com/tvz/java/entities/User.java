package com.tvz.java.entities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class User implements Hash, Serializable {
    private static final Logger logger = LoggerFactory.getLogger(User.class);
    private String username;
    private String password;
    private UserRole userRole;

    public User(String username, String password, UserRole userRole) {
        this.username = username;
        this.password = password;
        this.userRole = userRole;
    }
    public static class UserBuilder{
        private String username;
        private String password;
        private UserRole userRole;

        public UserBuilder addUsername(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder addPassword(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder addUserRole(UserRole userRole) {
            this.userRole = userRole;
            return this;
        }

        public User build(){
            return new User(username, password, userRole);
        }
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

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    @Override
    public String hashPassword(String password){
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(password.getBytes(StandardCharsets.UTF_8));
            return String.format("%064x", new BigInteger(1, hash));
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to hash password", e);
        }
        return null;
    }
}
