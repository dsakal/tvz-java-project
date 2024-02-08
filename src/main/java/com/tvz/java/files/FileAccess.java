package com.tvz.java.files;

import com.tvz.java.entities.Changes;
import com.tvz.java.entities.User;

import java.util.List;
import java.util.Set;

public interface FileAccess {
    Set<User> readUsers();
    void writeUsers(Set<User> users);
    void serializeChanges(List<Changes<?, ?>> changes);
    List<Changes<?, ?>> deserializeChanges();
}
