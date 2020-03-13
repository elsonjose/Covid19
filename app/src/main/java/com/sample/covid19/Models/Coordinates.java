package com.sample.covid19.Models;

public class Coordinates
{
    private String lat;

    private String Long;

    public Coordinates(String lat, String aLong) {
        this.lat = lat;
        Long = aLong;
    }

    public String getLat(){
        return this.lat;
    }
    public String getLong() {
        return Long;
    }

}