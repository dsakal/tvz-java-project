package com.tvz.java.controllers;

import com.tvz.java.entities.Changes;
import com.tvz.java.entities.Furnace;
import com.tvz.java.entities.Maintenance;
import com.tvz.java.entities.Status;
import com.tvz.java.files.FileManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoryController {
    private final FileManager fileManager = new FileManager();
    @FXML
    private TableView<Changes<?,?>> changesTableView;
    @FXML
    private TableColumn<Changes<?,?>, String> beforeTableColumn;
    @FXML
    private TableColumn<Changes<?,?>, String> afterTableColumn;
    @FXML
    private TableColumn<Changes<?,?>, String> userTableColumn;
    @FXML
    private TableColumn<Changes<?,?>, String> dateTimeTableColumn;
    private List<Changes<?, ?>> changes = fileManager.deserializeChanges();
    @FXML
    public void initialize(){
        beforeTableColumn.setCellFactory(param -> createWrappedTextCell());
        afterTableColumn.setCellFactory(param -> createWrappedTextCell());

        beforeTableColumn.setCellValueFactory(cellData -> {
            Changes<?,?> changes = cellData.getValue();
            if (changes.getBefore() instanceof Furnace furnace) {
                return new SimpleStringProperty(furnace.getName() + ", " + furnace.getSerialNumber() + ", " + furnace.getFuel().getFuelType() + ", " + furnace.getPowerOutput() + ", "
                        + furnace.getMaxTemp());
            }else if (changes.getBefore() instanceof Maintenance maintenance) {
                return new SimpleStringProperty(maintenance.getFurnace().toString() + ", " + maintenance.getDescription() + ", " + maintenance.getCategory()
                        + ", " + maintenance.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")) + ", "
                        + maintenance.getDuration());
            }else if (changes.getBefore() instanceof Status status) {
                return new SimpleStringProperty(status.getFurnace().toString() + ", " + status.getCurrentStatus() + ", " + status.getEfficiency()
                        + ", " + status.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
            }else if (changes.getBefore() instanceof String str) {
                return new SimpleStringProperty(str);
            }else {
                return new SimpleStringProperty("");
            }
        });
        afterTableColumn.setCellValueFactory(cellData -> {
            Changes<?,?> changes = cellData.getValue();
            if (changes.getAfter() instanceof Furnace furnace) {
                return new SimpleStringProperty(furnace.getName() + ", " + furnace.getSerialNumber() + ", " + furnace.getFuel().getFuelType() + ", " + furnace.getPowerOutput() + ", "
                        + furnace.getMaxTemp());
            }else if (changes.getAfter() instanceof Maintenance maintenance) {
                return new SimpleStringProperty(maintenance.getFurnace().toString() + ", " + maintenance.getDescription() + ", " + maintenance.getCategory()
                        + ", " + maintenance.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")) + ", "
                        + maintenance.getDuration());
            }else if (changes.getAfter() instanceof Status status) {
                return new SimpleStringProperty(status.getFurnace().toString() + ", " + status.getCurrentStatus() + ", " + status.getEfficiency()
                        + ", " + status.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy.")));
            }else if (changes.getAfter() instanceof String str) {
                return new SimpleStringProperty(str);
            }else {
                return new SimpleStringProperty("");
            }
        });
        userTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUser().getUsername()));
        dateTimeTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss"))));


        changesTableView.setItems(FXCollections.observableArrayList(changes));
    }
    private TableCell<Changes<?,?>, String> createWrappedTextCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    Text text = new Text(item);
                    text.setStyle("-fx-text-alignment:justify;");
                    text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(35));
                    setGraphic(text);
                }
            }
        };
    }
}
