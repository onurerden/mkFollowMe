package com.belbim.kopter.followme;

import java.sql.Timestamp;

/**
 * Created by eakbiyik on 29.12.2014.
 */
public class FollowMe {
    private Double lat = 0.0;
    private Double lng = 0.0;
    private int bearing = 0;
    //int kopterID = 0;
    private Timestamp time ;
    private int event=0;
    private int followMeDeviceId;
    private int routeId;
    int sessionId = -1;


    public int getRouteId(){
        return routeId;
    }

    public void setRouteId(int routeId){
        this.routeId=routeId;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public int getBearing() {
        return bearing;
    }

    public void setBearing(int bearing) {
        this.bearing = bearing;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public int getFollowMeDeviceId() {
        return followMeDeviceId;
    }

    public void setFollowMeDeviceId(int followMeDeviceId) {this.followMeDeviceId = followMeDeviceId;}

    public int getSessionId() {return this.sessionId;}

    public void setSessionId(int id) {this.sessionId = id;}
}
