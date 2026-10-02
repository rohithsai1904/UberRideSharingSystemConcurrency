package com.example.drivers;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.RideRequest;
import com.example.enums.DriverStatus;
import com.example.strategy.drivermatching.DriverMatchingStrategy;

public class DriverMatching {
    
    private DriverMatchingStrategy matchingStrategy;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public DriverMatching(DriverMatchingStrategy matchingStrategy){
        this.matchingStrategy = matchingStrategy;
    }
    
    public void processRequest(RideRequest request, DriverPool driverpool){
        List<Driver> driverList = driverpool.getDriversOfType(request.getVehicleType());
        
        if(matchingStrategy!=null){
            Driver driver =  matchingStrategy.findDriver(driverList, request);
            driver.offerRide(request);
        }
        
        else{
            for(Driver d: driverList){
                executor.submit(() ->{
                    if(d.getDriverStatus()==DriverStatus.AVAILABLE)
                        d.offerRide(request);
                });
            }
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
    
}
