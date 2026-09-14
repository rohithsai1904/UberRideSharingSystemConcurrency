package com.example;

import java.util.List;

import com.example.strategy.drivermatching.DriverMatchingStrategy;

public class DriverMatching {
    private static DriverMatching instance;
    private DriverMatchingStrategy matchingStrategy;

    private DriverMatching(DriverMatchingStrategy matchingStrategy){
        this.matchingStrategy = matchingStrategy;
    }

    public static DriverMatching getInstance(){
        return instance;
    }
    
    
    public static DriverMatching getInstance(DriverMatchingStrategy matchingStrategy){
        if(instance==null){
            instance=new DriverMatching(matchingStrategy);
        }
        return instance;
    }
    
    public void processRequest(RideRequest request, DriverPool driverpool){
        List<Driver> driverList = driverpool.getDriverOfType(request.getVehicleType());
        Driver driver =  matchingStrategy.findDriver(driverList, request);
        driver.offerRide(request);
    }
    
}
