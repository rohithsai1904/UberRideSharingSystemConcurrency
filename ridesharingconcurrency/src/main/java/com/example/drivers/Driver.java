package com.example.drivers;

import com.example.Ride;
import com.example.RideRequest;
import com.example.enums.DriverStatus;
import com.example.enums.RideStatus;
import com.example.object.Location;
import com.example.riders.Rider;
import com.example.vehicle.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Driver {
    private int id;
    private String name;
    private Location curLoc;
    private DriverStatus status;
    private Vehicle vh;
    private RideRequest rr;
    private Ride currentRide;
    private final List<RideRequest> offers;
    private final ScheduledExecutorService scheduler;


    public Driver(int id,String name,Location loc,Vehicle vh,ScheduledExecutorService scheduler){
        this.vh =vh;
        this.id =id;
        this.name = name;
        this.curLoc =loc;
        this.status = DriverStatus.AVAILABLE;
        this.rr = null;
        this.currentRide = null;
        this.offers = new ArrayList<>();
        this.scheduler = scheduler;
    }

    public synchronized DriverStatus getDriverStatus(){
        return this.status;
    }

    public Location getLocation(){
        return this.curLoc;
    }

    public Vehicle getVehicle(){
        return this.vh;
    }

    public synchronized void setStatus(DriverStatus status){
        this.status=status;
    }

    public synchronized void goOffline(){
        if(status==DriverStatus.BUSY){
            System.out.println("Driver cannot go offline in middle on ride");
            return;
        }
        this.status = DriverStatus.OFFLINE;
    }

    public synchronized void goOnline(){
        this.status = DriverStatus.AVAILABLE;
    }

    public synchronized Ride getCurrentRide() {
        return this.currentRide;
    }

    public synchronized void startRide(){

        if (this.currentRide == null) {
            return;
        }
    
        if (this.currentRide.getStatus() != RideStatus.DRIVER_ASSIGNED) {
            return;
        }
    
        Rider r = this.currentRide.getRider();
        r.assignRide(currentRide);
        this.currentRide.setStatus(RideStatus.RIDE_STARTED);
    }

    public synchronized void endRide() {

        if (this.currentRide == null) {
            return;
        }
    
        if (this.status != DriverStatus.BUSY) {
            return;
        }
    
        if (!this.currentRide.canEndRide()) {
            return;
        }
    
        this.currentRide.setStatus(RideStatus.COMPLETED);
        this.status = DriverStatus.AVAILABLE;
    
        System.out.println(
            this.currentRide.getEstimatedFare()
            + " is the Amount to be collected"
        );
    
        this.currentRide.getRider().endRide();
    }

    public synchronized void markTimeoutForDriver(RideRequest req) {
        if (!req.timeoutDriver(this)) {
            return;
        }

        this.offers.remove(req);
    }

    public synchronized boolean offerRide(RideRequest rideRequest){
        if(this.status != DriverStatus.AVAILABLE){
            return false;
        }

        if (rideRequest == null) {
            return false;
        }
        this.offers.add(rideRequest);

        scheduler.schedule(()->{
            markTimeoutForDriver(rideRequest);
        }, 10, TimeUnit.SECONDS);

        return true;
    }

    public  void rejectRide(DriverMatching driverMatching,DriverPool dp,RideRequest req){
        if (req == null) {
            return;
        }
    
        synchronized (this) {
            System.out.println(this.name + " rejected the ride");
            this.offers.remove(req);
        }
    
        req.addRejectedDriver(this);
    
        driverMatching.processRequest(req, dp);
    }

    public synchronized void acceptRide(RideRequest req){
        if(this.status!=DriverStatus.AVAILABLE){
            return;
        }
        if(req.getStatus()==RideStatus.CANCELLED){
            return;
        }
        if (req.getRejectedDriver().contains(this)) {
            return;
        }
        if(req.reserveRide()){
            System.out.println(this.name+" accepted the ride. Rider Name: "+ req.getRider().getName());
            this.status = DriverStatus.BUSY;
            this.rr = req;
            this.currentRide = new Ride(this,req);
        }
        else{
            System.out.println("Somebody accepeted the ride");
        }
    }

    public synchronized void viewOfferedRequests(){
        for(RideRequest req: this.offers){
            if(req.getStatus()==RideStatus.REQUESTED)
                System.out.println(req.getRider()+" "+req.getSourceLocation()+" "+req.getDestinationLocation()+" "+req.getEstimatedFare());
        }
    }

    public synchronized boolean isDriverAvailable(){
        if(this.status!=DriverStatus.AVAILABLE){
            return false;
        }
        return true;
    }
}
