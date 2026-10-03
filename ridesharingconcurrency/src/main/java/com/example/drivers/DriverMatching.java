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
        List<Driver> driverList = driverpool.getDriversOfType(request.getVehicleType());
        
        if(matchingStrategy!=null){
            Driver driver =  matchingStrategy.findDriver(driverList, request);
            if(driver.offerRide(request)==false){
                processRequest(request, driverpool);
            };
        }
        
        else{
            for(Driver d: driverList){
                if(d.isDriverAvailable())
                    d.offerRide(request);
            }
        }
    }
}
