package com.tvz.java.files;

import com.tvz.java.entities.Changes;
import com.tvz.java.entities.User;
import com.tvz.java.entities.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class FileUtils implements FileAccess{
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    private static final String USERS_FILE = "dat\\users.txt";
    private static final String CHANGES_FILE = "dat\\changes.dat";
    @Override
    public Set<User> readUsers() {
        Set<User> users = new HashSet<>();
        try (Scanner in = new Scanner(new File(USERS_FILE))) {
            while (in.hasNextLine()){
                String[] split = in.nextLine().split(",");
                Optional<UserRole> role = Optional.empty();
                switch (split[2]) {
                    case "ADMIN" -> role = Optional.of(UserRole.ADMIN);
                    case "USER" -> role = Optional.of(UserRole.USER);
                    case "GUEST" -> role = Optional.of(UserRole.GUEST);
                }
                users.add(new User.UserBuilder()
                        .addUsername(split[0])
                        .addPassword(split[1])
                        .addUserRole(role.get())
                        .build());
            }
        } catch (IOException e) {
            logger.error("Failed reading users from user file!", e);
        }
        return users;
    }
    @Override
    public void writeUsers(Set<User> users){
        for (User u : users){
            try (PrintWriter out = new PrintWriter(new FileOutputStream("dat\\users.txt", true))) {
                out.println(u.getUsername() + "," + u.hashPassword(u.getPassword()) + "," + u.getUserRole().getRole());
            } catch (IOException e) {
                logger.error("Failed to write users to user file!", e);
            }
        }
    }
    @Override
    public void serializeChanges(List<Changes<?, ?>> changes){
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(CHANGES_FILE))) {
            out.writeObject(changes);
        } catch (IOException e) {
            logger.error("Failed to serialize changes file", e);
        }
    }
    @Override
    public List<Changes<?, ?>> deserializeChanges() {
        List<Changes<?, ?>>  changes = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(CHANGES_FILE))) {
            changes = (List<Changes<?, ?>>) in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            logger.error("Failed to deserialize changes file", ex);
        }
        return changes;
    }
}
