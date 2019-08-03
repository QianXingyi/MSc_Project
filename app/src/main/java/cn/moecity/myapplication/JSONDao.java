package cn.moecity.myapplication;

import android.location.Location;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONDao {
    private JSONArray getSteps(String apiStr){
        JSONObject direction = null;
        JSONArray routes = null;
        JSONObject legs=null;
        JSONArray steps=null;

        try {
            direction = new JSONObject(apiStr);
            routes = direction.getJSONArray("routes");
            legs=routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0);
            steps=legs.getJSONArray("steps");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Log.e("JSONmsg","Steps are \n"+steps+".");

        return steps;
    }

    public int getDistance(String apiStr){
        JSONObject direction = null;
        JSONArray routes = null;
        JSONObject legs=null;
        JSONObject dis=null;
        int disValue=0;
        try {
            direction = new JSONObject(apiStr);
            routes = direction.getJSONArray("routes");
            legs=routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0);
            dis=legs.getJSONObject("distance");
            disValue=dis.getInt("value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return disValue;
    }

    public int getDuration(String apiStr){
        JSONObject direction = null;
        JSONArray routes = null;
        JSONObject legs=null;
        JSONObject dur=null;
        int durValue=0;
        try {
            direction = new JSONObject(apiStr);
            routes = direction.getJSONArray("routes");
            legs=routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0);
            dur=legs.getJSONObject("duration");
            durValue=dur.getInt("value");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return durValue;
    }

    public List<POI> getStepList(String apiStr){
        List<POI>poiList=new ArrayList<POI>();
        JSONArray steps=getSteps(apiStr);
        for (int i=0;i<steps.length();i++)
        {
            try {
                JSONObject jsonObject=steps.getJSONObject(i);
                POI objTemp=new POI();
                objTemp.setDistance(jsonObject.getJSONObject("distance").getInt("value"));
                objTemp.setDuration(jsonObject.getJSONObject("duration").getInt("value"));
                objTemp.setPoiID(i);
                objTemp.setHtmlMsg(jsonObject.getString("html_instructions")
                        .replaceAll("<b>","")
                        .replaceAll("<\\/b>",""));
                Double startLat,startLng,endLat,endLng;
                startLat=jsonObject.getJSONObject("start_location").getDouble("lat");
                startLng=jsonObject.getJSONObject("start_location").getDouble("lng");
                endLat=jsonObject.getJSONObject("end_location").getDouble("lat");
                endLng=jsonObject.getJSONObject("end_location").getDouble("lng");
                objTemp.setStarLocation(startLat,startLng);
                objTemp.setEndLocation(endLat,endLng);
                poiList.add(objTemp);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            //Log.e("count",i+"");
        }
        return poiList;
    }
}
