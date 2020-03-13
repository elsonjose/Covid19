package com.sample.covid19.Models;

import java.util.List;

public class Confirmed
{
    private String last_updated;


    private List<Locations> locations;



    public Confirmed(String last_updated, int latest, List<Locations> locations) {
        this.last_updated = last_updated;
        this.locations = locations;
    }

    public String getLast_updated() {
        return last_updated;
    }


    public List<Locations> getLocations() {
        return locations;
    }

}