package com.example;


public class RiderService {
    
    DriverPool dp;
    DriverMatching driverMatching;

    public RiderService(DriverPool dp, DriverMatching driverMatching){
        this.dp = dp;
        this.driverMatching = driverMatching;
    }

    public RideRequest createRideRequest(Rider rider,Location curLoc,Location destination,VehicleType vehicleType){
        RideRequest req = new RideRequest(rider, curLoc, destination, vehicleType);
        driverMatching.processRequest(req, dp);
        return req;
    }
}
