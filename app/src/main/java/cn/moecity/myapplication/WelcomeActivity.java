package cn.moecity.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;

import static cn.moecity.myapplication.MainActivity.REQUEST_CHECK_SETTINGS;

public class WelcomeActivity extends AppCompatActivity {

    private LocationRequest locationRequest;
    private TextToSpeech mSpeech;
    private Button startBtn;
    private LinearLayout waitlayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);
        createLocationRequest();
        startBtn=findViewById(R.id.startBtn);
        waitlayout=findViewById(R.id.wait1layout);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBtn.setVisibility(View.GONE);
                waitlayout.setVisibility(View.VISIBLE);
                LoadTask loadTask=new LoadTask();
                loadTask.execute(5000);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mSpeech = new TextToSpeech(WelcomeActivity.this, new TTSListener());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSpeech.stop();
    }

    protected void createLocationRequest() {
        //get permission
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
                        resolvable.startResolutionForResult(WelcomeActivity.this, REQUEST_CHECK_SETTINGS);
                        Toast.makeText(getApplicationContext(), "Permission OK!", Toast.LENGTH_SHORT).show();
                    } catch (IntentSender.SendIntentException sendEx) {
                        Toast.makeText(getApplicationContext(), "Permission Error!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private class TTSListener implements TextToSpeech.OnInitListener {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                int supported = mSpeech.setLanguage(Locale.UK);
                if (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                    Toast.makeText(getApplicationContext(), "Language unavailable", Toast.LENGTH_LONG).show();
                }else {

                    String speakStr=getString(R.string.welcome_op)+"\n"+getString(R.string.click_to_go);
                    mSpeech.speak(speakStr, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        }
    }
    private class LoadTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }
    }
}
