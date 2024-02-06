package com.tvz.java.controllers;

import com.tvz.java.database.DatabaseUtils;
import com.tvz.java.entities.Furnace;
import com.tvz.java.entities.Maintenance;
import com.tvz.java.threads.ReadThread;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FurnaceBrowseController {
    private final DatabaseUtils databaseUtils = new DatabaseUtils();
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
    private TextField searchTextField;
    @FXML
    private ChoiceBox<String> searchChoiceBox;
    private List<Furnace> furnaces = new ArrayList<>();

    public void initialize(){
        //furnaces = databaseUtils.readFurnacesDatabase();
        List<String> categories = Arrays.asList("All", "Name", "Serial Number", "Fuel Type", "Power Output", "Max Temperature");
        searchChoiceBox.setItems(FXCollections.observableArrayList(categories));
        searchChoiceBox.getSelectionModel().select(0);

        nameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        serialNumTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSerialNumber()));
        fuelTypeTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFuel().getFuelType()));
        powerOutputTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPowerOutput().toString() + " KW"));
        maxTempTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMaxTemp().toString() + " °C"));

        refreshTable();
    }
    public void onSearchClick() {
        String text = searchTextField.getText().toLowerCase();
        String category = searchChoiceBox.getValue();

        List<Furnace> filteredFurnaces = furnaces.stream()
                .filter(f -> switch (category) {
                    case "All" -> f.getName().toLowerCase().contains(text) ||
                            f.getSerialNumber().toLowerCase().contains(text) ||
                            f.getFuel().getFuelType().toLowerCase().contains(text) ||
                            f.getPowerOutput().toString().toLowerCase().contains(text) ||
                            f.getMaxTemp().toString().toLowerCase().contains(text);
                    case "Name" -> f.getName().toLowerCase().contains(text);
                    case "Serial Number" -> f.getSerialNumber().toLowerCase().contains(text);
                    case "Fuel Type" -> f.getFuel().getFuelType().toLowerCase().contains(text);
                    case "Power Output" -> f.getPowerOutput().toString().toLowerCase().contains(text);
                    case "Max Temperature" -> f.getMaxTemp().toString().toLowerCase().contains(text);
                    default -> false;
                })
                .toList();
        furnaceTableView.setItems(FXCollections.observableArrayList(filteredFurnaces));
    }
    public void readFurnaces(){
        ReadThread<Furnace> readThread = new ReadThread<>(furnaces, Furnace.class);
        Platform.runLater(readThread);
    }
    private void refreshTable() {
        Thread thread = new Thread(() -> {
            readFurnaces();
            Platform.runLater(() -> furnaceTableView.setItems(FXCollections.observableArrayList(furnaces)));
        });
        thread.start();
    }
}
