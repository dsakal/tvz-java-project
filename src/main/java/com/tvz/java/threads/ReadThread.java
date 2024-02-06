package com.tvz.java.threads;

import com.tvz.java.entities.Furnace;
import com.tvz.java.entities.Maintenance;
import com.tvz.java.entities.Status;

import java.util.List;

public class ReadThread<T> extends DatabaseThread implements Runnable {
    private List<T> objectList;
    private Class<T> objectType;

    public ReadThread(List<T> objectList, Class<T> objectType) {
        this.objectList = objectList;
        this.objectType = objectType;
    }

    @Override
    public void run() {
        if (objectType.equals(Furnace.class)) {
            objectList.clear();
            objectList.addAll((List<T>) super.readFurnaces());
        }
        if (objectType.equals(Maintenance.class)) {
            objectList.clear();
            objectList.addAll((List<T>) super.readMaintenances());
        }
        if (objectType.equals(Status.class)) {
            objectList.clear();
            objectList.addAll((List<T>) super.readStatuses());
        }
    }
}
