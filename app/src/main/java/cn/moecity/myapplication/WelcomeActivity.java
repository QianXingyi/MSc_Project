package cn.moecity.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.IntentSender;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        createLocationRequest();
        mSpeech = new TextToSpeech(WelcomeActivity.this, new TTSListener());
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
                }
            }
        }
    }
}
