package com.example;

public class Driver {
    private int id;
    private String name;
    private Location curLoc;
    private DriverStatus status;
    private Vehicle vh;
    private RideRequest rr;
    private Ride currentRide;

    public Driver(int id,String name,Location loc,Vehicle vh){
        this.vh =vh;
        this.id =id;
        this.name = name;
        this.curLoc =loc;
        this.status = DriverStatus.AVAILABLE;
        this.rr = null;
        this.currentRide = null;
    }

    public DriverStatus getDriverStatus(){
        return this.status;
    }

    public Location getLocation(){
        return this.curLoc;
    }

    public Vehicle getVehicle(){
        return this.vh;
    }

    public void setStatus(DriverStatus status){
        this.status=status;
    }

    public void goOffline(){
        if(status==DriverStatus.BUSY){
            throw new RuntimeException("Cannot go to Offline when BUSY");
        }
        this.setStatus(DriverStatus.OFFLINE);
    }

    public void goOnline(){
        this.setStatus(DriverStatus.AVAILABLE);
    }

    public Ride getCurrentRide() {
        return this.currentRide;
    }

    public void startRide(){
        this.currentRide.setStatus(RideStatus.RIDE_STARTED);
    }

    public void endRide(){
        this.currentRide.setStatus(RideStatus.COMPLETED);
        this.status = DriverStatus.AVAILABLE;
        System.out.println(this.currentRide.getEstimatedFare()+" is the Amount to be collected");
        this.currentRide.getRider().endRide();
    }

    public void offerRide(RideRequest rideRequest){
        this.rr = rideRequest;
    }

    public void rejectRide(){
        System.out.println(this.name+" rejected the ride");
        this.rr.addDriver(this);
        this.rr.setStatus(RideStatus.REQUESTED);
        DriverMatching.getInstance().processRequest(this.rr, DriverPool.getInstance());
        this.rr = null;
    }

    public void acceptRide(){
        if (this.rr == null) {
            throw new RuntimeException("No ride offered");
        }
        System.out.println(this.name+" accepted the ride. Rider Name: "+ this.rr.getRider().getName());
        this.rr.setStatus(RideStatus.DRIVER_ASSIGNED);
        this.status = DriverStatus.BUSY;
        this.currentRide = new Ride(this, rr);
        this.rr.getRider().assignRide(this.currentRide);
        this.rr = null;
    }
}
