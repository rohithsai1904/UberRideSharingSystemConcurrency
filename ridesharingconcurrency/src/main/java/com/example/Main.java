package com.example;

import com.example.drivers.Driver;
import com.example.drivers.DriverMatching;
import com.example.drivers.DriverPool;
import com.example.object.Location;
import com.example.riders.Rider;

import com.example.strategy.pricing.StandardPricingStrategy;
import com.example.strategy.pricing.SurgePricingStrategy;
import com.example.vehicle.Vehicle;
import com.example.vehicle.VehicleType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.math.BigDecimal;

import java.util.concurrent.Future;

public class Main {
    public static void main(String[] args) throws Exception{
        System.out.println("Hello world!");

        DriverPool dp = new DriverPool();
        DriverMatching driverMatching = new DriverMatching();

        RideService rs =  RideService.getInstance(dp, driverMatching,new SurgePricingStrategy(
            new StandardPricingStrategy(),
            new BigDecimal("1.70")));

        ExecutorService pool = Executors.newScheduledThreadPool(5);

        Vehicle vh1 = new Vehicle(VehicleType.BIKE, 677676);
        Driver d1 = new Driver(123, "Arun", new Location(10, 200), vh1,rs);

        Vehicle vh2 = new Vehicle(VehicleType.SEDAN, 112233);
        Driver d2 = new Driver(124, "Priya", new Location(15, 180), vh2,rs);

        Vehicle vh3 = new Vehicle(VehicleType.AUTO, 445566);
        Driver d3 = new Driver(125, "Ravi", new Location(8, 210), vh3,rs);

        Vehicle vh4 = new Vehicle(VehicleType.SUV, 778899);
        Driver d4 = new Driver(126, "Meena", new Location(25, 150), vh4,rs);

        Vehicle vh5 = new Vehicle(VehicleType.BIKE, 990011);
        Driver d5 = new Driver(127, "Karthik", new Location(112, 195), vh5,rs);

        Vehicle vh6 = new Vehicle(VehicleType.BIKE, 334455);
        Driver d6 = new Driver(128, "Sneha", new Location(30, 120), vh6,rs);

        dp.addDriver(d1);
        dp.addDriver(d2);
        dp.addDriver(d3);
        dp.addDriver(d4);
        dp.addDriver(d5);
        dp.addDriver(d6);

        Rider r1 = new Rider(22,"NTR",new Location(78,66),rs);
        Rider r2 = new Rider(25,"AA",new Location(22,25),rs);

        RideRequest req1 = r1.createRideRequest(new Location(99, 366),VehicleType.BIKE);
        RideRequest req2 = r2.createRideRequest(new Location(919, 166),VehicleType.BIKE);
        //RideRequest req3 = r2.createRideRequest(new Location(919, 166),VehicleType.SEDAN);

        System.out.println(d1.toString());
        System.out.println(d2.toString());
        System.out.println(d5.toString());
        System.out.println(d6.toString());

        /* 
        System.out.println("===========================================================");
        System.out.println(req1.toString());

        d2.acceptRide(req1);
        System.out.println(req1.toString());

        d6.acceptRide(req1);
        System.out.println(req1.toString());

        d1.acceptRide(req1);

        d1.acceptRide(req2);
        d6.acceptRide(req2);
        System.out.println(req2.toString());
        System.out.println("===========================================================");
        //rs.shutdown();
        */
        System.out.println("===========================================================");
        CountDownLatch ready = new CountDownLatch(4);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(4);

        List<Driver> drivers = List.of(d1, d2, d6, d5);
        List<Future<Driver>> futures = new ArrayList<>();

        for (Driver d : drivers) {
            futures.add(pool.submit(() -> {
                try {
                    ready.countDown();
                    start.await();

                    return d.acceptRide(req1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                } finally {
                    done.countDown();
                }
            }));
        }

        

        // Wait until all drivers are ready
        ready.await();

        // Release all drivers together
        start.countDown();
        Driver assignedDriver = null;
        for (Future<Driver> future : futures) {
            Driver result = future.get();
        
            if (result != null){
                assignedDriver = result;
                System.out.println("Winner: " + assignedDriver.getName());
            }
        }
        // Wait until all attempts finish
        //done.await();
        final Driver assignedDriver1 = assignedDriver;
        Thread thread = new Thread(() -> {
            try {
                
                assignedDriver1.endRide();
                assignedDriver1.endRide();
                Thread.sleep(1000);
                r1.cancelRide();
        
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            }
        });
        
        thread.start();

        thread.join();
        pool.shutdown();
        rs.shutdown();
    }
}