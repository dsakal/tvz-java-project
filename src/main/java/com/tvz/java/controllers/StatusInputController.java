package com.tvz.java.controllers;

import com.tvz.java.database.DatabaseManager;
import com.tvz.java.entities.*;
import com.tvz.java.files.FileManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class StatusInputController {
    private final DatabaseManager databaseManager = new DatabaseManager();
    private final FileManager fileAccess = new FileManager();
    private List<Changes<?, ?>> changes = fileAccess.deserializeChanges();
    private Optional<User> user = LoginController.getLoggedUser();
    @FXML
    private TableView<Status> statusTableView;
    @FXML
    private TableColumn<Status, String> furnaceTableColumn;
    @FXML
    private TableColumn<Status, String> statusTableColumn;
    @FXML
    private TableColumn<Status, String > efficiencyTableColumn;
    @FXML
    private TableColumn<Status, String> dateTableColumn;
    @FXML
    private ChoiceBox<Furnace> furnaceChoiceBox;
    @FXML
    private ChoiceBox<String> statusChoiceBox;
    @FXML
    private DatePicker datePicker;
    private List<Maintenance> maintenances;
    private List<Furnace> furnaces;
    private List<Status> statusList;
    private Optional<Status> selectedStatus = Optional.empty();

    public void initialize(){
        List<String> currentStatuses = Arrays.asList("In production", "Under maintenance", "Not in use");

        furnaces = databaseManager.getFurnacesFromDatabase();
        maintenances = databaseManager.getMaintenancesFromDatabase();
        statusList = databaseManager.getStatusFromDatabase();

        statusChoiceBox.setItems(FXCollections.observableArrayList(currentStatuses));
        statusChoiceBox.getSelectionModel().select(0);

        furnaceChoiceBox.setItems(FXCollections.observableArrayList(furnaces));
        furnaceChoiceBox.getSelectionModel().select(0);

        furnaceTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFurnace().toString()));
        statusTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCurrentStatus()));
        efficiencyTableColumn.setCellValueFactory(cellData -> {
            double efficiencyValue = cellData.getValue().getEfficiency();
            if (efficiencyValue == (int) efficiencyValue) {
                return new SimpleStringProperty(String.format("%.0f%%", efficiencyValue));
            } else {
                return new SimpleStringProperty(String.format("%.2f%%", efficiencyValue));
            }
        });
        dateTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy."))));
        statusTableView.setItems(FXCollections.observableArrayList(statusList));

        statusTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedStatus = Optional.of(statusTableView.getSelectionModel().getSelectedItem());
                furnaceChoiceBox.getSelectionModel().select(selectedStatus.get().getFurnace());
                statusChoiceBox.getSelectionModel().select(selectedStatus.get().getCurrentStatus());
                datePicker.setValue(selectedStatus.get().getDate());
            }
        });
    }

    public void onNewClick(){
        Status status = getDataFromScreen();
        if (status != null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to add a new status?");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No");
            alert.getButtonTypes().setAll(yesButton, noButton);

            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == yesButton) {
                    databaseManager.addNewStatusToDatabase(status);
                    changes.add(new Changes<>("Added new status", status, user.get(), LocalDateTime.now()));
                    fileAccess.serializeChanges(changes);
                }
            });
        }
        refreshTable();
        clearInput();
    }

    public void onEditClick(){
        Status status = getDataFromScreen();
        if (selectedStatus.isPresent()){
            status.setId(selectedStatus.get().getId());
        }
        Optional<Status> before = Optional.empty();
        for (Status s : statusList){
            if (s.getId().equals(status.getId())){
                before = Optional.of(s);
            }
        }
        if (status != null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to edit the selected status?");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No");
            alert.getButtonTypes().setAll(yesButton, noButton);

            Optional<Status> finalBefore = before;
            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == yesButton) {
                    databaseManager.editStatusInDatabase(status);
                    changes.add(new Changes<>(finalBefore.get(), status, user.get(), LocalDateTime.now()));
                    fileAccess.serializeChanges(changes);
                }
            });
        }
        refreshTable();
        clearInput();
    }
    public void onDeleteClick(){
        if (selectedStatus.isPresent()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete the selected status?");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No");
            alert.getButtonTypes().setAll(yesButton, noButton);

            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == yesButton) {
                    databaseManager.deleteStatusFromDatabase(selectedStatus.get());
                    changes.add(new Changes<>(selectedStatus.get(), "Deleted status", user.get(), LocalDateTime.now()));
                    fileAccess.serializeChanges(changes);
                }
            });
        }
        refreshTable();
        clearInput();
    }
    public Status getDataFromScreen(){
        StringBuilder errorMessages = new StringBuilder();

        Furnace furnace = furnaceChoiceBox.getValue();
        if (furnace == null){
            errorMessages.append("A furnace must be selected!\n");
        }
        String currentStatus = statusChoiceBox.getValue();
        if (currentStatus.isEmpty()){
            errorMessages.append("Current status must be selected!\n");
        }
        LocalDate date = datePicker.getValue();
        if (date == null){
            errorMessages.append("Date must be selected!\n");
        }
        Double efficiency = calculateEfficiency(furnace);
        if (!errorMessages.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input error!");
            alert.setHeaderText("Not all data entered!");
            alert.setContentText(errorMessages.toString());
            alert.showAndWait();
        }else {
            return new Status(1L, furnace, currentStatus, efficiency, date);
        }
        return null;
    }
    public void refreshTable() {
        statusList = databaseManager.getStatusFromDatabase();
        statusTableView.setItems(FXCollections.observableArrayList(statusList));
    }
    public void clearInput(){
        furnaceChoiceBox.getSelectionModel().select(0);
        statusChoiceBox.getSelectionModel().select(0);
        datePicker.setValue(null);
    }
    public Double calculateEfficiency(Furnace furnace){
        Double efficiency;
        if (furnace.getFuel().equals(FuelType.ELECTRICITY)){
            efficiency = 75.00;
        }else {
            efficiency = 65.00;
        }
        Integer maintenanceDowntime = 0;
        for (Maintenance m : maintenances){
            if (m.getDate().isAfter(ChronoLocalDate.from(LocalDateTime.now().minusYears(1))) && m.getDate().isBefore(ChronoLocalDate.from(LocalDateTime.now()))) {
                if (m.getFurnace().equals(furnace)) {
                    maintenanceDowntime += m.getDuration();
                }
            }
        }
        efficiency -= (((furnace.getPowerOutput()*16)/1000) + ((double) maintenanceDowntime/24));
        return efficiency;
    }
}
