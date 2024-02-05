package com.tvz.java.controllers;

import com.tvz.java.Main;
import com.tvz.java.entities.UserRole;
import com.tvz.java.entities.User;
import com.tvz.java.exceptions.LoginException;
import com.tvz.java.exceptions.WrongPasswordException;
import com.tvz.java.exceptions.WrongUsernameException;
import com.tvz.java.files.FileManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.util.Optional;

public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordTextField;
    private final FileManager fileManager = new FileManager();
    public static Optional<User> loggedUser;
    public static Optional<User> getLoggedUser(){
        return loggedUser;
    }
    public static void setLoggedUser(){
        loggedUser = null;
    }

    public void initialize() {
        /*List<User> users = new ArrayList<>();
        users.add(new User("admin", "admin", UserRole.ADMIN));
        users.add(new User("dominik", "password", UserRole.USER));
        users.add(new User("pero", "password", UserRole.USER));
        users.add(new User("guest", "guest", UserRole.GUEST));
        fileManager.writeUsers(users);*/
    }
    @FXML
    protected void onLoginClick(){
        StringBuilder errorMessages = new StringBuilder();

        try{
            validateLogin(usernameTextField.getText(), passwordTextField.getText());
            if (loggedUser.isPresent()){
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("statusBrowseScreen.fxml"));
                Scene scene = null;
                try {
                    scene = new Scene(fxmlLoader.load());
                } catch (IOException e) {
                    logger.error("Failed loading home screen after login", e);
                }
                Main.getStage().setTitle("Status");
                Main.getStage().setScene(scene);
                Main.getStage().show();
            }
        }catch (LoginException | WrongUsernameException | WrongPasswordException e){
            logger.info(e.getMessage());
            errorMessages.append(e.getMessage()).append("!\nPlease enter credentials again!");
        }
        if (!errorMessages.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login error!");
            alert.setHeaderText("Login failed!");
            alert.setContentText(errorMessages.toString());
            alert.showAndWait();
        }
    }

    private void validateLogin(String username, String pass) throws WrongUsernameException, WrongPasswordException {
        loggedUser = fileManager.readUsers().stream()
                    .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(u.hashPassword(pass)))
                    .findFirst();

        if (username.isEmpty()){
            if (pass.isEmpty()){
                throw new WrongPasswordException("Login failed, username and password not entered");
            }
            throw new WrongUsernameException("Login failed, username not entered");
        }
        else if (pass.isEmpty()){
            throw new WrongPasswordException("Login failed, password not entered");
        }
        else{
            throw new LoginException("Login failed, incorrect credentials");
        }
    }

    @FXML
    protected void onGuestClick(){
        loggedUser = fileManager.readUsers().stream()
                    .filter(u -> u.getUserRole().equals(UserRole.ADMIN))
                    .findFirst();

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("statusBrowseScreen.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            logger.error("Failed loading home screen after guest login", e);
        }
        Main.getStage().setTitle("Status");
        Main.getStage().setScene(scene);
        Main.getStage().show();
    }
}
