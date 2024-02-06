package com.tvz.java.threads;

import com.tvz.java.entities.Furnace;
import com.tvz.java.entities.Maintenance;
import com.tvz.java.entities.Status;

public class DeleteThread<T> extends DatabaseThread implements Runnable{
    private T object;
    public DeleteThread(T object){
        this.object = object;
    }
    @Override
    public void run() {
        if (object instanceof Furnace){
            super.deleteFurnace((Furnace) object);
        }
        if (object instanceof Maintenance){
            super.deleteMaintenance((Maintenance) object);
        }
        if (object instanceof Status){
            super.deleteStatus((Status) object);
        }
    }
}