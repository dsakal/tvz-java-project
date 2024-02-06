package com.tvz.java.threads;

import com.tvz.java.controllers.FurnaceInputController;
import com.tvz.java.database.DatabaseUtils;
import com.tvz.java.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class DatabaseThread{
    private static final Logger logger = LoggerFactory.getLogger(DatabaseThread.class);
    DatabaseUtils databaseUtils = new DatabaseUtils();
    private static boolean databaseConnectionActive = false;

    public synchronized List<Furnace> readFurnaces() {
        while (databaseConnectionActive){
            try {
                wait();
            } catch (InterruptedException e) {
                logger.error("Thread interrupted while waiting for database connection.", e);
            }
        }
        databaseConnectionActive = true;
        List<Furnace> furnaces = databaseUtils.readFurnacesDatabase();
        databaseConnectionActive = false;
        notifyAll();
        return furnaces;
    }
    public synchronized void createFurnace(Furnace furnace) {
        while (databaseConnectionActive){
            try {
                wait();
            } catch (InterruptedException e) {
                logger.error("Thread interrupted while waiting for database connection.", e);
            }
        }
        databaseConnectionActive = true;
        databaseUtils.createFurnaceInDatabase(furnace);
        databaseConnectionActive = false;
        notifyAll();
    }
    public synchronized void updateFurnace(Furnace furnace) {
        while (databaseConnectionActive){
            try {
                wait();
            } catch (InterruptedException e) {
                logger.error("Thread interrupted while waiting for database connection.", e);
            }
        }
        databaseConnectionActive = true;
        databaseUtils.updateFurnaceInDatabase(furnace);
        databaseConnectionActive = false;
        notifyAll();
    }
    public synchronized void deleteFurnace(Furnace furnace) {
        while (databaseConnectionActive){
            try {
                wait();
            } catch (InterruptedException e) {
                logger.error("Thread interrupted while waiting for database connection.", e);
            }
        }
        databaseConnectionActive = true;
        databaseUtils.deleteFurnaceFromDatabase(furnace);
        databaseConnectionActive = false;
        notifyAll();
    }
    public synchronized List<Maintenance> readMaintenances() {
        while (databaseConnectionActive){
            try {
                wait();
            } catch (InterruptedException e) {
                logger.error("Thread interrupted while waiting for database connection.", e);
            }
        }
        databaseConnectionActive = true;
        List<Maintenance> maintenances = databaseUtils.readMaintenancesDatabase();
        databaseConnectionActive = false;
        notifyAll();
        return maintenances;
    }
    public synchronized void createMaintenance(Maintenance maintenance) {
        while (databaseConnectionActive){
            try {
                wait();
            } catch (InterruptedException e) {
                logger.error("Thread interrupted while waiting for database connection.", e);
            }
        }
        databaseConnectionActive = true;
        databaseUtils.createMaintenanceInDatabase(maintenance);
        databaseConnectionActive = false;
        notifyAll();
    }
    public synchronized void updateMaintenance(Maintenance maintenance) {
        while (databaseConnectionActive){
            try {
                wait();
            } catch (InterruptedException e) {
                logger.error("Thread interrupted while waiting for database connection.", e);
            }
        }
        databaseConnectionActive = true;
        databaseUtils.updateMaintenanceInDatabase(maintenance);
        databaseConnectionActive = false;
        notifyAll();
    }
    public synchronized void deleteMaintenance(Maintenance maintenance) {
        while (databaseConnectionActive){
            try {
                wait();
            } catch (InterruptedException e) {
                logger.error("Thread interrupted while waiting for database connection.", e);
            }
        }
        databaseConnectionActive = true;
        databaseUtils.deleteMaintenanceFromDatabase(maintenance);
        databaseConnectionActive = false;
        notifyAll();
    }
    public synchronized List<Status> readStatuses() {
        while (databaseConnectionActive){
            try {
                wait();
            } catch (InterruptedException e) {
                logger.error("Thread interrupted while waiting for database connection.", e);
            }
        }
        databaseConnectionActive = true;
        List<Status> status = databaseUtils.readStatusesFromDatabase();
        databaseConnectionActive = false;
        notifyAll();
        return status;
    }
    public synchronized void createStatus(Status status) {
        while (databaseConnectionActive){
            try {
                wait();
            } catch (InterruptedException e) {
                logger.error("Thread interrupted while waiting for database connection.", e);
            }
        }
        databaseConnectionActive = true;
        databaseUtils.createStatusInDatabase(status);
        databaseConnectionActive = false;
        notifyAll();
    }
    public synchronized void updateStatus(Status status) {
        while (databaseConnectionActive){
            try {
                wait();
            } catch (InterruptedException e) {
                logger.error("Thread interrupted while waiting for database connection.", e);
            }
        }
        databaseConnectionActive = true;
        databaseUtils.updateStatusInDatabase(status);
        databaseConnectionActive = false;
        notifyAll();
    }
    public synchronized void deleteStatus(Status status) {
        while (databaseConnectionActive){
            try {
                wait();
            } catch (InterruptedException e) {
                logger.error("Thread interrupted while waiting for database connection.", e);
            }
        }
        databaseConnectionActive = true;
        databaseUtils.deleteStatusFromDatabase(status);
        databaseConnectionActive = false;
        notifyAll();
    }
}
