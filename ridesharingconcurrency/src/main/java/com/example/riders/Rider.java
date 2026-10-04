package com.example.riders;

import com.example.Ride;
import com.example.RideRequest;
import com.example.object.Location;
import com.example.vehicle.VehicleType;

public class Rider {
    private int id;
    private String name;
    private Location curLoc;
    private final RiderService riderService;

    private int isInRide;
    private RideRequest requestedRide;
    private Ride currRide;
    
    public Rider(int id,String name,Location location,RiderService riderService){
        this.id=id;
        this.name=name;
        this.curLoc=location;
        this.requestedRide=null;
        this.isInRide=0;
        this.currRide= null;
        this.riderService=riderService;
    }

    public String getName(){
        return this.name;
    }

    public Location getLocation(){
        return this.curLoc;
    }

    public RideRequest createRideRequest(Location destination,VehicleType vehicleType){
        if(isInRide!=0){
            throw new RuntimeException("Rider is already in a ride");
        }
        this.requestedRide = riderService.createRideRequest(this, this.curLoc, destination, vehicleType);
        return this.requestedRide;
    }

    public void cancelRequestedRide(){
        if(this.requestedRide==null){
            throw new RuntimeException("No Ride Requested Exists");
        }
        riderService.cancelRequestedRide(requestedRide);
        this.requestedRide=null;
    }

    public void viewStatus(){
        if(isInRide==1 && this.requestedRide!=null)
            System.out.println(this.name+" 's Ride status is " +this.requestedRide.getStatus());
        else if(isInRide==2 && this.currRide!=null)
            System.out.println(this.name+" 's Ride status is " +this.currRide.getStatus());
        else
            System.out.println("Rider "+ this.name +" Idle");
    }

    public void assignRide(Ride ride){
        this.isInRide=2;
        this.currRide = ride;
    }

    public void enterOtp(){
        System.out.println("Give this OTP to Driver : "+this.currRide.getOtp());
    }

    public void endRide(){
        System.out.println(this.name+" 's Ride Ended. Ride status is " +this.requestedRide.getStatus());
        System.out.println("The rides total fare is: "+this.currRide.getEstimatedFare());
        this.isInRide = 0;        
    }
}
