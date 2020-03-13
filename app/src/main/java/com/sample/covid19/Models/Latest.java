package com.sample.covid19.Models;

public class Latest
{
    private int confirmed;

    private int deaths;

    private int recovered;

    public void setConfirmed(int confirmed){
        this.confirmed = confirmed;
    }
    public int getConfirmed(){
        return this.confirmed;
    }
    public void setDeaths(int deaths){
        this.deaths = deaths;
    }
    public int getDeaths(){
        return this.deaths;
    }
    public void setRecovered(int recovered){
        this.recovered = recovered;
    }
    public int getRecovered(){
        return this.recovered;
    }
}