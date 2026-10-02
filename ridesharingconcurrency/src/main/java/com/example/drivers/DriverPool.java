package com.example.drivers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.vehicle.VehicleType;

public class DriverPool {

    private final List<Driver> drivers = new ArrayList<>();
    private final Map<VehicleType, List<Driver>> driverVehicleMap =
            new HashMap<>();

    public synchronized void addDriver(Driver driver) {
        drivers.add(driver);

        VehicleType vehicleType =
                driver.getVehicle().getVehicleType();

        driverVehicleMap
                .computeIfAbsent(vehicleType, k -> new ArrayList<>())
                .add(driver);
    }

    public synchronized void removeDriver(Driver driver) {
        drivers.remove(driver);

        VehicleType vehicleType =
                driver.getVehicle().getVehicleType();

        List<Driver> driversOfType =
                driverVehicleMap.get(vehicleType);

        if (driversOfType != null) {
            driversOfType.remove(driver);

            if (driversOfType.isEmpty()) {
                driverVehicleMap.remove(vehicleType);
            }
        }
    }

    public synchronized List<Driver> getDriversOfType(
            VehicleType vehicleType) {

        List<Driver> driversOfType =
                driverVehicleMap.get(vehicleType);

        if (driversOfType == null) {
            return Collections.emptyList();
        }

        return new ArrayList<>(driversOfType);
    }
}