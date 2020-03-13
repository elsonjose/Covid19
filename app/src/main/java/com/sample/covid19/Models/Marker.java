package com.sample.covid19.Models;

public class Marker {
    String markerID;
    int confirmed;
    String Country;
    String Province;
    int deaths;
    int recovered;

    public Marker(String markerID, int confirmed, String country, String province, int deaths, int recovered) {
        this.markerID = markerID;
        this.confirmed = confirmed;
        Country = country;
        Province = province;
        this.deaths = deaths;
        this.recovered = recovered;
    }

    public String getMarkerID() {
        return markerID;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public String getCountry() {
        return Country;
    }

    public String getProvince() {
        return Province;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getRecovered() {
        return recovered;
    }
}
