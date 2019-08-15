package cn.moecity.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private List<Node> nodeList = new ArrayList<>();
    private List<Node> visibleList = new ArrayList<>();
    private NodeDao nodeDao = new NodeDao();
    private SimpleAdapter simpleAdapter;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private TextView latLngView, detailView;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private int distanceTemp;
    private int bearingTemp;
    private Location myNextLoc, currentLoc, destinationLoc;
    private int destNo, destDir;
    private List<POI> steps = new ArrayList<>();
    private int disValue, disDir, userDir, destDis;
    private int durValue;
    private Node dirTemp;
    private Boolean isDone = false;
    private Boolean isStart = false;
    private Boolean isFinished=false;
    private TextToSpeech mSpeech;
    private Button startBtn;
    private String interchange_op, interchange_ed;
    private String building_53_op, building_53_ed;
    private String building_58_op, building_58_ed;
    private String restaurant_op, restaurant_ed;
    private String library_op, library_ed;
    private String shop_op, shop_ed;
    private String eee_op, eee_ed;
    private String loc1_op, loc1_ed;
    private String loc2_op, loc2_ed;
    private String sports_op, sports_ed;
    private String end_op, end_ed;
    private String opString, edString;
    private int choseLoc;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    float[] mValues;
    private MediaPlayer mediaPlayer;
    private SensorEventListener mListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            mValues = event.values;
            float direction = mValues[0];
            userDir = (int) direction;
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    private static String readMyInputStream(InputStream is) {
        //binary result to string
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
            return "Error in reading stream!";
        }
        return new String(result);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mListener, mSensor,
                SensorManager.SENSOR_DELAY_GAME);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mListener);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        stopLocationUpdates();
        mSpeech.shutdown();
        mediaPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(mListener);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        stopLocationUpdates();
        mSpeech.shutdown();
        mediaPlayer.stop();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initString();
        choseLoc = 0;
        destNo = 0;
        destDir = 0;
        destDis = 0;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        latLngView = findViewById(R.id.textView);
        listView = findViewById(R.id.showNode);
        detailView = findViewById(R.id.textView2);
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bibi);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        createLocationRequest();
        mSpeech = new TextToSpeech(MainActivity.this, new TTSListener());
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                updateLocInfo(locationResult);
            }
        };
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //initial location Services

        locationUpdate();

        nodeList = nodeDao.CreateNodes();
        opString = interchange_op;
        edString = interchange_ed;
        //For the function of using file to import nodes
        //JSONArray jsonArray = nodeDao.SaveToJSON(nodeList);
        //Log.e("JSON", jsonArray.toString());

        updateList();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.e("click", visibleList.get(position).getLocName());
                nodeList = nodeDao.UnlockNode(nodeList, visibleList.get(position).getNodeNo());
                updateList();
            }
        });

    }

    private void refreshString(int destNo, String locName) {
        initString();
        String temp = "OK! Then you want to go to" + locName + ",right?";
        switch (destNo) {
            case 1:
                opString = interchange_op;
                edString = interchange_ed;
                break;
            case 2:
                opString = building_53_op;
                edString = building_53_ed;
                break;
            case 3:
                opString = building_58_op;
                edString = building_58_ed;
                break;
            case 4:
                opString = temp + restaurant_op;
                edString = restaurant_ed;
                break;

            case 5:
                opString = temp + library_op;
                edString = library_ed;
                break;

            case 6:
                opString = temp + shop_op;
                edString = shop_ed;
                break;
            case 7:
                opString = eee_op;
                edString = eee_ed;
                break;
            case 8:
            case 9:
            case 10:

                switch (choseLoc) {
                    case 0:
                        opString = temp + loc1_op;
                        edString = loc1_ed;
                        choseLoc++;
                        break;
                    case 1:
                        opString = temp + loc2_op;
                        edString = loc2_ed;
                        choseLoc = 3;
                        break;
                }
                break;
            case 11:
                opString = sports_op;
                edString = sports_ed;
                break;
            case 12:
                opString = end_op;
                edString = end_ed;
                choseLoc = 0;
                break;
        }
    }

    protected void createLocationRequest() {
        //get permission
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(2000);
        locationRequest.setFastestInterval(2000);
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

    private void initString() {

        interchange_op = getString(R.string.interchange_op);
        interchange_ed = getString(R.string.interchange_ed);

        building_53_op = getString(R.string.building_53_op);
        building_53_ed = getString(R.string.building_53_ed);

        building_58_op = getString(R.string.building_58_op);
        building_58_ed = getString(R.string.building_58_ed);

        restaurant_op = getString(R.string.restaurant_op);
        restaurant_ed = getString(R.string.restaurant_ed);

        library_op = getString(R.string.library_op);
        library_ed = getString(R.string.library_ed);

        shop_op = getString(R.string.shop_op);
        shop_ed = getString(R.string.shop_ed);

        eee_op = getString(R.string.eee_op);
        eee_ed = getString(R.string.eee_ed);

        loc1_op = getString(R.string.loc1_op);
        loc1_ed = getString(R.string.loc1_ed);

        loc2_op = getString(R.string.loc2_op);
        loc2_ed = getString(R.string.loc2_ed);

        sports_op = getString(R.string.sports_op);
        sports_ed = getString(R.string.sports_ed);

        end_op = getString(R.string.end_op);
        end_ed = getString(R.string.end_ed);


    }

    private void locationUpdate() {
        //initial location info
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
                            myNextLoc.setLongitude(nodeList.get(0).getMyLocation().getLocation().getLongitude());
                            myNextLoc.setLatitude(nodeList.get(0).getMyLocation().getLocation().getLatitude());
                            latLngView.setText(location.getProvider() + ","
                                    + location.getLatitude() + ","
                                    + location.getLongitude() +
                                    "," + location.distanceTo(myNextLoc) + "m," + location.bearingTo(myNextLoc) + ".");
                            //Log.e("initial", "get the initial data");
                            updateList();
                        } else {
                            latLngView.setText("No");
                        }
                    }
                });
    }

    private void updateLocInfo(LocationResult locationResult) {
        if (locationResult.getLastLocation() != null) {
            myNextLoc = new Location(getString(R.string.app_name));
            for (Node nowLocation : visibleList) {
                myNextLoc.setProvider(nowLocation.getLocName());
                myNextLoc.setLongitude(nowLocation.getMyLocation().getLocation().getLongitude());
                myNextLoc.setLatitude(nowLocation.getMyLocation().getLocation().getLatitude());
                currentLoc = locationResult.getLastLocation();
                distanceTemp = (int) locationResult.getLastLocation().distanceTo(myNextLoc);
                bearingTemp = (int) locationResult.getLastLocation().bearingTo(myNextLoc);
                //put the distances between nodes and the current location into the object
                nowLocation.setDistanceToCurrent(distanceTemp);
                nowLocation.setBearingToCurrent(bearingTemp);
                //Log.e("nextLoc",myNextLoc.getProvider()+","+visibleList.get(i).getDistanceToCurrent());
                if (nowLocation.getNodeNo() == destNo) {
                    destDir = bearingTemp;
                    destDis = distanceTemp;
                }
                if (nowLocation.getDistanceToCurrent() <= 20) {
                    nodeList = nodeDao.UnlockNode(nodeList, nowLocation.getNodeNo());
                    updateList();

                }
            }

            //for mediaPlayer
            latLngView.setText(locationResult.getLastLocation().getProvider()
                    + "," + locationResult.getLastLocation().getLatitude() + ","
                    + locationResult.getLastLocation().getLongitude()
                    + ",\n" + nodeDao.SaveToJSON(visibleList));
            Log.e("location update", "get");
            if (isDone) {
                //get new direction
                //start new route
                Log.e("location update", "poi");
                setMediaPlayer();
                updateDirection(locationResult);
            }
        } else {
            latLngView.setText("No location data!");
        }
    }

    private void updateDirection(LocationResult locationResult) {
        ///Speak out the steps
        for (POI step : steps) {
            int disTemp = (int) step.getStarLocation().distanceTo(currentLoc);
            if (disTemp <= 20 && !mSpeech.isSpeaking() && !step.getUsed()) {
                isDone = true;
                String speakStr = step.getHtmlMsg();
                mSpeech.speak(speakStr, TextToSpeech.QUEUE_FLUSH, null, null);
                //Log.e("poi",step.getPoiID()+"");
                step.setUsed(true);
                isStart = true;
            }
        }
        ///User indoor or not on the main rd
        if (!isStart && !mSpeech.isSpeaking()) {
            mSpeech.speak("Please go to the main road!", TextToSpeech.QUEUE_FLUSH, null, null);
            isStart = true;
        }

        detailView.setText(dirTemp.getLocName() + "\n" + steps.toString());
    }

    private void updateList() {
        //update the list of nodes
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        visibleList = new ArrayList<>();
        checkEdSpeak();
        for (Node tempNode : nodeList) {
            if (tempNode.getVisible())
                visibleList.add(tempNode);
        }
        if (visibleList.size() <= 0) {
            nodeList = nodeDao.CreateNodes();
            updateList();

        }
        for (Node visibleNode : visibleList) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("icon", R.mipmap.ic_launcher_round);
            listItem.put("name", visibleNode.getLocName());
            listItem.put("nodeNo", visibleNode.getNodeNo());
            listItems.add(listItem);
        }
        if (visibleList.size() > 1) {
            isDone = false;
            AfterDelayTask afterDelayTask = new AfterDelayTask();
            afterDelayTask.execute(30000);
        } else {
            //Log.e("direction", "direction");
            isDone = false;
            DelayAsyncTask delayAsyncTask = new DelayAsyncTask();
            delayAsyncTask.execute(3000);
        }
        simpleAdapter = new SimpleAdapter(this, listItems, R.layout.listitem,
                new String[]{"icon", "name", "nodeNo"},
                new int[]{R.id.item_img, R.id.item_content, R.id.item_title});
        listView.setAdapter(simpleAdapter);
    }
    private void checkFinish(){
        if (!mSpeech.isSpeaking()){
            isFinished=true;
            mSpeech.shutdown();
            mediaPlayer.stop();
            startActivity(new Intent(getApplicationContext(),EndActivity.class));
            finish();
        }else checkFinish();
    }

    private void setMediaPlayer() {
        if (!isFinished) {
            if (!mSpeech.isSpeaking()) {
                mediaPlayer.stop();
                if (destDis > 200)
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bibi_2);
                else if (destDis > 100)
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bibi_1);
                else
                    mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bibi);
                if (destDir > userDir) {
                    Toast.makeText(MainActivity.this, destDir - userDir + "!", Toast.LENGTH_SHORT).show();
                    if (destDir - userDir < 90)
                        mediaPlayer.setVolume(1.0f, 1.0f);
                    else if (destDir - userDir > 180)
                        mediaPlayer.setVolume(1.0f, 0.0f);
                    else
                        mediaPlayer.setVolume(0.0f, 1.0f);
                } else {
                    Toast.makeText(MainActivity.this, userDir - destDir + "!", Toast.LENGTH_SHORT).show();
                    if (userDir - destDir < 90)
                        mediaPlayer.setVolume(1.0f, 1.0f);
                    else if (userDir - destDir > 180)
                        mediaPlayer.setVolume(0.0f, 1.0f);
                    else
                        mediaPlayer.setVolume(1.0f, 0.0f);
                }
                mediaPlayer.start();
            } else {
                setMediaPlayer();
            }
        }else {
            mediaPlayer.stop();
            mSpeech.shutdown();
        }

    }

    private class AfterDelayTask extends AsyncTask {
        //delay for user moving
        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                stopLocationUpdates();
                Log.e("msg", "long block");
                Thread.sleep(30000);
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
        //delay for data input to object
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

            //Init the data with the first node in visible list
            destinationLoc = visibleList.get(0).getMyLocation().getLocation();
            destNo = visibleList.get(0).getNodeNo();
            destDir = visibleList.get(0).getBearingToCurrent();
            disDir = visibleList.get(0).getDistanceToCurrent();
            destDis = disDir;
            dirTemp = visibleList.get(0);
            for (Node visibleNode : visibleList) {

                if (visibleNode.getDistanceToCurrent() < disDir) {
                    destinationLoc = visibleNode.getMyLocation().getLocation();
                    destNo = visibleNode.getNodeNo();
                    destDir = visibleNode.getBearingToCurrent();
                    disDir = visibleNode.getDistanceToCurrent();
                    dirTemp = visibleNode;
                    destDis = disDir;
                }

            }
            //Log.e("direction is", dirTemp.getLocName());
            refreshString(destNo, dirTemp.getLocName());
            detailView.setText(dirTemp.getLocName() + "\n" + steps.toString() + "\n" + destNo + "\n" + destDir);
            checkOpSpeak();
            DirectionApiTask directionApiTask = new DirectionApiTask();
            directionApiTask.execute(5000);
        }

    }

    private void checkOpSpeak() {
        if (mSpeech.isSpeaking()) {
            checkOpSpeak();
        } else {
            mSpeech.speak(opString, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void checkEdSpeak() {
        if (mSpeech.isSpeaking()) {
            checkEdSpeak();
        } else {
            mSpeech.speak(edString, TextToSpeech.QUEUE_FLUSH, null, null);
            if (edString.equals(end_ed)){
                checkFinish();
            }
        }
    }

    private class DirectionApiTask extends AsyncTask<Object, Object, Message> {
        //class to connect Direction Api
        @Override
        protected Message doInBackground(Object... objects) {
            int code;
            try {
                String path = getString(R.string.direction_api_address) +
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
                    /*
                     * get Direction API Data
                     */
                    JSONDao jsonDao = new JSONDao();
                    Toast.makeText(getApplicationContext(), "Getting data OK", Toast.LENGTH_SHORT)
                            .show();
                    //Log.e("countlocation",visibleList.get(count).getLocName());
                    steps = jsonDao.getStepList(message.obj.toString());
                    isStart = false;
                    //Log.e("steps thread", steps.toString());
                    disValue = jsonDao.getDistance(message.obj.toString());
                    durValue = jsonDao.getDuration(message.obj.toString());
                    isDone = true;
                    break;
                case 0:
                    Toast.makeText(getApplicationContext(), "getting data error", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "request code error, not 200",
                            Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    }

    private class TTSListener implements TextToSpeech.OnInitListener {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                //Toast.makeText(getApplicationContext(),"TTS success",Toast.LENGTH_SHORT).show();
                int supported = mSpeech.setLanguage(Locale.UK);
                if (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                    Toast.makeText(getApplicationContext(), "Language unavailable", Toast.LENGTH_LONG).show();
                }
//                else {
//                    //Toast.makeText(getApplicationContext(), "Language available", Toast.LENGTH_LONG).show();
//                }
            }
        }
    }

}
