package com.example.riders;

import com.example.Ride;
import com.example.RideRequest;
import com.example.RideService;
import com.example.enums.RiderState;
import com.example.object.Location;
import com.example.vehicle.VehicleType;

public class Rider {
    private int id;
    private String name;
    private Location curLoc;
    private final RideService rideService;

    private RiderState state;
    private RideRequest requestedRide;
    private Ride currRide;
    
    public Rider(int id,String name,Location location,RideService rideService){
        this.id=id;
        this.name=name;
        this.curLoc=location;
        this.requestedRide=null;
        this.state=RiderState.IDLE;
        this.currRide= null;
        this.rideService=rideService;
    }

    public String getName(){
        return this.name;
    }

    public Location getLocation(){
        return this.curLoc;
    }


    public RideRequest createRideRequest(Location destination,VehicleType vehicleType){
        if(state!=RiderState.IDLE){
            throw new RuntimeException("Rider is already in a ride");
        }
        this.requestedRide = rideService.requestRide(this, this.curLoc, destination, vehicleType);
        this.state = RiderState.REQUESTED;
        return this.requestedRide;
    }

    public void setStatus(RiderState status){
        this.state = status;
    }

    public void cancelRide(){
        if(this.state==RiderState.DRIVER_ASSIGNED && this.currRide!=null){
            rideService.cancelRide(this, currRide);
            this.currRide = null;
            this.requestedRide = null;
            this.state = RiderState.IDLE;
        }
        else if(this.state==RiderState.REQUESTED && this.requestedRide!=null){

            if(this.requestedRide.cancel()){
                this.requestedRide = null;
                this.state = RiderState.IDLE;
            }
            
        }
    }

    public void viewStatus(){
        if(state==RiderState.REQUESTED)
            System.out.println(this.name+" 's Ride status is " +this.requestedRide.getStatus());
        else if(state==RiderState.IDLE)
            System.out.println("Rider Name"+ this.name +" Idle");
        else
            System.out.println(this.name+" 's Ride status is " +this.currRide.toString());
    }

    public void assignRide(Ride ride){
        this.state=RiderState.DRIVER_ASSIGNED;
        this.currRide = ride;
    }

    public void endRide(){
        System.out.println(this.name+" 's Ride Ended. Ride status is " +this.currRide.getStatus());
        System.out.println("The rides total fare is: "+this.currRide.getFinalFare());
        this.state = RiderState.IDLE;
        this.currRide = null;
        this.requestedRide = null;       
    }

    @Override 
    public String toString(){
        return "Rider Name is at ("+this.name+" , "+this.id+").He is at Location"+this.getLocation();
    }
}
