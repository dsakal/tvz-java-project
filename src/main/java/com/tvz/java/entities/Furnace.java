package com.tvz.java.entities;

import java.io.Serializable;
import java.util.Objects;

public class Furnace extends Entity implements Serializable {
    private String name;
    private String serialNumber;
    private FuelType fuel;
    private Double powerOutput;
    private Integer maxTemp;

    public Furnace(Long id, String name, String serialNumber, FuelType fuel, Double powerOutput, Integer maxTemp) {
        super(id);
        this.name = name;
        this.serialNumber = serialNumber;
        this.fuel = fuel;
        this.powerOutput = powerOutput;
        this.maxTemp = maxTemp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public FuelType getFuel() {
        return fuel;
    }

    public void setFuel(FuelType fuel) {
        this.fuel = fuel;
    }

    public Double getPowerOutput() {
        return powerOutput;
    }

    public void setPowerOutput(Double powerOutput) {
        this.powerOutput = powerOutput;
    }

    public Integer getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(Integer maxTemp) {
        this.maxTemp = maxTemp;
    }

    @Override
    public String toString() {
        return name + " " + serialNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Furnace furnace = (Furnace) o;
        return Objects.equals(name, furnace.name) &&
                Objects.equals(serialNumber, furnace.serialNumber) &&
                fuel == furnace.fuel &&
                Objects.equals(powerOutput, furnace.powerOutput) &&
                Objects.equals(maxTemp, furnace.maxTemp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, serialNumber, fuel, powerOutput, maxTemp);
    }
}
