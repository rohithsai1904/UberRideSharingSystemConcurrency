package com.example;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

public class Ride {
    private int otp;
    private Rider rider;
    private BigDecimal estimatedFare;
    private RideStatus status;
    private Driver driver;

    public Ride(Driver driver, RideRequest req) {
        this.rider = req.getRider();
        this.driver = driver;
        this.estimatedFare = req.getEstimatedFare();
        this.status = req.getStatus();
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
}
