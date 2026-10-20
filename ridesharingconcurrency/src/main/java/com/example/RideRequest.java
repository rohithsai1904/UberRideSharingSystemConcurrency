package com.example;

import java.math.BigDecimal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.example.drivers.Driver;
import com.example.enums.RideRequestStatus;
import com.example.object.Location;
import com.example.riders.Rider;
import com.example.vehicle.VehicleType;

public class RideRequest {

    private final Rider rider;
    private final Location source;
    private final Location destination;
    private final VehicleType vehicleType;
    private BigDecimal estimatedFare;
    private RideRequestStatus status;
    private Set<Driver> rejectedDrivers;


    public RideRequest(Rider rider, Location sourceLocation, Location destinationLocation, VehicleType vehicleType) {
        this.rider = rider;
        source = sourceLocation;
        destination = destinationLocation;
        this.vehicleType = vehicleType;
        this.status = RideRequestStatus.REQUESTED;
        this.rejectedDrivers = ConcurrentHashMap.newKeySet();
    }

    public BigDecimal getEstimatedFare() {
        return estimatedFare;
    }

    public void setEstimatedFare(BigDecimal estimatedFare){
        this.estimatedFare = estimatedFare;
    }

    public VehicleType getVehicleType(){
        return this.vehicleType;
    }

    public Location getSourceLocation(){
        return this.source;
    }

    public Location getDestinationLocation() {
        return this.destination;
    }

    public Rider getRider() {
        return this.rider;
    }

    public synchronized RideRequestStatus getStatus() {
        return this.status;
    }

    public synchronized void setStatus(RideRequestStatus status) {
        this.status = status;
    }

    public synchronized void addRejectedDriver(Driver driver){
        this.rejectedDrivers.add(driver);
    }

    public boolean isRejectedBy(Driver driver){
        return this.rejectedDrivers.contains(driver);
    }

    public synchronized void reserveRide(){
        if(this.status==RideRequestStatus.REQUESTED){
            this.setStatus(RideRequestStatus.DRIVER_ASSIGNED);
        }
        return;
    }

    public synchronized Ride accept(Driver driver){
        if(status!=RideRequestStatus.REQUESTED){
            if(status==RideRequestStatus.CANCELLED){
                System.out.println("Ride Cancelled");
                return null;
            }
            System.out.println("Sorry ,"+driver.getName()+" .Already accepted by one of the driver.");
            return null;
        }
        if(isRejectedBy(driver))
            return null;
        reserveRide();
        return new Ride(driver, this);
    }

    public synchronized boolean cancel(){
        if(status==RideRequestStatus.REQUESTED){
            status = RideRequestStatus.CANCELLED;
            return true;
        }
        return false;

    }

    @Override 
    public String toString(){
        return "Status of Ride Request is "+ this.status+" Pick up at "+this.source+" Drop at "+this.destination+" Estimated fare is "+this.getEstimatedFare();
    }

}
