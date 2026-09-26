package com.example.riders;

import com.example.RideRequest;
import com.example.drivers.DriverMatching;
import com.example.drivers.DriverPool;
import com.example.enums.RideStatus;
import com.example.object.Location;
import com.example.vehicle.VehicleType;

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

    public void cancelRequestedRide(RideRequest requestedRide){
        RideStatus status = requestedRide.getStatus();
        if(status==RideStatus.RIDE_STARTED || status==RideStatus.COMPLETED){
            throw new RuntimeException("Cannot cancel a ride that has already started or completed");
        }
        requestedRide.setStatus(RideStatus.CANCELLED);
    }
}
