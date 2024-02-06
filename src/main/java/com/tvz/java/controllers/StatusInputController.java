package com.tvz.java.controllers;

import com.tvz.java.database.DatabaseUtils;
import com.tvz.java.entities.*;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class StatusInputController {
    private final DatabaseUtils databaseUtils = new DatabaseUtils();
    private final FileUtils fileAccess = new FileUtils();
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
    private List<Maintenance> maintenances = new ArrayList<>();
    private List<Furnace> furnaces = new ArrayList<>();
    private List<Status> statusList = new ArrayList<>();
    private Optional<Status> selectedStatus = Optional.empty();

    public void initialize(){
        List<String> currentStatuses = Arrays.asList("In production", "Under maintenance", "Not in use");

        /*furnaces = databaseUtils.readFurnacesDatabase();
        maintenances = databaseUtils.readMaintenancesDatabase();
        statusList = databaseUtils.readStatusesFromDatabase();*/

        statusChoiceBox.setItems(FXCollections.observableArrayList(currentStatuses));
        statusChoiceBox.getSelectionModel().select(0);

        refreshChoiceBox();

        furnaceTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().furnace().toString()));
        statusTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().currentStatus()));
        efficiencyTableColumn.setCellValueFactory(cellData -> {
            double efficiencyValue = cellData.getValue().efficiency();
            if (efficiencyValue == (int) efficiencyValue) {
                return new SimpleStringProperty(String.format("%.0f%%", efficiencyValue));
            } else {
                return new SimpleStringProperty(String.format("%.2f%%", efficiencyValue));
            }
        });
        dateTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().date().format(DateTimeFormatter.ofPattern("dd.MM.yyyy."))));

        statusTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedStatus = Optional.of(statusTableView.getSelectionModel().getSelectedItem());
                furnaceChoiceBox.getSelectionModel().select(selectedStatus.get().furnace());
                statusChoiceBox.getSelectionModel().select(selectedStatus.get().currentStatus());
                datePicker.setValue(selectedStatus.get().date());
            }
        });
        refreshTable();
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
                    //databaseUtils.createStatusInDatabase(status);
                    CreateThread<Status> createThread = new CreateThread<>(status);
                    Platform.runLater(createThread);
                    changes.add(new Changes<>("Added new status", status, user.get(), LocalDateTime.now()));
                    fileAccess.serializeChanges(changes);
                }
            });
        }
        refreshTable();
        clearInput();
    }

    public void onEditClick(){
        Status statusFromScreen = getDataFromScreen();
        Status status = selectedStatus.map(selected -> new Status(selected.id(), statusFromScreen.furnace(),
                        statusFromScreen.currentStatus(), statusFromScreen.efficiency(), statusFromScreen.date()))
                .orElse(statusFromScreen);
        Optional<Status> before = statusList.stream()
                .filter(s -> s.id().equals(status.id()))
                .findFirst();

        if (status != null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to edit the selected status?");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No");
            alert.getButtonTypes().setAll(yesButton, noButton);

            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == yesButton) {
                    //databaseUtils.updateStatusInDatabase(status);
                    UpdateThread<Status> updateThread = new UpdateThread<>(status);
                    Platform.runLater(updateThread);

                    changes.add(new Changes<>(before.get(), status, user.get(), LocalDateTime.now()));
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
                    //databaseUtils.deleteStatusFromDatabase(selectedStatus.get());
                    DeleteThread<Status> deleteThread = new DeleteThread<>(selectedStatus.get());
                    Platform.runLater(deleteThread);

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
    public void readStatus(){
        ReadThread<Status> readThread = new ReadThread<>(statusList, Status.class);
        Platform.runLater(readThread);
    }
    private void refreshTable() {
        Thread thread = new Thread(() -> {
            readFurnaces();
            readMaintenances();
            readStatus();
            Platform.runLater(() -> statusTableView.setItems(FXCollections.observableArrayList(statusList)));
        });
        thread.start();
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
        Integer maintenanceDowntime = maintenances.stream()
                .filter(m -> m.getDate().isAfter(LocalDate.now().minusYears(1)) &&
                        m.getDate().isBefore(LocalDate.now()) &&
                        m.getFurnace().equals(furnace))
                .mapToInt(Maintenance::getDuration)
                .sum();
        efficiency -= (((furnace.getPowerOutput()*16)/1000) + ((double) maintenanceDowntime/24));
        return efficiency;
    }
    public void readFurnaces(){
        ReadThread<Furnace> readThread = new ReadThread<>(furnaces, Furnace.class);
        Platform.runLater(readThread);
    }
    public void readMaintenances(){
        ReadThread<Maintenance> readThread = new ReadThread<>(maintenances, Maintenance.class);
        Platform.runLater(readThread);
    }
    private void refreshChoiceBox() {
        Thread thread = new Thread(() -> {
            readFurnaces();
            Platform.runLater(() -> furnaceChoiceBox.setItems(FXCollections.observableArrayList(furnaces)));
            Platform.runLater(() -> furnaceChoiceBox.getSelectionModel().select(0));
        });
        thread.start();
    }
}
