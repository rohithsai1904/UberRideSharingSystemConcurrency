package com.example;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

import com.example.drivers.Driver;
import com.example.enums.RideStatus;
import com.example.enums.RiderState;
import com.example.riders.Rider;
import com.example.object.Location;

public class Ride {
    private int otp;
    private Rider rider;
    private BigDecimal finalFare;
    private BigDecimal estimatedFare;
    private volatile RideStatus status;
    private Driver driver;
    private Location destnLocation;
    private Location pickUpLocation;
    private final Location initialLocation;

    private long startTime;

    public Ride(Driver driver, RideRequest req) {
        this.rider = req.getRider();
        this.driver = driver;
        this.status = RideStatus.DRIVER_ASSIGNED;
        this.destnLocation = req.getDestinationLocation();
        this.pickUpLocation = req.getSourceLocation();
        this.initialLocation = driver.getLocation();
        this.startTime = System.nanoTime();
        this.estimatedFare = req.getEstimatedFare();
    }

    public Location getInitialLocation(){
        return this.initialLocation;
    }

    public long getStartTime(){
        return this.startTime;
    }

    public Rider getRider() {
        return this.rider;
    }

    public Driver getDriver() {
        return this.driver;
    }

    public BigDecimal getFinalFare() {
        return this.finalFare;
    }

    public void setFinalFare(BigDecimal finalFare) {
        this.finalFare = finalFare;
    }

    public RideStatus getStatus() {
        return this.status;
    }

    public Location getPickUpLocation(){
        return this.pickUpLocation;
    }

    public Location getDestinationLocation(){
        return this.destnLocation;
    }

    public void generateOtp() {
        this.otp = ThreadLocalRandom.current().nextInt(1000, 10000);
    }

    public boolean canEndRide(){
        if(driver.getLocation().getEuclideanDistance(this.destnLocation)==0){
            return true;
        }
        return false;
    }

    public synchronized void startRide(){
        if(this.status!=RideStatus.DRIVER_ASSIGNED)
            return;
        this.status = RideStatus.RIDE_STARTED;
        this.getRider().setStatus(RiderState.IN_RIDE);
    }

    public synchronized boolean endRide(){
        if(this.status!=RideStatus.RIDE_STARTED){
            System.out.println("Ride Didnot Started Yet");
            return false;
        }
        if(this.canEndRide()==false){
            System.out.println("END RIDE ==== Cannot stop as we didnt reach destination yet");
            return false;
        }
        this.status = RideStatus.COMPLETED;
        this.rider.endRide();
        return true;
    }

    public synchronized boolean cancelRide(){
        if(this.status!=RideStatus.DRIVER_ASSIGNED){
            System.out.println("Ride already Started");
            return false;
        }
        System.out.println("Rider wants to cancel the ride");
        this.finalFare = new BigDecimal("0.15").multiply(this.estimatedFare);
        this.status = RideStatus.CANCELLED;
        return true;
    }

    @Override 
    public String toString(){
        return "Driver Status is "+this.driver.toString()+"Ride Status is "+this.getStatus()+"Rider Details is "+this.rider.toString();
    }
}
