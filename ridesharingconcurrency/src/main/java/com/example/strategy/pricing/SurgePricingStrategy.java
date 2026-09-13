package com.example.strategy.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import com.example.Location;
import com.example.VehicleType;

public final class SurgePricingStrategy implements PricingStrategy {
    private static final BigDecimal MINIMUM_MULTIPLIER = BigDecimal.ONE;

    private final PricingStrategy basePricingStrategy;
    private final BigDecimal surgeMultiplier;

    public SurgePricingStrategy(PricingStrategy basePricingStrategy, BigDecimal surgeMultiplier) {
        this.basePricingStrategy = Objects.requireNonNull(basePricingStrategy, "basePricingStrategy");
        this.surgeMultiplier = validateMultiplier(surgeMultiplier);
    }

    @Override
    public BigDecimal calculateEstimatedFare(Location source, Location destination, VehicleType vehicleType) {
        BigDecimal baseFare = basePricingStrategy.calculateEstimatedFare(source, destination, vehicleType);
        return applyMultiplier(baseFare);
    }

    @Override
    public BigDecimal calculateFinalFare(int distance, int durationInMinutes, VehicleType vehicleType) {
        BigDecimal baseFare = basePricingStrategy.calculateFinalFare(distance, durationInMinutes, vehicleType);
        return applyMultiplier(baseFare);
    }

    public BigDecimal getSurgeMultiplier() {
        return surgeMultiplier;
    }

    private BigDecimal applyMultiplier(BigDecimal baseFare) {
        return baseFare.multiply(surgeMultiplier).setScale(2, RoundingMode.HALF_UP);
    }

    private static BigDecimal validateMultiplier(BigDecimal multiplier) {
        Objects.requireNonNull(multiplier, "surgeMultiplier");
        if (multiplier.compareTo(MINIMUM_MULTIPLIER) < 0) {
            throw new IllegalArgumentException("surgeMultiplier must be at least 1.0");
        }
        return multiplier.setScale(2, RoundingMode.HALF_UP);
    }
}
