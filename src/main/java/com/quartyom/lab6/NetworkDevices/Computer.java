package com.quartyom.lab6.NetworkDevices;

public class Computer extends NetworkDevice{
    public Computer(long id){
        super(id);
    }
    public Computer(){
        super.name = "computer";
    }
    @Override
    public String getType() {
        return "Computer";
    }

    public String wire(Long deviceId){
        NetworkDevice device = Medium.idToDevice(deviceId);
        if (device == null){
            return "No device";
        }
        else if (device == this){
            return "The same device";
        }
        else if (Medium.canWire(this, device)) {
            String result = device.acceptWire();
            if (result == null){
                return "The device doesn't support wired connections";
            }
            else {
                return "Message from device: " + result;
            }
        }
        else {
            return "Can't establish connection";
        }
    }

    public String wireResponse = "I'm a computer and my id is " + id + ". That's it.";

    @Override
    public String acceptWire() {
        return wireResponse;
    }

    @Override
    public String acceptCall() {
        return null;
    }

    @Override
    public String toString(){
        return "type: Computer\n" + super.toString();
    }
}
