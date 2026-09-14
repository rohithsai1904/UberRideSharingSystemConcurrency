package com.example.strategy.drivermatching;

import com.example.Driver;
import com.example.RideRequest;
import com.example.DriverStatus;
import com.example.Location;

import java.util.List;


public class NearestDriverStrategy implements DriverMatchingStrategy{

    @Override
    public Driver findDriver(List<Driver> drivers,RideRequest req){

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