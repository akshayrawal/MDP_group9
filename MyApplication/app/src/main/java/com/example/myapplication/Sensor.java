package com.example.myapplication;

public class Sensor {
    public int x_coor;
    public int y_coor;

    public Sensor(int x, int y){
        this.y_coor = y;
        this.x_coor = x;
    }

    public void setX(int x){
        this.x_coor = x;
    }

    public void setY(int y){
        this.y_coor = y;
    }

    public int getX(){
        return this.x_coor;
    }

    public int getY(){
        return this.y_coor;
    }

    public boolean isValid(){
        return (x_coor>=0 && x_coor<15 && y_coor>=0 && y_coor<20);
    }
}