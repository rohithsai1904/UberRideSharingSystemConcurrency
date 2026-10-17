package com.example.object;

import java.util.Objects;

public class Location {
    private int x;
    private int y;
    
    public Location(int X,int Y){
        this.x=X;
        this.y =Y;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }

    public int getEuclideanDistance(Location desLocation){
        int deltaX = desLocation.getX() - this.getX();
        int deltaY = desLocation.getY() - this.getY();
        return (int) Math.round(Math.sqrt(deltaX * deltaX + deltaY * deltaY));
    }

    @Override 
    public String toString(){
        return "Location (X,Y) is at ("+this.x+" , "+this.y+")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
