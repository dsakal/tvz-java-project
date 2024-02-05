package com.tvz.java.entities;

public enum FuelType {
    GAS("Gas"),
    OIL("Oil"),
    ELECTRICITY("Electricity");

    private final String fuelType;

    FuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getFuelType() {
        return fuelType;
    }
}
