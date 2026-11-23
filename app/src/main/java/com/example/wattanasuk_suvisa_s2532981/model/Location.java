package com.example.wattanasuk_suvisa_s2532981.model;

public class Location {
    private String road = "";
    private String direction = "";
    private MapPoint from = new MapPoint();
    private MapPoint to = new MapPoint();

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public MapPoint getFrom() {
        return from;
    }

    public MapPoint getTo() {
        return to;
    }
}
