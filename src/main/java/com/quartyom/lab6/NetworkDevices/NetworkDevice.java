package com.quartyom.lab6.NetworkDevices;

import java.io.Serializable;
import java.util.*;

public abstract class NetworkDevice implements Serializable {
    public NetworkDevice(long id){
        this.id = id;
    }
    public NetworkDevice(){
        id = Medium.provideId();
    }
    public float position;
    public final long id;
    public String name;
    public String nameWithId() {
        return "%s (%d)".formatted(name, id);
    }

    public abstract String getType();
    public Long connectedWiFiToId;
    public final HashSet<Long> acceptedWiFiConnectionsId = new HashSet<>();
    public final HashSet<Long> acceptedBluetoothConnectionsId = new HashSet<>();

    // нельзя подключиться к самому себе и нельзя подключиться к далёкому устройству
    public String connectToWiFi(Long deviceId){
        NetworkDevice device = Medium.idToDevice(deviceId);
        if (device == null){
            return "No device";
        }
        else if (device == this){
            return "The same device";
        }
        else if (device == Medium.idToDevice(connectedWiFiToId)){
            return "Connected already";
        }
        else if (Medium.idToDevice(device.connectedWiFiToId) == this){
            return "Pair connection is unavailable";
        }
        else if (Medium.canEstablishWiFi(this, device)){
            connectedWiFiToId = device.id;
            device.acceptedWiFiConnectionsId.add(id);
            return "Connected";
        }
        else {
            return "Can't establish connection";
        }
    }

    public String disconnectFromWiFi(){
        if (connectedWiFiToId != null){
            connectedWiFiToId = null;
            return "Disconnected";
        }
        else {
            return "No connection already";
        }

    }

    public String connectedWiFiToString(){
        NetworkDevice connectedWiFiTo = Medium.idToDevice(connectedWiFiToId);
        if (Medium.canEstablishWiFi(this, connectedWiFiTo)){
            return connectedWiFiTo.nameWithId();
        }
        return "no devices";
    }

    public String wiFiConnectionsToHereToString(){
        boolean any = false;
        StringBuilder output = new StringBuilder();
        for (Long deviceId : acceptedWiFiConnectionsId){
            NetworkDevice device = Medium.idToDevice(deviceId);
            if (Medium.canEstablishWiFi(this, device)){
                output.append(device.nameWithId()).append(", ");
                any = true;
            }
        }
        if (any) {
            return output.toString();
        }
        else {
            return "no devices";
        }
    }

    public String requestBluetoothConnection(Long deviceId){
        NetworkDevice device = Medium.idToDevice(deviceId);
        if (device == null){
            return "No device";
        }
        else if (device == this){
            return "The same device";
        }
        else {
            return Medium.requestBluetooth(this, device);
        }
    }

    public void acceptBluetoothConnection(Long deviceId){
        acceptedBluetoothConnectionsId.add(deviceId);
    }

    public String disconnectBluetooth(Long deviceId){
        if (deviceId == null) {
            return "No device";
        }
        else if (!acceptedBluetoothConnectionsId.contains(deviceId)){
            return "Isn't connected already";
        }
        else if (deviceId == id){
            return "The same device";
        }
        else {
            acceptedBluetoothConnectionsId.remove(deviceId);
            return "Unpaired";
        }
    }

    public String bluetoothConnectionsToString(){
        boolean any = false;
        StringBuilder output = new StringBuilder();
        for (Long deviceId : acceptedBluetoothConnectionsId){
            NetworkDevice device = Medium.idToDevice(deviceId);
            if (Medium.canEstablishBluetooth(this, device)){
                output.append(device.nameWithId()).append(", ");
                any = true;
            }
        }
        if (any) {
            return output.toString();
        }
        else {
            return "no devices";
        }
    }

    public String connectionsToString(){
        return "connected to (WiFi): %s\nconnections to here (WiFi): %s\nbluetooth pairs: %s"
                .formatted(connectedWiFiToString(), wiFiConnectionsToHereToString(), bluetoothConnectionsToString());
    }

    @Override
    public String toString(){
        return "name: %s\nid: %d\nposition: %f\n%s"
                .formatted(nameWithId(), id, position, connectionsToString());
    }

    public abstract String acceptWire();
    public abstract String acceptCall();


    public float getPosition() {
        return position;
    }
    public long getId() {
        return id;
    }
    public String getName(){
        return name;
    }

}
