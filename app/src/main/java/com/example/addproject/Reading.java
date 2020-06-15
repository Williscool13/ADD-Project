package com.example.addproject;

public class Reading {
    public String longitude;
    public String latitude;
    public String temperature;
    private String humidity;
    private String pressure;
    public String key;

    public Reading(String k, String t, String h, String lt, String lo, String p){
        this.humidity = h;
        this.temperature = t;
        this.pressure = p;
        this.latitude = lt;
        this.longitude = lo;
        this.key = k;
    }
}
