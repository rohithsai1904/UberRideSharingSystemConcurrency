package com.example;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

import com.example.drivers.Driver;
import com.example.enums.RideStatus;
import com.example.riders.Rider;
import com.example.object.Location;;

public class Ride {
    private int otp;
    private Rider rider;
    private BigDecimal estimatedFare;
    private RideStatus status;
    private Driver driver;
    private Location destnLocation;

    public Ride(Driver driver, RideRequest req) {
        this.rider = req.getRider();
        this.driver = driver;
        this.estimatedFare = req.getEstimatedFare();
        this.status = req.getStatus();
        this.destnLocation = req.getDestinationLocation();
    }

    public Rider getRider() {
        return this.rider;
    }

    public Driver getDriver() {
        return this.driver;
    }

    public BigDecimal getEstimatedFare() {
        return this.estimatedFare;
    }

    public RideStatus getStatus() {
        return this.status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public int getOtp() {
        return this.otp;
    }

    public void generateOtp() {
        this.otp = ThreadLocalRandom.current().nextInt(1000, 10000);
    }

    public void clearDriver() {
        this.driver = null;
    }

    public boolean canEndRide(){
        if(driver.getLocation().getEuclideanDistance(this.destnLocation)==0){
            return true;
        }
        return false;
    }
}
