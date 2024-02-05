package com.tvz.java.entities;

import java.time.LocalDate;
import java.util.Objects;

public class Status extends Entity {
    private Furnace furnace;
    private String currentStatus;
    private Double efficiency;
    private LocalDate date;

    public Status(Long id, Furnace furnace, String currentStatus, Double efficiency, LocalDate date) {
        super(id);
        this.furnace = furnace;
        this.currentStatus = currentStatus;
        this.efficiency = efficiency;
        this.date = date;
    }

    public Furnace getFurnace() {
        return furnace;
    }

    public void setFurnace(Furnace furnace) {
        this.furnace = furnace;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Double getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(Double efficiency) {
        this.efficiency = efficiency;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Status status = (Status) o;
        return Objects.equals(furnace, status.furnace) && Objects.equals(currentStatus, status.currentStatus) && Objects.equals(efficiency, status.efficiency) && Objects.equals(date, status.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(furnace, currentStatus, efficiency, date);
    }
}
