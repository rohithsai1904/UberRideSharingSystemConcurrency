package com.example.strategy.pricing;

import java.math.BigDecimal;

import com.example.Location;
import com.example.VehicleType;

public interface PricingStrategy {
    BigDecimal calculateEstimatedFare(Location source, Location destination, VehicleType vehicleType);

    BigDecimal calculateFinalFare(int distance, int durationInMinutes, VehicleType vehicleType);
}