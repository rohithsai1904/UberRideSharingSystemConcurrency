package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverPool {

    private List<Driver> drivers;
    private Map<VehicleType, List<Driver>> driverVehicleMap;

    public DriverPool(){
        drivers = new ArrayList<>();
        driverVehicleMap = new HashMap<>();
    }

    public void addDriver(Driver driver){
        drivers.add(driver);
        
        VehicleType vt = driver.getVehicle().getVehicleType();
        driverVehicleMap.computeIfAbsent(vt, k -> new ArrayList<>()).add(driver);
    }

    public void removeDriver(Driver driver){
        drivers.remove(driver);

        VehicleType vt = driver.getVehicle().getVehicleType();
        driverVehicleMap.computeIfAbsent(vt, k -> new ArrayList<>()).remove(driver);
    }

    public List<Driver> getDriverOfType(VehicleType vt){
       List<Driver> driversOfType = driverVehicleMap.get(vt);
        if (driversOfType != null) {
            return driversOfType;
        }
        throw new RuntimeException("No Such VehicleType exists");
    }
    
}
