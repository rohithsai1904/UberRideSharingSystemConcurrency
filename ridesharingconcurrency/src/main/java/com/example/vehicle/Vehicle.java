package com.example;

public class Vehicle {
    private VehicleType vt;
    private int licenseid;

    public Vehicle(VehicleType vt,int licenseid){
        this.vt=vt;
        this.licenseid=licenseid;
    }

    public VehicleType getVehicleType(){
        return this.vt;
    }
}
