package com.tvz.java.controllers;

import com.tvz.java.database.DatabaseManager;
import com.tvz.java.entities.*;
import com.tvz.java.exceptions.DuplicateInputException;
import com.tvz.java.exceptions.NotDeletableException;
import com.tvz.java.files.FileManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FurnaceInputController {
    private static final Logger logger = LoggerFactory.getLogger(FurnaceInputController.class);
    private final FileManager fileManager = new FileManager();
    private final DatabaseManager databaseManager = new DatabaseManager();
    private List<Changes<?, ?>> changes = fileManager.deserializeChanges();
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
    private List<Furnace> furnaces;
    private Optional<Furnace> selectedFurnace = Optional.empty();
    public void initialize(){
        furnaces = databaseManager.getFurnacesFromDatabase();
        List<String> fuelTypes = Arrays.asList("Oil", "Gas", "Electricity");
        fuelTypeChoiceBox.setItems(FXCollections.observableArrayList(fuelTypes));
        fuelTypeChoiceBox.getSelectionModel().select(0);

        nameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        serialNumTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSerialNumber()));
        fuelTypeTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFuel().getFuelType()));
        powerOutputTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPowerOutput().toString() + " KW"));
        maxTempTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMaxTemp().toString() + " °C"));

        furnaceTableView.setItems(FXCollections.observableArrayList(furnaces));

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
    }

    public void onNewClick(){
        Furnace furnace = getDataFromScreen();
        if (furnace != null){
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
                        databaseManager.addNewFurnaceToDatabase(furnace);
                        changes.add(new Changes<>("Added new furnace", furnace, user.get(), LocalDateTime.now()));
                        fileManager.serializeChanges(changes);
                    }
                });
                refreshTable();
                clearInput();
            }catch (DuplicateInputException e){
                logger.error("Duplicate furnace input", e);
                Alert alert2 = new Alert(Alert.AlertType.ERROR);
                alert2.setTitle("Error!");
                alert2.setHeaderText("Duplicate input!");
                alert2.setContentText("Duplicate furnace cannot be added!");
                alert2.showAndWait();
            }
        }
    }

    public void onEditClick(){
        Furnace furnace = getDataFromScreen();
        if (selectedFurnace.isPresent()){
            furnace.setId(selectedFurnace.get().getId());
        }
        Optional<Furnace> before = Optional.empty();
        for (Furnace f : furnaces){
            if (f.getId().equals(furnace.getId())){
                before = Optional.of(f);
            }
        }
        if (furnace != null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to edit the selected furnace?");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No");
            alert.getButtonTypes().setAll(yesButton, noButton);

            Optional<Furnace> finalBefore = before;
            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == yesButton) {
                    databaseManager.editFurnaceInDatabase(furnace);
                    changes.add(new Changes<>(finalBefore.get(), furnace, user.get(), LocalDateTime.now()));
                    fileManager.serializeChanges(changes);
                }
            });
        }
        refreshTable();
        clearInput();
    }
    public void onDeleteClick(){
        if (selectedFurnace.isPresent()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete the selected furnace?");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No");
            alert.getButtonTypes().setAll(yesButton, noButton);

            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == yesButton) {
                    try {
                        validateDelete(selectedFurnace.get());
                        databaseManager.deleteFurnaceFromDatabase(selectedFurnace.get());
                    }
                    catch (NotDeletableException e) {
                        Alert alert2 = new Alert(Alert.AlertType.ERROR);
                        alert2.setTitle("Error!");
                        alert2.setHeaderText("Unable to delete!");
                        alert2.setContentText("This furnace cannot be deleted!");
                        alert2.showAndWait();
                        logger.error("Failed to delete furnace from database!", e);
                    }
                    changes.add(new Changes<>(selectedFurnace.get(), "Deleted furnace", user.get(), LocalDateTime.now()));
                    fileManager.serializeChanges(changes);
                }
            });
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
        List<Status> statuses = databaseManager.getStatusFromDatabase();
        List<Maintenance> maintenances = databaseManager.getMaintenancesFromDatabase();

        boolean check = statuses.stream().anyMatch(s -> s.getFurnace().equals(furnace)) ||
                        maintenances.stream().anyMatch(m -> m.getFurnace().equals(furnace));

        if (check){
            throw new NotDeletableException();
        }
    }
    public void refreshTable() {
        furnaces = databaseManager.getFurnacesFromDatabase();
        furnaceTableView.setItems(FXCollections.observableArrayList(furnaces));
    }
    public void clearInput(){
        nameTextField.clear();
        serialNumTextField.clear();
        fuelTypeChoiceBox.getSelectionModel().select(0);
        powerOutputTextField.clear();
        maxTempTextField.clear();
    }
}
