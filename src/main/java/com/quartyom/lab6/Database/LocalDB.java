package com.quartyom.lab6.Database;

import com.quartyom.lab6.NetworkDevices.*;

import java.io.Serializable;
import java.util.ArrayList;

public class LocalDB implements Serializable {
    private ArrayList<NetworkDevice> devices;

    public LocalDB(){
        devices = new ArrayList<>();
    }

    public void addDevice(NetworkDevice device){
        devices.add(device);
        Medium.addDevice(device);
    }

    public void removeDevice(NetworkDevice device){
        devices.remove(device);
        Medium.removeDevice(device);
    }

    public void setDevices(ArrayList<NetworkDevice> devices){
        this.devices.clear();
        Medium.clear();
        for (NetworkDevice item : devices){
            addDevice(item);
        }
    }
    public ArrayList<NetworkDevice> getDevices(){
        return new ArrayList<>(devices);    // для целостности данных
    }

}
