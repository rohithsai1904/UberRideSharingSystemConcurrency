package com.example;

import com.example.enums.RideStatus;
import com.example.object.Location;
import com.example.drivers.Driver;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class LocationService {

    private static final int STEP = 25;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public void autoUpdateLocation(Driver driver, Ride ride) {
        
        AtomicReference<ScheduledFuture<?>> futureRef = new AtomicReference<>();
        
        ScheduledFuture<?> future = scheduler.scheduleWithFixedDelay(() -> {

            RideStatus status = ride.getStatus();
            if (status == RideStatus.COMPLETED || status == RideStatus.CANCELLED) {
                cancel(futureRef);
                return;
            }

            if (status == RideStatus.DRIVER_ASSIGNED) {
                Location pickup = ride.getPickUpLocation();
                driver.setLocation(pickup);
                driver.startRide();
                return;
                //moveTowards(driver, pickup);
            } else if (status == RideStatus.RIDE_STARTED) {
                Location destination = ride.getDestinationLocation();
                if (hasArrived(driver, destination)) {
                    driver.setLocation(destination);
                    driver.endRide();
                    cancel(futureRef);
                } else {
                    moveTowards(driver, destination);
                }
            }
        }, 1, 1, TimeUnit.SECONDS);

        futureRef.set(future);
    }

    private boolean hasArrived(Driver driver, Location target) {
        return driver.getLocation().getEuclideanDistance(target) <= STEP;
    }

    private void moveTowards(Driver driver, Location target) {
        Location current = driver.getLocation();
        int remaining = current.getEuclideanDistance(target);
        if (remaining <= STEP) {
            driver.setLocation(target);
            return;
        }

        int dx = target.getX() - current.getX();
        int dy = target.getY() - current.getY();
        int x = current.getX() + (int) Math.round(dx * (STEP / (double) remaining));
        int y = current.getY() + (int) Math.round(dy * (STEP / (double) remaining));
        if (x == current.getX() && y == current.getY()) {
            x += Integer.signum(dx);
            y += Integer.signum(dy);
        }
        driver.setLocation(new Location(x, y));
    }

    private void cancel(AtomicReference<ScheduledFuture<?>> futureRef) {
        ScheduledFuture<?> future = futureRef.get();
        if (future != null) {
            future.cancel(false);
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
