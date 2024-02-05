package com.tvz.java.entities;

public sealed interface Hash permits User{
    String hashPassword(String passwordToHash);
}
