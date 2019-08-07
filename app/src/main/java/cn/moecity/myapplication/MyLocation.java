package cn.moecity.myapplication;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

public class MyLocation {
    private int locNo;
    private String locName;
    private LatLng locLatLng;
    private Boolean isNode;
    private Location location;

    public MyLocation() {

    }

    public MyLocation(int locNo, String locName, LatLng locLatLng, Boolean isNode) {
        this.locNo = locNo;
        this.locName = locName;
        this.locLatLng = locLatLng;
        this.isNode = isNode;
        location = new Location("UoS AAR");
        location.setLatitude(locLatLng.latitude);
        location.setLongitude(locLatLng.longitude);
    }

    public int getLocNo() {
        return locNo;
    }

    public void setLocNo(int locNo) {
        this.locNo = locNo;
    }

    public String getLocName() {
        return locName;
    }

    public void setLocName(String locName) {
        this.locName = locName;
    }

    public Boolean getNode() {
        return isNode;
    }

    public void setNode(Boolean node) {
        isNode = node;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "MyLocation{" +
                "locNo=" + locNo +
                ", locName='" + locName + '\'' +
                ", locLatLng=" + locLatLng +
                ", isNode=" + isNode +
                ", location=" + location +
                '}';
    }
}
