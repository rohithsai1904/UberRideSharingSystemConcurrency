package com.example.strategy.drivermatching;

import com.example.RideRequest;
import com.example.drivers.Driver;

import java.util.List;

public interface DriverMatchingStrategy{

    public Driver findDriver(List<Driver> drivers,RideRequest req);
}