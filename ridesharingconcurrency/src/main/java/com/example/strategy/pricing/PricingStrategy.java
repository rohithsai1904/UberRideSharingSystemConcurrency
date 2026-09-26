package com.example.strategy.pricing;

import java.math.BigDecimal;

import com.example.object.Location;
import com.example.vehicle.VehicleType;

public interface PricingStrategy {
    BigDecimal calculateEstimatedFare(Location source, Location destination, VehicleType vehicleType);

    BigDecimal calculateFinalFare(int distance, int durationInMinutes, VehicleType vehicleType);
}