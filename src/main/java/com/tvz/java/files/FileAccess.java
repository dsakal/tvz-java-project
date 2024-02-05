package com.tvz.java.files;

import com.tvz.java.entities.Changes;
import com.tvz.java.entities.User;

import java.util.List;

public interface FileAccess {
    List<User> readUsers();
    void writeUsers(List<User> users);
    void serializeChanges(List<Changes<?, ?>> changes);
    List<Changes<?, ?>> deserializeChanges();
}
