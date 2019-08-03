package cn.moecity.myapplication;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MyLocationDao {
    public List<MyLocation> createMyLocations(){
        List<MyLocation> myLocationList=new ArrayList<>();
        myLocationList.add(new MyLocation(1,"Southampton Uni Interchange",new LatLng(50.9361869,-1.396808),true));

        myLocationList.add(new MyLocation(2,"Building 53 \nMountbatten Building",new LatLng(50.9373694,-1.3980566),true));

        myLocationList.add(new MyLocation(3,"Building 58 \nMurray Building",new LatLng(50.9367934,-1.3984242),true));

        myLocationList.add(new MyLocation(4,"Building 42 \nPiazza Restaurant",new LatLng(50.9344523,-1.3972437),true));

        myLocationList.add(new MyLocation(5,"Building 36 \nHartley Library",new LatLng(50.9350852,-1.3959189),true));

        myLocationList.add(new MyLocation(6,"Building 57 \n SUSU Shop",new LatLng(50.9344531,-1.3970102),true));

        myLocationList.add(new MyLocation(7,"Building 32 \nEEE Building",new LatLng(50.936052,-1.3959484),true));

        myLocationList.add(new MyLocation(8,"Building 59 \nZepler Building",new LatLng(50.9373694,-1.3980566),true));

        myLocationList.add(new MyLocation(9,"Building 16 \nLaboratory",new LatLng(50.9376195,-1.3958665),true));

        myLocationList.add(new MyLocation(10,"Building 18 \nJubilee Sports Centre",new LatLng(50.9341497,-1.3963619),true));

        return myLocationList;

    }

    public MyLocation getLocationByLocId(int locId){
        MyLocation myLocation=new MyLocation();
        List<MyLocation> myLocationList=createMyLocations();
        for (int i = 0; i < myLocationList.size(); i++) {
            if (myLocationList.get(i).getLocNo()==locId){
                myLocation=myLocationList.get(i);
            }
        }
        return myLocation;
    }
}
