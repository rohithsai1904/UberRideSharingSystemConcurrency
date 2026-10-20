package com.example.drivers;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Optional;

import com.example.RideRequest;
import com.example.enums.RideRequestStatus;
import com.example.strategy.drivermatching.DriverMatchingStrategy;

public class DriverMatching {
    
    private DriverMatchingStrategy matchingStrategy = null;
    private final ExecutorService pool = Executors.newFixedThreadPool(10);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);


    public DriverMatching(DriverMatchingStrategy matchingStrategy){
        this.matchingStrategy = matchingStrategy;
    }

    public DriverMatching(){}
    
    public void processRequest(RideRequest request, DriverPool driverpool){
        List<Driver> driverList = driverpool.getDriversOfType(request.getVehicleType());
        
        if(matchingStrategy!=null){
            Optional<Driver> driver = matchingStrategy.findDriver(driverList, request);
            if(driver!=null)
                driver.ifPresent(d -> d.receiveOffer(request));
        }
        
        else{
            for(Driver d: driverList){
                pool.submit(()-> dispatchOffers(d, request));
            }
        }
    }

    public void dispatchOffers(Driver driver, RideRequest request){
        if (request.getStatus() != RideRequestStatus.REQUESTED) {
            return;
        }

        boolean received = driver.receiveOffer(request);

        if (!received) {
            return;
        }

        scheduler.schedule(()->{
            driver.markTimeoutForDriver(request);
        }, 10, TimeUnit.SECONDS);


    }
    public void shutdown(){
        pool.shutdown();
        scheduler.shutdown();
    }

    public void rematch(RideRequest request, DriverPool driverpool){
        if(matchingStrategy==null)
            return;
        processRequest(request, driverpool);
    }
}
