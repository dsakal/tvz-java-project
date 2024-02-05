package com.tvz.java.controllers;

import com.tvz.java.database.DatabaseManager;
import com.tvz.java.entities.Furnace;
import com.tvz.java.entities.Maintenance;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class MaintenanceBrowseController {
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceBrowseController.class);
    private final DatabaseManager databaseManager = new DatabaseManager();
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
    private TextField searchTextField;
    @FXML
    private ChoiceBox<String> searchChoiceBox;
    private List<Maintenance> maintenances;

    public void initialize() {
        maintenances = databaseManager.getMaintenancesFromDatabase();
        List<String> categories = Arrays.asList("All", "Furnace", "Description", "Category", "Date", "Duration");

        searchChoiceBox.setItems(FXCollections.observableArrayList(categories));
        searchChoiceBox.getSelectionModel().select(0);

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
    }
    public void onSearchClick() {
        String text = searchTextField.getText().toLowerCase();
        String category = searchChoiceBox.getValue();

        List<Maintenance> filteredMaintenance = maintenances.stream()
                .filter(maintenance -> {
                    Furnace furnace = maintenance.getFurnace();
                    switch (category) {
                        case "All":
                            return maintenance.getDescription().toLowerCase().contains(text) ||
                                    maintenance.getCategory().toLowerCase().contains(text) ||
                                    furnace.getName().toLowerCase().contains(text) ||
                                    maintenance.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")).toLowerCase().contains(text) ||
                                    maintenance.getDuration().toString().toLowerCase().contains(text);
                        case "Furnace":
                            return furnace.getName().toLowerCase().contains(text);
                        case "Description":
                            return maintenance.getDescription().toLowerCase().contains(text);
                        case "Category":
                            return maintenance.getCategory().toLowerCase().contains(text);
                        case "Date":
                            return maintenance.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")).toLowerCase().contains(text);
                        case "Duration":
                            return maintenance.getDuration().toString().toLowerCase().contains(text);
                        default:
                            return false;
                    }
                })
                .toList();
        maintenanceTableView.setItems(FXCollections.observableArrayList(filteredMaintenance));
    }
}
