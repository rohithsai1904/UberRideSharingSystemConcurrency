package com.example.strategy.drivermatching;

import com.example.RideRequest;
import com.example.drivers.Driver;
import com.example.enums.DriverStatus;
import com.example.object.Location;

import java.util.List;


public class NearestDriverStrategy implements DriverMatchingStrategy{

    @Override
    public synchronized Driver findDriver(List<Driver> drivers,RideRequest req){

        Driver nearestDriver = null;
        int minDistance = Integer.MAX_VALUE;
        Location srcLocation = req.getSourceLocation(); 

        for(Driver driver: drivers){
            if(!(req.getRejectedDriver().contains(driver)) && driver.getDriverStatus()==DriverStatus.AVAILABLE){

                Location driverLoc = driver.getLocation();
                
                int distance = srcLocation.getEuclideanDistance(driverLoc);

                if(distance<minDistance){
                    minDistance = distance;
                    nearestDriver = driver;
                }
            }
        }
        if(nearestDriver==null){
            throw new RuntimeException("No Drivers found. Try after Sometime");
        }
        return nearestDriver;
    }

}