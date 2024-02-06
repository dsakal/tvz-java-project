package com.tvz.java.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public record Status(Long id, Furnace furnace, String currentStatus, Double efficiency, LocalDate date) implements Serializable {
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

    @Override
    public String toString() {
        return furnace.toString() + ", "
                + currentStatus + ", "
                + String.format("%.2f%%", efficiency) + ", "
                + date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy."));
    }
}
