package com.tvz.java.threads;

import com.tvz.java.entities.Furnace;
import com.tvz.java.entities.Maintenance;
import com.tvz.java.entities.Status;

public class CreateThread<T> extends DatabaseThread implements Runnable {
    private T object;

    public CreateThread(T object) {
        this.object = object;
    }

    @Override
    public void run() {
        if (object instanceof Furnace){
            super.createFurnace((Furnace) object);
        }
        if (object instanceof Maintenance){
            super.createMaintenance((Maintenance) object);
        }
        if (object instanceof Status){
            super.createStatus((Status) object);
        }
    }
}

