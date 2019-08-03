package cn.moecity.myapplication;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private List<Node> nodeList = new ArrayList<>();
    private List<Node> visibleList = new ArrayList<>();
    private NodeDao nodeDao = new NodeDao();
    private SimpleAdapter simpleAdapter;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private TextView latLngView;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private int distanceTemp;
    private Location myNextLoc, currentLoc, destinationLoc;
    private List<POI> steps = new ArrayList<>();
    private int disValue, disDir;
    private int durValue;
    private Node dirTemp;
    private Boolean isDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        latLngView = findViewById(R.id.textView);
        createLocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateLocInfo(locationResult);

            }
        };
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationUpdate();
        nodeList = nodeDao.CreateNodes();
        JSONArray jsonArray = nodeDao.SaveToJSON(nodeList);
        Log.e("JSON", jsonArray.toString());
        listView = findViewById(R.id.showNode);
        updateList();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("click", visibleList.get(position).getLocName());
                nodeList = nodeDao.UnlockNode(nodeList, visibleList.get(position).getNodeNo());
                updateList();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void startLocationUpdates() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback, Looper.myLooper());
    }

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(100);
        locationRequest.setFastestInterval(100);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                    }
                }
            }
        });
    }


    private void locationUpdate() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            }
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            myNextLoc = new Location("");
                            myNextLoc.setLongitude(nodeList.get(6).getMyLocation().getLocLatLng().longitude);
                            myNextLoc.setLatitude(nodeList.get(0).getMyLocation().getLocLatLng().latitude);
                            latLngView.setText(location.getProvider() + ","
                                    + location.getLatitude() + ","
                                    + location.getLongitude() +
                                    "," + location.distanceTo(myNextLoc) + "m");
                            Log.e("initial", "get the initial data");
                            updateList();
                        } else {
                            latLngView.setText("No");
                        }
                    }
                });
    }

    private void updateLocInfo(LocationResult locationResult){
        if (locationResult.getLastLocation() != null) {
            myNextLoc = new Location("");
            Node nowLocation;
            for (int i = 0; i < visibleList.size(); i++) {
                nowLocation = new Node();
                nowLocation = visibleList.get(i);
                myNextLoc.setProvider(nowLocation.getLocName());
                myNextLoc.setLongitude(nowLocation.getMyLocation().getLocLatLng().longitude);
                myNextLoc.setLatitude(nowLocation.getMyLocation().getLocLatLng().latitude);
                currentLoc = locationResult.getLastLocation();
                distanceTemp = (int) locationResult.getLastLocation().distanceTo(myNextLoc);
                visibleList.get(i).setDistanceToCurrent(distanceTemp);
                //Log.e("nextLoc",myNextLoc.getProvider()+","+visibleList.get(i).getDistanceToCurrent());
                if (visibleList.get(i).getDistanceToCurrent() <= 20) {
                    nodeList = nodeDao.UnlockNode(nodeList, nowLocation.getNodeNo());
                    updateList();

                }
            }

            latLngView.setText(locationResult.getLastLocation().getProvider()
                    + "," + locationResult.getLastLocation().getLatitude() + ","
                    + locationResult.getLastLocation().getLongitude()
                    + ",\n" + nodeDao.SaveToJSON(visibleList));
            Log.e("location", "get");
            if (isDone) {
                //get new direction
                //start new route
                updateDirection();
                isDone = false;
            }
        } else {
            latLngView.setText("No location data!");
        }
    }

    private void updateList() {
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        visibleList = new ArrayList<>();
        for (int i = 0; i < nodeList.size(); i++) {
            if (nodeList.get(i).getVisible())
                visibleList.add(nodeList.get(i));
        }
        if (visibleList.size() <= 0) {
            nodeList = nodeDao.CreateNodes();
            updateList();
        }
        for (int i = 0; i < visibleList.size(); i++) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("icon", R.mipmap.ic_launcher_round);
            listItem.put("name", visibleList.get(i).getLocName());
            listItem.put("nodeNo", visibleList.get(i).getNodeNo());
            listItems.add(listItem);
        }
        if (visibleList.size() > 1) {
            isDone = false;
            AfterDelayTask afterDelayTask = new AfterDelayTask();
            afterDelayTask.execute(30000);
        } else {
            Log.e("direction", "direction");
            isDone = false;
            DelayAsyncTask delayAsyncTask = new DelayAsyncTask();
            delayAsyncTask.execute(3000);
        }
        simpleAdapter = new SimpleAdapter(this, listItems, R.layout.listitem,
                new String[]{"icon", "name", "nodeNo"},
                new int[]{R.id.item_img, R.id.item_content, R.id.item_title});
        listView.setAdapter(simpleAdapter);
    }

    private static String readMyInputStream(InputStream is) {
        byte[] result;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            is.close();
            baos.close();
            result = baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            String errorStr = "error in getting data";
            return errorStr;
        }
        return new String(result);
    }

    private void updateDirection(){

    }

    private class AfterDelayTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                stopLocationUpdates();
                Log.e("msg", "block");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            DelayAsyncTask delayAsyncTask = new DelayAsyncTask();
            delayAsyncTask.execute(3000);
        }
    }

    private class DelayAsyncTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                stopLocationUpdates();
                Log.e("msg", "block");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            startLocationUpdates();
            Log.e("msg", "recovered");


            for (int i = 0; i < visibleList.size(); i++) {
                if (i == 0) {
                    destinationLoc = visibleList.get(i).getMyLocation().getLocation();
                    disDir = visibleList.get(i).getDistanceToCurrent();
                    dirTemp = visibleList.get(i);
                } else {
                    if (visibleList.get(i).getDistanceToCurrent() < disDir) {
                        destinationLoc = visibleList.get(i).getMyLocation().getLocation();
                        disDir = visibleList.get(i).getDistanceToCurrent();
                        dirTemp = visibleList.get(i);
                    }
                }
            }
            Log.e("direction is", dirTemp.getLocName());
            DirectionApiTask directionApiTask = new DirectionApiTask();
            directionApiTask.execute(5000);
        }

    }

    private class DirectionApiTask extends AsyncTask<Object, Object, Message> {


        @Override
        protected Message doInBackground(Object... objects) {
            int code;
            try {
                String path = "https://maps.googleapis.com/maps/api/directions/" +
                        "json?" +
                        "&language=en-gb" +
                        "&mode=walking" +
                        "&origin=" + currentLoc.getLatitude() + "," + currentLoc.getLongitude() +
                        "&destination=" + destinationLoc.getLatitude() + "," + destinationLoc.getLongitude() +
                        "&key=" + getString(R.string.google_maps_key);
                URL url = new URL(path);
                /**
                 * HTTP request
                 */
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                code = conn.getResponseCode();
                if (code == 200) {

                    InputStream is = conn.getInputStream();
                    String result = readMyInputStream(is);

                    Message msg = new Message();
                    msg.obj = result;
                    msg.what = 1;
                    return msg;

                } else {

                    Message msg = new Message();
                    msg.what = 2;
                    return msg;
                }
            } catch (Exception e) {

                e.printStackTrace();
                Message msg = new Message();
                msg.what = 0;
                return msg;
            }
        }

        @Override
        protected void onPostExecute(Message message) {
            super.onPostExecute(message);
            switch (message.what) {
                case 1:
                    /**
                     * get Direction API Data
                     */
                    JSONDao jsonDao = new JSONDao();
                    Toast.makeText(MainActivity.this, "getting data OK", Toast.LENGTH_SHORT)
                            .show();
                    //Log.e("countlocation",visibleList.get(count).getLocName());
                    steps = jsonDao.getStepList(message.obj.toString());
                    Log.e("steps thread", steps.toString());
                    disValue = jsonDao.getDistance(message.obj.toString());
                    durValue = jsonDao.getDuration(message.obj.toString());
                    isDone = true;
                    break;
                case 0:
                    Toast.makeText(MainActivity.this, "getting data error", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case 2:
                    Toast.makeText(MainActivity.this, "request code error, not 200",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

}
