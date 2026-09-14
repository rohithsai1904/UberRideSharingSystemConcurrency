package com.example.strategy.drivermatching;

import com.example.Driver;
import com.example.RideRequest;
import java.util.List;

public interface DriverMatchingStrategy{

    public Driver findDriver(List<Driver> drivers,RideRequest req);
}