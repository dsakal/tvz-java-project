package com.tvz.java.controllers;

import com.tvz.java.database.DatabaseManager;
import com.tvz.java.entities.Changes;
import com.tvz.java.entities.Furnace;
import com.tvz.java.entities.Maintenance;
import com.tvz.java.entities.User;
import com.tvz.java.files.FileManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class MaintenanceInputController {
    private final FileManager fileManager = new FileManager();
    private final DatabaseManager databaseManager = new DatabaseManager();
    private List<Changes<?, ?>> changes = fileManager.deserializeChanges();
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
    private List<Maintenance> maintenances;
    private List<Furnace> furnaces;
    private Optional<Maintenance> selectedMaintenance = Optional.empty();

    public void initialize(){
        furnaces = databaseManager.getFurnacesFromDatabase();
        maintenances = databaseManager.getMaintenancesFromDatabase();

        List<String> categories = Arrays.asList("Scheduled", "Unscheduled");
        categoryChoiceBox.setItems(FXCollections.observableArrayList(categories));
        categoryChoiceBox.getSelectionModel().select(0);

        furnaceChoiceBox.setItems(FXCollections.observableArrayList(furnaces));
        furnaceChoiceBox.getSelectionModel().select(0);

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
        maintenanceTableView.setItems(FXCollections.observableArrayList(maintenances));

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
    }

    public void onNewClick(){
        Maintenance maintenance = getDataFromScreen();
        if (maintenance != null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to add a new maintenance?");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No");
            alert.getButtonTypes().setAll(yesButton, noButton);

            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == yesButton) {
                    databaseManager.addNewMaintenanceToDatabase(maintenance);
                    changes.add(new Changes<>("Added new maintenance", maintenance, user.get(), LocalDateTime.now()));
                    fileManager.serializeChanges(changes);
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
        Optional<Maintenance> before = Optional.empty();
        for (Maintenance m : maintenances){
            if (m.getId().equals(maintenance.getId())){
                before = Optional.of(m);
            }
        }
        if (maintenance != null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to edit the selected maintenance?");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No");
            alert.getButtonTypes().setAll(yesButton, noButton);

            Optional<Maintenance> finalBefore = before;
            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == yesButton) {
                    databaseManager.editMaintenanceInDatabase(maintenance);
                    changes.add(new Changes<>(finalBefore.get(), maintenance, user.get(), LocalDateTime.now()));
                    fileManager.serializeChanges(changes);
                }
            });
        }
        refreshTable();
        clearInput();
    }
    public void onDeleteClick(){
        if (selectedMaintenance.isPresent()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete the selected furnace?");

            ButtonType yesButton = new ButtonType("Yes");
            ButtonType noButton = new ButtonType("No");
            alert.getButtonTypes().setAll(yesButton, noButton);

            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == yesButton) {
                    databaseManager.deleteMaintenanceFromDatabase(selectedMaintenance.get());
                    changes.add(new Changes<>(selectedMaintenance.get(), "Deleted maintenance", user.get(), LocalDateTime.now()));
                    fileManager.serializeChanges(changes);
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
    public void refreshTable() {
        maintenances = databaseManager.getMaintenancesFromDatabase();
        maintenanceTableView.setItems(FXCollections.observableArrayList(maintenances));
    }
    public void clearInput(){
        furnaceChoiceBox.getSelectionModel().select(0);
        descriptionTextArea.clear();
        categoryChoiceBox.getSelectionModel().select(0);
        datePicker.setValue(null);
        durationTextField.clear();
    }
}
