package com.example;


public class Rider {
    private int id;
    private String name;
    private Location curLoc;
    
    public Rider(int id,String name,Location location){
        this.id=id;
        this.name=name;
        this.curLoc=location;
    }

    public Location getLocation(){
        return this.curLoc;
    }

}
