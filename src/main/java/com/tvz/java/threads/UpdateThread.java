package com.tvz.java.threads;

import com.tvz.java.entities.Furnace;
import com.tvz.java.entities.Maintenance;
import com.tvz.java.entities.Status;

public class UpdateThread<T> extends DatabaseThread implements Runnable{
    private T object;
    public UpdateThread(T object){
        this.object = object;
    }
    @Override
    public void run() {
        if (object instanceof Furnace){
            super.updateFurnace((Furnace) object);
        }
        if (object instanceof Maintenance){
            super.updateMaintenance((Maintenance) object);
        }
        if (object instanceof Status){
            super.updateStatus((Status) object);
        }
    }
}
