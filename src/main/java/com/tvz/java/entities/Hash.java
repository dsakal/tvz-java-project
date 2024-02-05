package com.tvz.java.entities;

import com.tvz.java.exceptions.UnsupportedAlgorithmException;

public sealed interface Hash permits User{
    String hashPassword(String passwordToHash) throws UnsupportedAlgorithmException;
}
