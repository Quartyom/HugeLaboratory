package com.quartyom.lab6.NetworkDevices;

public class Tablet extends NetworkDevice{
    public Tablet(long id){
        super(id);
    }
    public Tablet(){
        super.name = "tablet";
    }
    @Override
    public String getType() {
        return "Tablet";
    }
    @Override
    public String acceptWire() {
        return "I'm a tablet. I'm almost as cool as computer";
    }

    @Override
    public String acceptCall() {
        return "Yes, i'm too busy for phone calls";
    }

    @Override
    public String toString(){
        return "type: Tablet\n%s\ndrawing: %s".formatted(super.toString(), drawing);
    }

    public String drawing = "nothing";
    public void draw(String whatToDraw){
        if (whatToDraw != null){
            drawing = whatToDraw;
        }
    }
}
