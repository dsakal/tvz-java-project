package com.tvz.java.controllers;

import com.tvz.java.database.DatabaseUtils;
import com.tvz.java.entities.Maintenance;
import com.tvz.java.entities.Status;
import com.tvz.java.threads.ReadThread;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatusBrowseController {
    private final DatabaseUtils databaseUtils = new DatabaseUtils();
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
    private List<Status> statusList = new ArrayList<>();

    public void initialize(){
        List<String> categories = Arrays.asList("All", "Furnace", "Current Status", "Efficiency", "Next Maintenance");
        //statusList = databaseUtils.readStatusesFromDatabase();

        categoryChoiceBox.setItems(FXCollections.observableArrayList(categories));
        categoryChoiceBox.getSelectionModel().select(0);

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
        refreshTable();
    }
    public void onSearchClick(){
        String text = searchTextField.getText().toLowerCase();
        String category = categoryChoiceBox.getValue();

        List<Status> filteredStatus = statusList.stream()
                .filter(status -> {
                    switch (category) {
                        case "All":
                            return status.furnace().getName().toLowerCase().contains(text) ||
                                    status.currentStatus().toLowerCase().contains(text) ||
                                    status.efficiency().toString().toLowerCase().contains(text) ||
                                    status.date().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")).toLowerCase().contains(text);
                        case "Furnace":
                            return status.furnace().getName().toLowerCase().contains(text);
                        case "Current Status":
                            return status.currentStatus().toLowerCase().contains(text);
                        case "Efficiency":
                            return status.efficiency().toString().toLowerCase().contains(text);
                        case "Next Maintenance":
                            return status.date().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")).toLowerCase().contains(text);
                        default:
                            return false;
                    }
                })
                .toList();
        statusTableView.setItems(FXCollections.observableArrayList(filteredStatus));
    }
    public void readStatus(){
        ReadThread<Status> readThread = new ReadThread<>(statusList, Status.class);
        Platform.runLater(readThread);
    }
    private void refreshTable() {
        Thread thread = new Thread(() -> {
            readStatus();
            Platform.runLater(() -> statusTableView.setItems(FXCollections.observableArrayList(statusList)));
        });
        thread.start();
    }
}
