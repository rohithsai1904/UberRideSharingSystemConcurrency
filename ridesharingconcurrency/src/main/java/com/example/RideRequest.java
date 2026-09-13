package com.example;

import java.math.BigDecimal;

import com.example.strategy.pricing.PricingStrategy;
import com.example.strategy.pricing.StandardPricingStrategy;

public class RideRequest {

    private final Rider rider;
    private final Location source;
    private final Location destination;
    private final VehicleType vehicleType;
    private final BigDecimal estimatedFare;

    public RideRequest(Rider rider, Location sourceLocation, Location destinationLocation,
                       VehicleType vehicleType) {
        this(rider, sourceLocation, destinationLocation, vehicleType, new StandardPricingStrategy());
    }

    public RideRequest(Rider rider, Location sourceLocation, Location destinationLocation,
                       VehicleType vehicleType, PricingStrategy pricingStrategy) {
        this.rider = rider;
        source = sourceLocation;
        destination = destinationLocation;
        this.vehicleType = vehicleType;
        this.estimatedFare = pricingStrategy.calculateEstimatedFare(source, destination, vehicleType);
    }

    public BigDecimal getEstimatedFare() {
        return estimatedFare;
    }

}
