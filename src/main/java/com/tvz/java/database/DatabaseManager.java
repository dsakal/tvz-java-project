package com.tvz.java.database;

import com.tvz.java.entities.FuelType;
import com.tvz.java.entities.Furnace;
import com.tvz.java.entities.Maintenance;
import com.tvz.java.entities.Status;
import com.tvz.java.exceptions.NotDeletableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class DatabaseManager implements DatabaseAccess{
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String DATABASE_FILE = "dat/database.properties";
    @Override
    public Connection connectToDatabase() throws SQLException, IOException {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(DATABASE_FILE));
        }catch (IOException e){
            logger.error("Failed to read database configuration file", e);
        }
        String databaseURL = properties.getProperty("URL");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        return DriverManager.getConnection(databaseURL, username, password);
    }
    @Override
    public List<Furnace> getFurnacesFromDatabase() {
        List<Furnace> furnaces = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM FURNACE");
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String seralNumber = resultSet.getString("serial_number");
                String fuelType = resultSet.getString("fuel");
                Optional<FuelType> fuel = Optional.empty();
                switch (fuelType.toLowerCase()) {
                    case "oil" -> fuel = Optional.of(FuelType.OIL);
                    case "gas" -> fuel = Optional.of(FuelType.GAS);
                    case "electricity" -> fuel = Optional.of(FuelType.ELECTRICITY);
                }
                Double powerOutput = resultSet.getDouble("power_output");
                Integer maxTemp = resultSet.getInt("max_temp");
                if (fuel.isPresent()){
                furnaces.add(new Furnace(id, name, seralNumber, fuel.get(), powerOutput, maxTemp));
                }
            }
        } catch (SQLException | IOException e) {
            logger.error("Failed to get furnaces from database", e);
        }
        return furnaces;
    }
    @Override
    public void addNewFurnaceToDatabase(Furnace furnace) {
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO FURNACE(NAME, SERIAL_NUMBER, FUEL, POWER_OUTPUT, MAX_TEMP) VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setString(1, furnace.getName());
            preparedStatement.setString(2, furnace.getSerialNumber());
            preparedStatement.setString(3, furnace.getFuel().getFuelType());
            preparedStatement.setDouble(4, furnace.getPowerOutput());
            preparedStatement.setInt(5, furnace.getMaxTemp());
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            logger.error("Failed to add new furnace to database", e);
        }
    }
    @Override
    public void editFurnaceInDatabase(Furnace furnace) {
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE FURNACE SET NAME = ?, SERIAL_NUMBER = ?, FUEL = ?, POWER_OUTPUT = ?, MAX_TEMP = ? WHERE ID=" + furnace.getId());
            preparedStatement.setString(1, furnace.getName());
            preparedStatement.setString(2, furnace.getSerialNumber());
            preparedStatement.setString(3, furnace.getFuel().getFuelType());
            preparedStatement.setDouble(4, furnace.getPowerOutput());
            preparedStatement.setInt(5, furnace.getMaxTemp());
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            logger.error("Failed to update furnace in database", e);
        }
    }
    @Override
    public void deleteFurnaceFromDatabase(Furnace furnace) throws NotDeletableException {
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM FURNACE WHERE ID = " + furnace.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            if (e instanceof SQLIntegrityConstraintViolationException){
                throw new NotDeletableException(e);
            }
            else {
                logger.error("Failed to delete furnace from database", e);
            }
        }
    }
    @Override
    public List<Maintenance> getMaintenancesFromDatabase() {
        List<Maintenance> maintenances = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM MAINTENANCE");
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long furnaceId = resultSet.getLong("furnace_id");
                Optional<Furnace> furnace = Optional.empty();
                for (Furnace f : getFurnacesFromDatabase()){
                    if (f.getId().equals(furnaceId)){
                        furnace = Optional.of(f);
                    }
                }
                String description = resultSet.getString("description");
                String category = resultSet.getString("category");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                Integer duration = resultSet.getInt("duration");
                if (furnace.isPresent()) {
                    maintenances.add(new Maintenance(id, furnace.get(), description, category, date, duration));
                }
            }
        } catch (SQLException | IOException e) {
            logger.error("Failed to get maintenances from database", e);
        }
        return maintenances;
    }
    @Override
    public void addNewMaintenanceToDatabase(Maintenance maintenance) {
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO MAINTENANCE(FURNACE_ID, DESCRIPTION, CATEGORY, DATE, DURATION) VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setLong(1, maintenance.getFurnace().getId());
            preparedStatement.setString(2, maintenance.getDescription());
            preparedStatement.setString(3, maintenance.getCategory());
            preparedStatement.setString(4, maintenance.getDate().format(DateTimeFormatter.ISO_DATE));
            preparedStatement.setInt(5, maintenance.getDuration());
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            logger.error("Failed to add new maintenance to database", e);
        }
    }
    @Override
    public void editMaintenanceInDatabase(Maintenance maintenance) {
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE MAINTENANCE SET FURNACE_ID = ?, DESCRIPTION = ?, CATEGORY = ?, DATE = ?, DURATION = ? WHERE ID=" + maintenance.getId());
            preparedStatement.setLong(1, maintenance.getFurnace().getId());
            preparedStatement.setString(2, maintenance.getDescription());
            preparedStatement.setString(3, maintenance.getCategory());
            preparedStatement.setString(4, maintenance.getDate().format(DateTimeFormatter.ISO_DATE));
            preparedStatement.setInt(5, maintenance.getDuration());
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            logger.error("Failed to update maintenance in database", e);
        }
    }
    @Override
    public void deleteMaintenanceFromDatabase(Maintenance maintenance) {
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM MAINTENANCE WHERE ID = " + maintenance.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            logger.error("Failed to delete maintenance from database", e);
        }
    }
    @Override
    public List<Status> getStatusFromDatabase() {
        List<Status> statuses = new ArrayList<>();
        try (Connection connection = connectToDatabase()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM STATUS");
            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                Long furnaceId = resultSet.getLong("furnace_id");
                Optional<Furnace> furnace = Optional.empty();
                for (Furnace f : getFurnacesFromDatabase()){
                    if (f.getId().equals(furnaceId)){
                        furnace = Optional.of(f);
                    }
                }
                String currentStatus = resultSet.getString("current_status");
                Double efficiency = resultSet.getDouble("efficiency");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                if (furnace.isPresent()) {
                    statuses.add(new Status(id, furnace.get(), currentStatus, efficiency, date));
                }
            }
        } catch (SQLException | IOException e) {
            logger.error("Failed to get statuses from database", e);
        }
        return statuses;
    }
    @Override
    public void addNewStatusToDatabase(Status status) {
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO STATUS(FURNACE_ID, CURRENT_STATUS, EFFICIENCY, DATE) VALUES (?, ?, ?, ?)");
            preparedStatement.setLong(1, status.getFurnace().getId());
            preparedStatement.setString(2, status.getCurrentStatus());
            preparedStatement.setDouble(3, status.getEfficiency());
            preparedStatement.setString(4, status.getDate().format(DateTimeFormatter.ISO_DATE));
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            logger.error("Failed to add new status to database", e);
        }
    }
    @Override
    public void editStatusInDatabase(Status status) {
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE STATUS SET FURNACE_ID = ?, CURRENT_STATUS = ?, EFFICIENCY = ?, DATE = ? WHERE ID=" + status.getId());
            preparedStatement.setLong(1, status.getFurnace().getId());
            preparedStatement.setString(2, status.getCurrentStatus());
            preparedStatement.setDouble(3, status.getEfficiency());
            preparedStatement.setString(4, status.getDate().format(DateTimeFormatter.ISO_DATE));
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            logger.error("Failed to update status in database", e);
        }
    }
    @Override
    public void deleteStatusFromDatabase(Status status) {
        try (Connection connection = connectToDatabase()) {
            PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM STATUS WHERE ID = " + status.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException | IOException e) {
            logger.error("Failed to delete status from database", e);
        }
    }
}
