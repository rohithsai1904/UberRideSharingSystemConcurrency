package com.example.drivers;

import com.example.Ride;
import com.example.RideRequest;
import com.example.enums.*;
import com.example.object.Location;
import com.example.vehicle.Vehicle;
import com.example.RideService;

import java.util.Set;
import java.util.HashSet;


public class Driver {
    private int id;
    private String name;
    private volatile Location curLoc;
    private DriverStatus status;
    private Vehicle vh;
    private Ride currentRide;
    private final Set<RideRequest> offers;
    private RideService rideService;


    public Driver(int id,String name,Location loc,Vehicle vh,RideService rideService){
        this.vh =vh;
        this.id =id;
        this.name = name;
        this.curLoc =loc;
        this.status = DriverStatus.AVAILABLE;
        this.currentRide = null;
        this.offers = new HashSet<>();
        this.rideService = rideService;
    }

    public synchronized DriverStatus getDriverStatus(){
        return this.status;
    }

    public void setStatus(DriverStatus status){
        this.status = status;
    }

    public void setLocation(Location loc){
        this.curLoc = loc;
    }

    public Location getLocation(){
        return this.curLoc;
    }

    public String getName(){
        return this.name;
    }

    public Vehicle getVehicle(){
        return this.vh;
    }

    public synchronized void goOffline(){
        if(status==DriverStatus.BUSY){
            System.out.println("Driver cannot go offline in middle on ride");
            return;
        }
        this.status = DriverStatus.OFFLINE;
    }

    public synchronized void goOnline(){
        if(this.status!=DriverStatus.OFFLINE)
            return;
        this.status = DriverStatus.AVAILABLE;
    }

    public synchronized Ride getCurrentRide() {
        return this.currentRide;
    }

    public synchronized void startRide(){
        if (this.currentRide == null) {
            return;
        }
        System.out.println("Driver "+this.getName()+" Started the Ride");
        rideService.startRide(currentRide);
    }

    public synchronized void endRide() {

        if (this.currentRide == null) {
            return;
        }
        if (this.status != DriverStatus.BUSY) {
            System.out.println("END RIDE ==== Driver is not in Ride. He cannot end Ride.");
            return;
        }

        if(rideService.endRide(this.currentRide)){
            collectMoney();
            this.status = DriverStatus.AVAILABLE;
            this.currentRide = null;
        }
    }

    public void collectMoney(){
        System.out.println( this.currentRide.getFinalFare() + " is the Amount to be collected");     
    }

    public void markTimeoutForDriver(RideRequest req) {
        declineOffer(req);
    }

    public synchronized boolean receiveOffer(RideRequest rideRequest){
        if(this.status != DriverStatus.AVAILABLE){
            return false;
        }

        if (rideRequest == null) {
            return false;
        }
        this.offers.add(rideRequest);

        return true;
    }

    public void rejectRide(RideRequest req) {
        if (req == null) {
            return;
        }
        System.out.println(this.name + " rejected the ride");
        declineOffer(req);
    }

    public void declineOffer(RideRequest req) {
        if (req == null) {
            return;
        }
        synchronized (this) {
            if (!this.offers.remove(req)) {
                return;
            }
        }
        rideService.declineOffer(this, req);
    }

    public synchronized Driver acceptRide(RideRequest req){       
        if(this.status!=DriverStatus.AVAILABLE || !this.offers.contains(req)){
            System.out.println("Either Driver is unavailable or You cannot accept this ride");
            return null;
        }
        Ride ride = rideService.acceptOffer(this,req);
        if(ride==null)
            return null;
        System.out.println("Congrats you got the ride --- "+this.name);
        this.status = DriverStatus.BUSY;
        this.currentRide = ride;
        return this;
        
    }

    public synchronized void viewOfferedRequests(){
        for(RideRequest req: this.offers){
            if(req.getStatus()==RideRequestStatus.REQUESTED)
                System.out.println("Driver Name is "+this.name+". Rider is" +req.getRider().getName()+" "+req.getSourceLocation()+" "+req.getDestinationLocation()+" "+req.getEstimatedFare());
        }
    }

    public synchronized boolean isDriverAvailable(){
        if(this.status!=DriverStatus.AVAILABLE){
            return false;
        }
        return true;
    }

    public synchronized void rideCancelled(){
        collectMoney();
        this.currentRide = null;
        this.status = DriverStatus.AVAILABLE;
    }

    @Override 
    public String toString(){
        this.viewOfferedRequests();
        return "Driver Name is "+this.name+".Id is "+this.id+".With current Location"+this.curLoc;
    }
}
