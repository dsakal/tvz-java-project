package com.tvz.java.controllers;

import com.tvz.java.Main;
import com.tvz.java.entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class MenuController {
    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);

    @FXML
    private MenuItem tournaments;
    @FXML
    private MenuItem teams;
    @FXML
    private MenuItem players;
    @FXML
    private Menu history;
    private final Optional<User> user = LoginController.getLoggedUser();
    @FXML
    public void initialize(){
        if (!user.get().getUserRole().getRole().equals("ADMIN")){
            tournaments.setVisible(false);
            teams.setVisible(false);
            players.setVisible(false);
            history.setVisible(false);
        }
    }
    public void showMaintenanceBrowse(){
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("maintenanceBrowseScreen.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            logger.error("Failed loading maintenance browse screen!", e);
        }
        Main.getStage().setTitle("Maintenance");
        Main.getStage().setScene(scene);
        Main.getStage().show();
    }
    public void showMaintenanceEdit(){
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("maintenanceEditScreen.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            logger.error("Failed loading maintenance edit screen!", e);
        }
        Main.getStage().setTitle("Edit maintenance");
        Main.getStage().setScene(scene);
        Main.getStage().show();
    }
    public void showFurnaceBrowse(){
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("furnaceBrowseScreen.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            logger.error("Failed loading furnace browse screen!", e);
        }
        Main.getStage().setTitle("Furnaces");
        Main.getStage().setScene(scene);
        Main.getStage().show();
    }
    public void showFurnaceEdit(){
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("furnaceInputScreen.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            logger.error("Failed loading furnace edit screen!", e);
        }
        Main.getStage().setTitle("Edit furnaces");
        Main.getStage().setScene(scene);
        Main.getStage().show();
    }
    public void showStatusBrowse(){
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("statusBrowseScreen.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            logger.error("Failed loading  screen!", e);
        }
        Main.getStage().setTitle("Status");
        Main.getStage().setScene(scene);
        Main.getStage().show();
    }
    public void showStatusEdit(){
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("statusInputScreen.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            logger.error("Failed loading efficiency edit screen!", e);
        }
        Main.getStage().setTitle("Edit status");
        Main.getStage().setScene(scene);
        Main.getStage().show();
    }
    public void showChanges(){
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("historyScreen.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            logger.error("Failed loading history screen!", e);
        }
        Main.getStage().setTitle("History");
        Main.getStage().setScene(scene);
        Main.getStage().show();
    }
    @FXML
    protected void onLogoutClick(){
        LoginController.setLoggedUser();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("loginScreen.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            logger.error("Failed loading login screen after logout!", e);
        }
        Main.getStage().setTitle("Login");
        Main.getStage().setScene(scene);
        Main.getStage().show();
    }
}
