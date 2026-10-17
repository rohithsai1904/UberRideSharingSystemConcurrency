package com.example;

import com.example.drivers.DriverMatching;
import com.example.drivers.DriverPool;
import com.example.object.Location;
import com.example.vehicle.VehicleType;
import com.example.riders.Rider;
import com.example.drivers.Driver;

import com.example.enums.*;

public class RideService {

    private DriverPool dp;
    private DriverMatching driverMatching;
    private static RideService instance;
    private final LocationService locationService = new LocationService();

    private RideService(DriverPool dp, DriverMatching driverMatching){
        this.dp = dp;
        this.driverMatching = driverMatching;
    }

    public static synchronized RideService getInstance(DriverPool dp, DriverMatching driverMatching){
        if(instance==null){
            instance = new RideService(dp, driverMatching);
        }
        return instance;
    }

    public void shutdown(){
        locationService.shutdown();
    }

    public RideRequest requestRide(Rider rider,Location pickup,Location dropLocation,VehicleType vehicleType){
        RideRequest req = new RideRequest(rider, pickup, dropLocation, vehicleType);
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
    }

    public synchronized boolean declineOffer(Driver driver, RideRequest req) {
        if (req.getStatus() != RideRequestStatus.REQUESTED) {
            return false;
        }
        req.addRejectedDriver(driver);
        driverMatching.rematch(req, dp);
        return true;
    }
}
