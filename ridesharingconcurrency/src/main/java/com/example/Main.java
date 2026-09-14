package com.example;

import com.example.strategy.drivermatching.NearestDriverStrategy;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        DriverPool dp = DriverPool.getInstance();
        DriverMatching matchingEngine = DriverMatching.getInstance(new NearestDriverStrategy());

        Vehicle vh1 = new Vehicle(VehicleType.BIKE, 677676);
        Driver d1 = new Driver(123, "Arun", new Location(10, 200), vh1);

        Vehicle vh2 = new Vehicle(VehicleType.SEDAN, 112233);
        Driver d2 = new Driver(124, "Priya", new Location(15, 180), vh2);

        Vehicle vh3 = new Vehicle(VehicleType.AUTO, 445566);
        Driver d3 = new Driver(125, "Ravi", new Location(8, 210), vh3);

        Vehicle vh4 = new Vehicle(VehicleType.SUV, 778899);
        Driver d4 = new Driver(126, "Meena", new Location(25, 150), vh4);

        Vehicle vh5 = new Vehicle(VehicleType.BIKE, 990011);
        Driver d5 = new Driver(127, "Karthik", new Location(112, 195), vh5);

        Vehicle vh6 = new Vehicle(VehicleType.SEDAN, 334455);
        Driver d6 = new Driver(128, "Sneha", new Location(30, 120), vh6);

        dp.addDriver(d1);
        dp.addDriver(d2);
        dp.addDriver(d3);
        dp.addDriver(d4);
        dp.addDriver(d5);
        dp.addDriver(d6);

        Rider r1 = new Rider(22,"NTR",new Location(78,66));
        Rider r2 = new Rider(25,"AA",new Location(22,25));

        r1.createRideRequest(new Location(99, 366),VehicleType.BIKE);
        
        d5.rejectRide();
        d1.acceptRide();

        d1.startRide();
        
        r1.viewStatus();

        d1.endRide();



    }
}