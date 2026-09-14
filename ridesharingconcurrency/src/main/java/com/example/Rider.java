package com.example;


public class Rider {
    private int id;
    private String name;
    private Location curLoc;
    private boolean isInRide;
    private RideRequest requestedRide;
    private Ride currRide;
    
    public Rider(int id,String name,Location location){
        this.id=id;
        this.name=name;
        this.curLoc=location;
        this.requestedRide=null;
        this.isInRide=false;
        this.currRide= null;
    }

    public String getName(){
        return this.name;
    }

    public Location getLocation(){
        return this.curLoc;
    }

    public void createRideRequest(Location destination,VehicleType vehicleType){
        if(isInRide){
            throw new RuntimeException("Rider is already in a ride");
        }
        this.requestedRide = new RideRequest(this, curLoc, destination, vehicleType);
        
        DriverMatching.getInstance().processRequest(requestedRide, DriverPool.getInstance());
    }

    public void cancelRequestedRide(){
        if(this.requestedRide==null){
            throw new RuntimeException("No Ride Requested Exists");
        }
        RideStatus status = this.requestedRide.getStatus();
        if(status==RideStatus.RIDE_STARTED || status==RideStatus.COMPLETED){
            throw new RuntimeException("Cannot cancel a ride that has already started or completed");
        }
        this.requestedRide.setStatus(RideStatus.CANCELLED);
        this.requestedRide=null;
    }

    public void viewStatus(){
        if(isInRide)
            System.out.println(this.name+" 's Ride status is " +this.requestedRide.getStatus());
        else
            System.out.println(this.name+" 's Ride status is " +this.currRide.getStatus());
    }

    public void assignRide(Ride ride){
        this.isInRide=true;
        this.currRide = ride;
    }

    public void enterOtp(){
        System.out.println("Give this OTP to Driver : "+this.currRide.getOtp());
    }

    public void endRide(){
        System.out.println(this.name+" 's Ride Ended. Ride status is " +this.requestedRide.getStatus());
        System.out.println("The rides total fare is: "+this.currRide.getEstimatedFare());
        this.isInRide = false;
        
    }
}
