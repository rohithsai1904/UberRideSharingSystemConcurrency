package com.example;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import com.example.drivers.Driver;
import com.example.enums.DriverStatus;
import com.example.enums.RideStatus;
import com.example.object.Location;
import com.example.riders.Rider;
import com.example.strategy.pricing.PricingStrategy;
import com.example.strategy.pricing.StandardPricingStrategy;
import com.example.vehicle.VehicleType;

public class RideRequest {

    private final Rider rider;
    private final Location source;
    private final Location destination;
    private final VehicleType vehicleType;
    private final BigDecimal estimatedFare;
    private RideStatus status;
    private Set<Driver> rejectedDrivers;

    public RideRequest(Rider rider, Location sourceLocation, Location destinationLocation,
                       VehicleType vehicleType) {
        this(rider, sourceLocation, destinationLocation, vehicleType, new StandardPricingStrategy());
    }

    public RideRequest(Rider rider, Location sourceLocation, Location destinationLocation,
                       VehicleType vehicleType, PricingStrategy pricingStrategy) {
        this.rider = rider;
        source = sourceLocation;
        destination = destinationLocation;
        this.vehicleType = vehicleType;
        this.estimatedFare = pricingStrategy.calculateEstimatedFare(source, destination, vehicleType);
        this.status = RideStatus.REQUESTED;
        this.rejectedDrivers = new HashSet<>();
    }

    public BigDecimal getEstimatedFare() {
        return estimatedFare;
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

    public synchronized RideStatus getStatus() {
        return this.status;
    }

    public synchronized void setStatus(RideStatus status) {
        this.status = status;
    }

    public synchronized boolean setDriverForTheRide(){
        if(this.status!=RideStatus.DRIVER_ASSIGNED){
            this.setStatus(RideStatus.DRIVER_ASSIGNED);
            return true;
        }
        return false;
    }

    public void addRejectedDriver(Driver driver){
        this.rejectedDrivers.add(driver);
    }

    public Set<Driver> getRejectedDriver(){
        return this.rejectedDrivers;
    }

}
