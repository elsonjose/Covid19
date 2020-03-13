package com.sample.covid19.Models;

public class Locations
{
    private Coordinates coordinates;

    private String country;

    private int confirmedCount;

    private String Province;

    public Locations(Coordinates coordinates, String country, int confirmedCount, String province) {
        this.coordinates = coordinates;
        this.country = country;
        this.confirmedCount = confirmedCount;
        Province = province;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getCountry() {
        return country;
    }

    public int getConfirmedCount() {
        return confirmedCount;
    }

    public String getProvince() {
        return Province;
    }
}