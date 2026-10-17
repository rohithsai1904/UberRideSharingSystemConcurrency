package com.example;

import java.math.BigDecimal;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.example.drivers.Driver;
import com.example.enums.RideRequestStatus;
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
    private RideRequestStatus status;
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
        this.status = RideRequestStatus.REQUESTED;
        this.rejectedDrivers = ConcurrentHashMap.newKeySet();
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
        if(status!=RideRequestStatus.REQUESTED)
            return null;
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
}
