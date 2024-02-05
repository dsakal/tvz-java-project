package com.tvz.java.controllers;

import com.tvz.java.database.DatabaseManager;
import com.tvz.java.entities.Furnace;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.Arrays;
import java.util.List;

public class FurnaceBrowseController {
    private final DatabaseManager databaseManager = new DatabaseManager();
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
    private List<Furnace> furnaces;

    public void initialize(){
        furnaces = databaseManager.getFurnacesFromDatabase();
        List<String> categories = Arrays.asList("All", "Name", "Serial Number", "Fuel Type", "Power Output", "Max Temperature");
        searchChoiceBox.setItems(FXCollections.observableArrayList(categories));
        searchChoiceBox.getSelectionModel().select(0);

        nameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        serialNumTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSerialNumber()));
        fuelTypeTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFuel().getFuelType()));
        powerOutputTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPowerOutput().toString() + " KW"));
        maxTempTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMaxTemp().toString() + " °C"));

        furnaceTableView.setItems(FXCollections.observableArrayList(furnaces));
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
}
