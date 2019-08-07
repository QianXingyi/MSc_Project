package cn.moecity.myapplication;

import android.location.Location;


public class POI {
    private int poiID;
    private int distance;
    private int duration;
    private Location starLocation;
    private Location endLocation;
    private String htmlMsg;
    private Boolean isUsed;

    public POI() {
        isUsed = false;
    }

    public POI(int poiID, int distance, int duration, Location starLocation, Location endLocation, String htmlMsg, Boolean isUsed) {
        this.poiID = poiID;
        this.distance = distance;
        this.duration = duration;
        this.starLocation = starLocation;
        this.endLocation = endLocation;
        this.htmlMsg = htmlMsg;
        this.isUsed = isUsed;
    }

    public Boolean getUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    public int getPoiID() {
        return poiID;
    }

    public void setPoiID(int poiID) {
        this.poiID = poiID;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Location getStarLocation() {
        return starLocation;
    }

    public void setStarLocation(Double lat,Double lng) {
        Location location=new Location("");
        location.setLatitude(lat);
        location.setLongitude(lng);
        starLocation=location;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Double lat,Double lng) {
        Location location=new Location("");
        location.setLatitude(lat);
        location.setLongitude(lng);
        endLocation=location;
    }

    public String getHtmlMsg() {
        return htmlMsg;
    }

    public void setHtmlMsg(String htmlMsg) {
        this.htmlMsg = htmlMsg;
    }

    @Override
    public String toString() {
        return "POI{" +
                "poiID=" + poiID +
                ", distance=" + distance +
                ", duration=" + duration +
                ", starLocation=" + starLocation +
                ", endLocation=" + endLocation +
                ", htmlMsg='" + htmlMsg + '\'' +
                '}';
    }
}
