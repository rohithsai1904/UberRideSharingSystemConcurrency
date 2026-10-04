package com.example.drivers;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.RideRequest;
import com.example.enums.RideStatus;
import com.example.strategy.drivermatching.DriverMatchingStrategy;

public class DriverMatching {
    
    private DriverMatchingStrategy matchingStrategy;
    ExecutorService pool = Executors.newFixedThreadPool(10);


    public DriverMatching(DriverMatchingStrategy matchingStrategy){
        this.matchingStrategy = matchingStrategy;
    }
    
    public void processRequest(RideRequest request, DriverPool driverpool){
        List<Driver> driverList = driverpool.getDriversOfType(request.getVehicleType());
        
        if(matchingStrategy!=null){
            Driver driver =  matchingStrategy.findDriver(driverList, request);
            if(driver!=null && driver.offerRide(request)==false){
                processRequest(request, driverpool);
            };
        }
        
        else{
            for(Driver d: driverList){
                pool.submit(()->d.offerRide(request));
            }
        }
        if(request.getStatus()==RideStatus.REQUESTED){
            System.out.println("Nobody accepted the Ride Request. Create a new One");
        }
    }

    public void shutdown(){
        pool.shutdown();
    }
}
