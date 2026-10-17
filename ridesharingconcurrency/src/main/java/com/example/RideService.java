package com.example;

import com.example.drivers.DriverMatching;
import com.example.drivers.DriverPool;
import com.example.object.Location;
import com.example.vehicle.VehicleType;
import com.example.riders.Rider;
import com.example.strategy.pricing.PricingStrategy;

import java.math.BigDecimal;

import com.example.drivers.Driver;

import com.example.enums.*;

public class RideService {

    private DriverPool dp;
    private DriverMatching driverMatching;
    private static RideService instance;
    private final LocationService locationService = new LocationService();
    private final PricingStrategy pricingStrategy;

    private RideService(DriverPool dp, DriverMatching driverMatching,PricingStrategy pricingStrategy){
        this.dp = dp;
        this.driverMatching = driverMatching;
        this.pricingStrategy = pricingStrategy;
    }

    public static synchronized RideService getInstance(DriverPool dp, DriverMatching driverMatching,PricingStrategy pricingStrategy){
        if(instance==null){
            instance = new RideService(dp, driverMatching,pricingStrategy);
        }
        return instance;
    }

    public void shutdown(){
        locationService.shutdown();
    }

    public RideRequest requestRide(Rider rider,Location pickup,Location dropLocation,VehicleType vehicleType){
        RideRequest req = new RideRequest(rider, pickup, dropLocation, vehicleType);
        BigDecimal fareprice = pricingStrategy.calculateEstimatedFare(pickup, dropLocation, vehicleType);
        req.setEstimatedFare(fareprice);
        driverMatching.processRequest(req, dp);
        return req;
    }

    public Ride acceptOffer(Driver driver,RideRequest req){
        Ride ride = req.accept(driver);
        if(ride!=null){
            req.getRider().assignRide(ride);
            locationService.autoUpdateLocation(driver, ride);
        }
        return ride;
    }

    public void startRide(Ride ride){
        ride.startRide();
    }

    public void endRide(Ride ride){
        ride.endRide();
        int distanceCovered = ride.getInitialLocation().getEuclideanDistance(ride.getDestinationLocation());
        int durationInSeconds = (int)(ride.getTime()/ 1_000_000_000);
        BigDecimal fareprice = pricingStrategy.calculateFinalFare(distanceCovered, durationInSeconds/60 ,ride.getDriver().getVehicle().getVehicleType());
        ride.setEstimatedFare(fareprice);
    }

    public synchronized boolean declineOffer(Driver driver, RideRequest req) {
        if (req.getStatus() != RideRequestStatus.REQUESTED) {
            return false;
        }
        req.addRejectedDriver(driver);
        driverMatching.rematch(req, dp);
        return true;
    }

    public synchronized void cancelRide(Rider rider,Ride ride){
        ride.cancelRide();
        ride.getDriver().rideCancelled();
    }

}
