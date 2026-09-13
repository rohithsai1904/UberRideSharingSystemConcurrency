package com.example;

public class Location {
    int X;
    int Y;
    
    public Location(int X,int Y){
        this.X=X;
        this.Y =Y;
    }

    public int getX(){
        return this.X;
    }

    public int getY(){
        return this.Y;
    }

    public int getEuclideanDistance(Location desLocation){
        int deltaX = desLocation.getX() - this.getX();
        int deltaY = desLocation.getY() - this.getY();
        return (int) Math.round(Math.sqrt(deltaX * deltaX + deltaY * deltaY));
    }
}
