package com.epl.annotationmanager;

public class LatLong {
    private double latitude;
    private double longitude;

    public void setLatitude(double _lat){
        latitude = _lat;
    }

    public void setLongitude(double _lon){
        longitude = _lon;
    }

    public double getLatitude(){
        return  latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
