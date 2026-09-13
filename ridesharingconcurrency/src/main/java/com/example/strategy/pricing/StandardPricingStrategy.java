package com.example.strategy.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.example.Location;
import com.example.VehicleType;

public class StandardPricingStrategy implements PricingStrategy {
    private static final BigDecimal BASE_FARE = new BigDecimal("30.00");

    @Override
    public BigDecimal calculateEstimatedFare(Location source, Location destination, VehicleType vehicleType) {
        return calculateFinalFare(source.getEuclideanDistance(destination), 0, vehicleType);
    }

    @Override
    public BigDecimal calculateFinalFare(int distance, int durationInMinutes, VehicleType vehicleType) {
        BigDecimal perKilometerRate = switch (vehicleType) {
            case BIKE -> new BigDecimal("8.00");
            case AUTO -> new BigDecimal("12.00");
            case SEDAN -> new BigDecimal("18.00");
            case SUV -> new BigDecimal("25.00");
        };

        BigDecimal perMinuteRate = new BigDecimal("2.00");
        return BASE_FARE
                .add(perKilometerRate.multiply(BigDecimal.valueOf(distance)))
                .add(perMinuteRate.multiply(BigDecimal.valueOf(durationInMinutes)))
                .setScale(2, RoundingMode.HALF_UP);
    }
}