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
    List<Furnace> readFurnacesDatabase();
    void createFurnaceInDatabase(Furnace furnace);
    void updateFurnaceInDatabase(Furnace furnace);
    void deleteFurnaceFromDatabase(Furnace furnace) throws NotDeletableException;
    List<Maintenance> readMaintenancesDatabase();
    void createMaintenanceInDatabase(Maintenance maintenance);
    void updateMaintenanceInDatabase(Maintenance maintenance);
    void deleteMaintenanceFromDatabase(Maintenance maintenance);
    List<Status> readStatusesFromDatabase();
    void createStatusInDatabase(Status status);
    void updateStatusInDatabase(Status status);
    void deleteStatusFromDatabase(Status status);
}
