package com.sample.covid19.Models;

public class dataModelLocation {

    private Coordinates coordinates;

    private String country;

    private int confirmedCount,deathCount,recoveredCount;

    private String Province;

    public dataModelLocation(Coordinates coordinates, String country, int confirmedCount, int deathCount, int recoveredCount, String province) {
        this.coordinates = coordinates;
        this.country = country;
        this.confirmedCount = confirmedCount;
        this.deathCount = deathCount;
        this.recoveredCount = recoveredCount;
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

    public int getDeathCount() {
        return deathCount;
    }

    public int getRecoveredCount() {
        return recoveredCount;
    }

    public String getProvince() {
        return Province;
    }
}
