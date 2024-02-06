package com.tvz.java.controllers;

import com.tvz.java.database.DatabaseUtils;
import com.tvz.java.entities.Changes;
import com.tvz.java.entities.Furnace;
import com.tvz.java.entities.Maintenance;
import com.tvz.java.entities.User;
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
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MaintenanceInputController {
    private final FileUtils fileUtils = new FileUtils();
    private final DatabaseUtils databaseUtils = new DatabaseUtils();
    private List<Changes<?, ?>> changes = fileUtils.deserializeChanges();
    private Optional<User> user = LoginController.getLoggedUser();
    @FXML
    private TableView<Maintenance> maintenanceTableView;
    @FXML
    private TableColumn<Maintenance, String> furnaceTableColumn;
    @FXML
    private TableColumn<Maintenance, String> descriptionTableColumn;
    @FXML
    private TableColumn<Maintenance, String > categoryTableColumn;
    @FXML
    private TableColumn<Maintenance, String> dateTableColumn;
    @FXML
    private TableColumn<Maintenance, String> durationTableColumn;
    @FXML
    private ChoiceBox<Furnace> furnaceChoiceBox;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private ChoiceBox<String> categoryChoiceBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField durationTextField;
    private List<Maintenance> maintenances = new ArrayList<>();
    private List<Furnace> furnaces = new ArrayList<>();
    private Optional<Maintenance> selectedMaintenance = Optional.empty();

    public void initialize(){
        //furnaces = databaseUtils.readFurnacesDatabase();
        //maintenances = databaseUtils.readMaintenancesDatabase();

        List<String> categories = Arrays.asList("Scheduled", "Unscheduled");
        categoryChoiceBox.setItems(FXCollections.observableArrayList(categories));
        categoryChoiceBox.getSelectionModel().select(0);

        refreshChoiceBox();
        //furnaceChoiceBox.getSelectionModel().select(0);

        furnaceTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFurnace().toString()));
        descriptionTableColumn.setCellFactory(tc -> {
            TableCell<Maintenance, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(descriptionTableColumn.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        descriptionTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        categoryTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
        dateTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy."))));
        durationTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDuration().toString() + "h"));

        maintenanceTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedMaintenance = Optional.of(maintenanceTableView.getSelectionModel().getSelectedItem());
                furnaceChoiceBox.getSelectionModel().select(selectedMaintenance.get().getFurnace());
                descriptionTextArea.setText(selectedMaintenance.get().getDescription());
                categoryChoiceBox.getSelectionModel().select(selectedMaintenance.get().getCategory());
                datePicker.setValue(selectedMaintenance.get().getDate());
                durationTextField.setText(selectedMaintenance.get().getDuration().toString());
            }
        });
        durationTextField.textProperty().addListener((obs, old, text) -> {
            if (!text.matches("\\d*")) {
                durationTextField.setText(text.replaceAll("[^\\d]", ""));
            }
        });
        refreshTable();
    }

    public void onNewClick(){
        Maintenance maintenance = getDataFromScreen();
        if (maintenance != null && user.isPresent()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to add a new maintenance?");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No");
            alert.getButtonTypes().setAll(yesButton, noButton);

            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == yesButton) {
                    //databaseUtils.createMaintenanceInDatabase(maintenance);
                    CreateThread<Maintenance> createThread = new CreateThread<>(maintenance);
                    Platform.runLater(createThread);

                    changes.add(new Changes<>("Added new maintenance", maintenance, user.get(), LocalDateTime.now()));
                    fileUtils.serializeChanges(changes);
                }
            });
        }
        refreshTable();
        clearInput();
    }
    public void onEditClick(){
        Maintenance maintenance = getDataFromScreen();
        if (selectedMaintenance.isPresent()){
            maintenance.setId(selectedMaintenance.get().getId());
        }
        Optional<Maintenance> before = maintenances.stream()
                .filter(m -> m.getId().equals(maintenance.getId()))
                .findFirst();

        if (maintenance != null && user.isPresent()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to edit the selected maintenance?");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No");
            alert.getButtonTypes().setAll(yesButton, noButton);

            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == yesButton) {
                    //databaseUtils.updateMaintenanceInDatabase(maintenance);
                    UpdateThread<Maintenance> updateThread = new UpdateThread<>(maintenance);
                    Platform.runLater(updateThread);

                    changes.add(new Changes<>(before.get(), maintenance, user.get(), LocalDateTime.now()));
                    fileUtils.serializeChanges(changes);
                }
            });
        }
        refreshTable();
        clearInput();
    }
    public void onDeleteClick(){
        if (selectedMaintenance.isPresent() && user.isPresent()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete the selected furnace?");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No");
            alert.getButtonTypes().setAll(yesButton, noButton);

            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == yesButton) {
                    //databaseUtils.deleteMaintenanceFromDatabase(selectedMaintenance.get());
                    DeleteThread<Maintenance> deleteThread = new DeleteThread<>(selectedMaintenance.get());
                    Platform.runLater(deleteThread);

                    changes.add(new Changes<>(selectedMaintenance.get(), "Deleted maintenance", user.get(), LocalDateTime.now()));
                    fileUtils.serializeChanges(changes);
                }
            });
        }
        refreshTable();
        clearInput();
    }
    public Maintenance getDataFromScreen(){
        StringBuilder errorMessages = new StringBuilder();

        Furnace furnace = furnaceChoiceBox.getValue();
        if (furnace == null){
            errorMessages.append("A furnace must be selected!\n");
        }
        String description = descriptionTextArea.getText();
        if (description.isEmpty()){
            errorMessages.append("A description must be entered!\n");
        }
        String category = categoryChoiceBox.getValue();
        if (category.isEmpty()){
            errorMessages.append("A category must be selected!\n");
        }
        LocalDate date = datePicker.getValue();
        if (date == null){
            errorMessages.append("Date must be selected!\n");
        }
        String duration = durationTextField.getText();
        if (duration.isEmpty()){
            errorMessages.append("Duration must be entered!\n");
        }
        if (!errorMessages.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input error!");
            alert.setHeaderText("Not all data entered!");
            alert.setContentText(errorMessages.toString());
            alert.showAndWait();
        }else {
            return new Maintenance(1L, furnace, description, category, date, Integer.parseInt(duration));
        }
        return null;
    }
    public void clearInput(){
        furnaceChoiceBox.getSelectionModel().select(0);
        descriptionTextArea.clear();
        categoryChoiceBox.getSelectionModel().select(0);
        datePicker.setValue(null);
        durationTextField.clear();
    }
    public void readFurnaces(){
        ReadThread<Furnace> readThread = new ReadThread<>(furnaces, Furnace.class);
        Platform.runLater(readThread);
    }
    public void readMaintenances(){
        ReadThread<Maintenance> readThread = new ReadThread<>(maintenances, Maintenance.class);
        Platform.runLater(readThread);
    }
    private void refreshTable() {
        Thread thread = new Thread(() -> {
            readMaintenances();
            Platform.runLater(() -> maintenanceTableView.setItems(FXCollections.observableArrayList(maintenances)));
        });
        thread.start();
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
