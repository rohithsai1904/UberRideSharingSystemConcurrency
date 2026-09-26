package com.example.drivers;

import java.util.List;

import com.example.RideRequest;
import com.example.strategy.drivermatching.DriverMatchingStrategy;

public class DriverMatching {
    
    private DriverMatchingStrategy matchingStrategy;

    public DriverMatching(DriverMatchingStrategy matchingStrategy){
        this.matchingStrategy = matchingStrategy;
    }
    
    public void processRequest(RideRequest request, DriverPool driverpool){
        List<Driver> driverList = driverpool.getDriverOfType(request.getVehicleType());
        Driver driver =  matchingStrategy.findDriver(driverList, request);
        driver.offerRide(request);
    }
    
}
