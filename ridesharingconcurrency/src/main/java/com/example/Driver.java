package com.example;

public class Driver {
    private int id;
    private String name;
    private Location curLoc;
    private DriverStatus status;
    private Vehicle vh;

    public Driver(int id,String name,Location loc,Vehicle vh){
        this.vh =vh;
        this.id =id;
        this.name = name;
        this.curLoc =loc;
        this.status = DriverStatus.AVAILABLE;
    }

    public DriverStatus getDriverStatus(){
        return this.status;
    }

    public Location getLocation(){
        return this.curLoc;
    }
}
