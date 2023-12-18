package com.quartyom.lab6.NetworkDevices;

public class Smartphone extends NetworkDevice{
    public Smartphone(long id){
        super(id);
    }
    public Smartphone(){
        super.name = "smartphone";
    }
    @Override
    public String acceptWire() {
        return null;
    }

    @Override
    public String acceptCall() {
        return callResponse;
    }

    public String callResponse = "Im a smartphone and i can call";

    public String call(Long deviceId){
        NetworkDevice device = Medium.idToDevice(deviceId);
        if (device == null){
            return "No device";
        }
        else if (device == this){
            return "The same device";
        }
        else if (Medium.canCall()) {
            String result = device.acceptCall();
            if (result == null){
                return "The device doesn't support calls";
            }
            else {
                return "Message from device: " + result;
            }
        }
        else {
            return "Can't establish connection";
        }
    }

    @Override
    public String toString(){
        return "type: Smartphone\n" + super.toString();
    }
    @Override
    public String nameWithId() {
        return "%s (%d)".formatted(name, id);
    }

    @Override
    public String getType() {
        return "Smartphone";
    }

}
