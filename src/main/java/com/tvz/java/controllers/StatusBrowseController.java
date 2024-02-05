package com.tvz.java.controllers;

import com.tvz.java.database.DatabaseManager;
import com.tvz.java.entities.Status;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class StatusBrowseController {
    private final DatabaseManager databaseManager = new DatabaseManager();
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
    private ChoiceBox<String> categoryChoiceBox;
    @FXML
    private TextField searchTextField;
    private List<Status> statusList;

    public void initialize(){
        List<String> categories = Arrays.asList("All", "Furnace", "Current Status", "Efficiency", "Next Maintenance");
        statusList = databaseManager.getStatusFromDatabase();

        categoryChoiceBox.setItems(FXCollections.observableArrayList(categories));
        categoryChoiceBox.getSelectionModel().select(0);

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
    }
    public void onSearchClick(){
        String text = searchTextField.getText().toLowerCase();
        String category = categoryChoiceBox.getValue();

        List<Status> filteredStatus = statusList.stream()
                .filter(status -> {
                    switch (category) {
                        case "All":
                            return status.getFurnace().getName().toLowerCase().contains(text) ||
                                    status.getCurrentStatus().toLowerCase().contains(text) ||
                                    status.getEfficiency().toString().toLowerCase().contains(text) ||
                                    status.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")).toLowerCase().contains(text);
                        case "Furnace":
                            return status.getFurnace().getName().toLowerCase().contains(text);
                        case "Current Status":
                            return status.getCurrentStatus().toLowerCase().contains(text);
                        case "Efficiency":
                            return status.getEfficiency().toString().toLowerCase().contains(text);
                        case "Next Maintenance":
                            return status.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")).toLowerCase().contains(text);
                        default:
                            return false;
                    }
                })
                .toList();
        statusTableView.setItems(FXCollections.observableArrayList(filteredStatus));
    }
}
