package com.tvz.java.database;

import com.tvz.java.entities.Furnace;
import com.tvz.java.entities.Maintenance;
import com.tvz.java.entities.Status;
import com.tvz.java.exceptions.NotDeletableException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface DatabaseAccess {
    Connection connectToDatabase() throws SQLException, IOException;
    List<Furnace> getFurnacesFromDatabase();
    void addNewFurnaceToDatabase(Furnace furnace);
    void editFurnaceInDatabase(Furnace furnace);
    void deleteFurnaceFromDatabase(Furnace furnace) throws NotDeletableException;
    List<Maintenance> getMaintenancesFromDatabase();
    void addNewMaintenanceToDatabase(Maintenance maintenance);
    void editMaintenanceInDatabase(Maintenance maintenance);
    void deleteMaintenanceFromDatabase(Maintenance maintenance);
    List<Status> getStatusFromDatabase();
    void addNewStatusToDatabase(Status status);
    void editStatusInDatabase(Status status);
    void deleteStatusFromDatabase(Status status);
}
