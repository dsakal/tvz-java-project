package com.tvz.java.controllers;

import com.tvz.java.database.DatabaseUtils;
import com.tvz.java.entities.*;
import com.tvz.java.exceptions.DuplicateInputException;
import com.tvz.java.exceptions.NotDeletableException;
import com.tvz.java.files.FileUtils;
import com.tvz.java.threads.CreateThread;
import com.tvz.java.threads.DeleteThread;
import com.tvz.java.threads.ReadThread;
import com.tvz.java.threads.UpdateThread;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FurnaceInputController {
    private static final Logger logger = LoggerFactory.getLogger(FurnaceInputController.class);
    private final FileUtils fileUtils = new FileUtils();
    private final DatabaseUtils databaseUtils = new DatabaseUtils();
    private List<Changes<?, ?>> changes = fileUtils.deserializeChanges();
    private Optional<User> user = LoginController.getLoggedUser();
    @FXML
    private TableView<Furnace> furnaceTableView;
    @FXML
    private TableColumn<Furnace, String> nameTableColumn;
    @FXML
    private TableColumn<Furnace, String> serialNumTableColumn;
    @FXML
    private TableColumn<Furnace, String> fuelTypeTableColumn;
    @FXML
    private TableColumn<Furnace, String> powerOutputTableColumn;
    @FXML
    private TableColumn<Furnace, String> maxTempTableColumn;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField serialNumTextField;
    @FXML
    private ChoiceBox<String> fuelTypeChoiceBox;
    @FXML
    private TextField powerOutputTextField;
    @FXML
    private TextField maxTempTextField;
    private List<Furnace> furnaces = new ArrayList<>();
    private List<Maintenance> maintenances = new ArrayList<>();
    private List<Status> statuses = new ArrayList<>();
    private Optional<Furnace> selectedFurnace = Optional.empty();
    public void initialize() {
        readMaintenances();
        readStatus();
        List<String> fuelTypes = Arrays.asList("Oil", "Gas", "Electricity");
        fuelTypeChoiceBox.setItems(FXCollections.observableArrayList(fuelTypes));
        fuelTypeChoiceBox.getSelectionModel().select(0);

        nameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        serialNumTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSerialNumber()));
        fuelTypeTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFuel().getFuelType()));
        powerOutputTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPowerOutput().toString() + " KW"));
        maxTempTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMaxTemp().toString() + " °C"));

        furnaceTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedFurnace = Optional.of(furnaceTableView.getSelectionModel().getSelectedItem());
                nameTextField.setText(selectedFurnace.get().getName());
                serialNumTextField.setText(selectedFurnace.get().getSerialNumber());
                fuelTypeChoiceBox.getSelectionModel().select(selectedFurnace.get().getFuel().getFuelType());
                powerOutputTextField.setText(selectedFurnace.get().getPowerOutput().toString());
                maxTempTextField.setText(selectedFurnace.get().getMaxTemp().toString());
            }
        });
        powerOutputTextField.textProperty().addListener((obs, old, text) -> {
            if (!text.matches("\\d*(\\.\\d*)?")) {
                powerOutputTextField.setText(old);
            }
        });
        maxTempTextField.textProperty().addListener((obs, old, text) -> {
            if (!text.matches("\\d*")) {
                maxTempTextField.setText(text.replaceAll("[^\\d]", ""));
            }
        });
        refreshTable();
    }

    public void onNewClick(){
        Furnace furnace = getDataFromScreen();
        if (furnace != null && user.isPresent()){
            try{
                checkDuplicate(furnace);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure you want to add a new furnace?");

                ButtonType yesButton = new ButtonType("Yes");
                ButtonType noButton = new ButtonType("No");
                alert.getButtonTypes().setAll(yesButton, noButton);

                alert.showAndWait().ifPresent(buttonType -> {
                    if (buttonType == yesButton) {
                        //databaseUtils.createFurnaceInDatabase(furnace);

                        CreateThread<Furnace> createThread = new CreateThread<>(furnace);
                        Platform.runLater(createThread);

                        changes.add(new Changes<>("Added new furnace", furnace, user.get(), LocalDateTime.now()));
                        fileUtils.serializeChanges(changes);
                    }
                });
            }catch (DuplicateInputException e){
                logger.error("Duplicate furnace input", e);
                Alert alert2 = new Alert(Alert.AlertType.ERROR);
                alert2.setTitle("Error!");
                alert2.setHeaderText("Duplicate input!");
                alert2.setContentText("Duplicate furnace cannot be added!");
                alert2.showAndWait();
            }
        }
        refreshTable();
        clearInput();
    }

    public void onEditClick(){
        Furnace furnace = getDataFromScreen();
        if (selectedFurnace.isPresent()){
            furnace.setId(selectedFurnace.get().getId());
        }

        Optional<Furnace> before = furnaces.stream()
                .filter(f -> f.getId().equals(furnace.getId()))
                .findFirst();

        if (furnace != null && user.isPresent()){
            try{
                checkDuplicate(furnace);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure you want to edit the selected furnace?");

                ButtonType yesButton = new ButtonType("Yes");
                ButtonType noButton = new ButtonType("No");
                alert.getButtonTypes().setAll(yesButton, noButton);

                alert.showAndWait().ifPresent(buttonType -> {
                    if (buttonType == yesButton) {
                        //databaseUtils.updateFurnaceInDatabase(furnace);

                        UpdateThread<Furnace> updateThread = new UpdateThread<>(furnace);
                        Platform.runLater(updateThread);

                        changes.add(new Changes<>(before.get(), furnace, user.get(), LocalDateTime.now()));
                        fileUtils.serializeChanges(changes);
                    }
                });
            }catch (DuplicateInputException e){
                logger.error("Duplicate furnace input", e);
                Alert alert2 = new Alert(Alert.AlertType.ERROR);
                alert2.setTitle("Error!");
                alert2.setHeaderText("Duplicate input!");
                alert2.setContentText("Duplicate furnace cannot be added!");
                alert2.showAndWait();
            }
        }
        refreshTable();
        clearInput();
    }
    public void onDeleteClick(){
        if (selectedFurnace.isPresent() && user.isPresent()){
            try{
                validateDelete(selectedFurnace.get());
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure you want to delete the selected furnace?");

                ButtonType yesButton = new ButtonType("Yes");
                ButtonType noButton = new ButtonType("No");
                alert.getButtonTypes().setAll(yesButton, noButton);

                alert.showAndWait().ifPresent(buttonType -> {
                    if (buttonType == yesButton) {
                        //databaseUtils.createFurnaceInDatabase(furnace);

                        DeleteThread<Furnace> deleteThread = new DeleteThread<>(selectedFurnace.get());
                        Platform.runLater(deleteThread);

                        changes.add(new Changes<>(selectedFurnace.get(), "Deleted furnace", user.get(), LocalDateTime.now()));
                        fileUtils.serializeChanges(changes);
                    }
                });
            }catch (NotDeletableException e) {
                Alert alert2 = new Alert(Alert.AlertType.ERROR);
                alert2.setTitle("Error!");
                alert2.setHeaderText("Unable to delete!");
                alert2.setContentText("This furnace cannot be deleted!");
                alert2.showAndWait();
                logger.error("Failed to delete furnace from database!", e);
            }
        }
        refreshTable();
        clearInput();
    }
    public Furnace getDataFromScreen(){
        StringBuilder errorMessages = new StringBuilder();

        String name = nameTextField.getText();
        if (name.isEmpty()){
            errorMessages.append("Name must be entered!\n");
        }
        String serialNumber = serialNumTextField.getText();
        if (serialNumber.isEmpty()){
            errorMessages.append("Serial number must be entered!\n");
        }
        String fuelType = fuelTypeChoiceBox.getValue();
        if (fuelType.isEmpty()){
            errorMessages.append("Fuel type must be selected!\n");
        }
        String powerOutput = powerOutputTextField.getText();
        if (powerOutput.isEmpty()){
            errorMessages.append("Power output must be entered!\n");
        }
        String maxTemp = maxTempTextField.getText();
        if (maxTemp.isEmpty()){
            errorMessages.append("Maximum temperature must be entered!\n");
        }
        Optional<FuelType> fuel = Optional.empty();
        switch (fuelType.toLowerCase()) {
            case "oil" -> fuel = Optional.of(FuelType.OIL);
            case "gas" -> fuel = Optional.of(FuelType.GAS);
            case "electricity" -> fuel = Optional.of(FuelType.ELECTRICITY);
        }
        if (!errorMessages.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input error!");
            alert.setHeaderText("Not all data entered!");
            alert.setContentText(errorMessages.toString());
            alert.showAndWait();
        }else {
            return new Furnace(1L, name, serialNumber, fuel.get(), Double.parseDouble(powerOutput), Integer.parseInt(maxTemp));
        }
        return null;
    }
    public void checkDuplicate(Furnace furnace) throws DuplicateInputException {
        boolean furnaceExists = furnaces.stream()
                .anyMatch(f -> f.getName().equals(furnace.getName()) &&
                        f.getSerialNumber().equals(furnace.getSerialNumber()) &&
                        f.getFuel().equals(furnace.getFuel()) &&
                        f.getMaxTemp().equals(furnace.getMaxTemp()) &&
                        f.getPowerOutput().equals(furnace.getPowerOutput()));
        if (furnaceExists){
            throw new DuplicateInputException();
        }
    }
    public void validateDelete(Furnace furnace){
        boolean check = statuses.stream().anyMatch(s -> s.furnace().equals(furnace)) ||
                        maintenances.stream().anyMatch(m -> m.getFurnace().equals(furnace));

        if (check){
            throw new NotDeletableException();
        }
    }
    public void readFurnaces(){
        ReadThread<Furnace> readThread = new ReadThread<>(furnaces, Furnace.class);
        Platform.runLater(readThread);
    }
    public void readMaintenances(){
        ReadThread<Maintenance> readThread = new ReadThread<>(maintenances, Maintenance.class);
        Platform.runLater(readThread);
    }
    public void readStatus(){
        ReadThread<Status> readThread = new ReadThread<>(statuses, Status.class);
        Platform.runLater(readThread);
    }
    public void clearInput(){
        nameTextField.clear();
        serialNumTextField.clear();
        fuelTypeChoiceBox.getSelectionModel().select(0);
        powerOutputTextField.clear();
        maxTempTextField.clear();
    }
    private void refreshTable() {
        Thread thread = new Thread(() -> {
            readFurnaces();
            Platform.runLater(() -> furnaceTableView.setItems(FXCollections.observableArrayList(furnaces)));
        });
        thread.start();
    }
}
