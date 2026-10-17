package com.example.strategy.drivermatching;

import com.example.RideRequest;
import com.example.drivers.Driver;

import java.util.List;
import java.util.Optional;

public interface DriverMatchingStrategy{

    public Optional<Driver> findDriver(List<Driver> drivers,RideRequest req);
}