package com.example.strategy.drivermatching;

import com.example.RideRequest;
import com.example.drivers.Driver;
import com.example.object.Location;

import java.util.List;
import java.util.Optional;


public class NearestDriverStrategy implements DriverMatchingStrategy{

    @Override
    public Optional<Driver> findDriver(List<Driver> drivers,RideRequest req){

        Driver nearestDriver = null;
        int minDistance = Integer.MAX_VALUE;
        Location srcLocation = req.getSourceLocation(); 

        for(Driver driver: drivers){
            if(!(req.isRejectedBy(driver)) && driver.isDriverAvailable()){

                Location driverLoc = driver.getLocation();
                
                int distance = srcLocation.getEuclideanDistance(driverLoc);

                if(distance<minDistance){
                    minDistance = distance;
                    nearestDriver = driver;
                }
            }
        }
        return Optional.ofNullable(nearestDriver);
    }

}