package com.tvz.java.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class Maintenance extends Entity implements Serializable {
    private Furnace furnace;
    private String description;
    private String Category;
    private LocalDate date;
    private Integer duration;

    public Maintenance(Long id, Furnace furnace, String description, String category, LocalDate date, Integer duration) {
        super(id);
        this.furnace = furnace;
        this.description = description;
        Category = category;
        this.date = date;
        this.duration = duration;
    }

    public Furnace getFurnace() {
        return furnace;
    }

    public void setFurnace(Furnace furnace) {
        this.furnace = furnace;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Maintenance that = (Maintenance) o;
        return Objects.equals(furnace, that.furnace) && Objects.equals(description, that.description) && Objects.equals(Category, that.Category) && Objects.equals(date, that.date) && Objects.equals(duration, that.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(furnace, description, Category, date, duration);
    }
}
