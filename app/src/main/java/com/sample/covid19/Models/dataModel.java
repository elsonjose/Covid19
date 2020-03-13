package com.sample.covid19.Models;

import java.util.List;

public class dataModel {
    private String confirmed;
    private String deaths;
    private String recovered;
    private String last_updated;
    private List<dataModelLocation> locations;

    public dataModel(String confirmed, String deaths, String recovered, String last_updated, List<dataModelLocation> locations) {
        this.confirmed = confirmed;
        this.deaths = deaths;
        this.recovered = recovered;
        this.last_updated = last_updated;
        this.locations = locations;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public String getDeaths() {
        return deaths;
    }

    public String getRecovered() {
        return recovered;
    }

    public String getLast_updated() {
        return last_updated;
    }

    public List<dataModelLocation> getLocations() {
        return locations;
    }
}
