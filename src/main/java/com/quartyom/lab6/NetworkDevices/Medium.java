package com.quartyom.lab6.NetworkDevices;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class Medium {
    private static final Logger log = LoggerFactory.getLogger("devices");
    private static long id = 0;
    public static long provideId(){
        return id++;
    }

    private static Random random = new Random();

    public static final float maxWiFiDistance = 30;
    public static boolean canEstablishWiFi(NetworkDevice device1, NetworkDevice device2){
        float errorProbability = 0.95f;
        return device1 != null && device2 != null &&
                (Math.abs(device1.position - device2.position) <= maxWiFiDistance) &&
                random.nextFloat() < errorProbability;
    }

    public static final float maxBluetoothDistance = 20;
    public static boolean canEstablishBluetooth(NetworkDevice device1, NetworkDevice device2){
        float errorProbability = 0.75f;
        return device1 != null && device2 != null &&
                (Math.abs(device1.position - device2.position) <= maxBluetoothDistance) &&
                random.nextFloat() < errorProbability;
    }

    public static final float maxWireDistance = 1000;
    public static boolean canWire(NetworkDevice device1, NetworkDevice device2){
        return device1 != null && device2 != null &&
                Math.abs(device1.position - device2.position) <= maxWireDistance;
    }

    public static boolean canCall(){
        float probability = 0.8f;
        return random.nextFloat() < probability;
    }

    private record BluetoothRequest(NetworkDevice dFrom, NetworkDevice dTo){}
    private static final HashSet<BluetoothRequest> bluetoothRequests = new HashSet<>();
    public static String requestBluetooth(NetworkDevice from, NetworkDevice to){
        BluetoothRequest fromTo = new BluetoothRequest(from, to);
        BluetoothRequest toFrom = new BluetoothRequest(to, from);
        if (bluetoothRequests.contains(fromTo)){
            log.info("The device " + from.nameWithId() + " has sent double request via bluetooth");
            return "Request is sent already";
        }
        else if (bluetoothRequests.contains(toFrom)){
            if (canEstablishBluetooth(from, to)) {
                bluetoothRequests.remove(toFrom);
                from.acceptBluetoothConnection(to.id);
                to.acceptBluetoothConnection(from.id);
                log.info("The device " + from.nameWithId() + " has paired with " + to.nameWithId() + " via bluetooth");
                return "Connection established";
            }
            else {
                log.warn("The device " + from.nameWithId() + " couldn't establish bluetooth connection");
                return "Something went wrong. Try again";
            }
        }
        else {
            bluetoothRequests.add(fromTo);
            log.info("The device " + from.nameWithId() + " has sent a bluetooth request");
            return "Request sent. Waiting for response";
        }
    }

    private static HashMap<Long, NetworkDevice> devices = new HashMap<>();

    public static NetworkDevice idToDevice(Long id){
        return devices.getOrDefault(id,null);
    }

    public static void addDevice(NetworkDevice device){
        devices.put(device.id, device);
        if (device.id >= id) {
            id = device.id + 1; // чтобы id всегда были уникальными, даже при загрузке
        }
        log.info("The device " + device.nameWithId() + " was added to medium");
    }
    public static void removeDevice(NetworkDevice device){
        log.info("The device " + device.nameWithId() + " was removed from medium");
        devices.remove(device.id);
    }

    public static void clear(){
        id = 0;
        bluetoothRequests.clear();
        devices.clear();
    }

}


