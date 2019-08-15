package cn.moecity.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

public class EndActivity extends AppCompatActivity {
    private Button reBtn;
    private TextToSpeech mSpeech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);
        reBtn=findViewById(R.id.restartBtn);
        reBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),WelcomeActivity.class));
                finish();
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
        mSpeech = new TextToSpeech(EndActivity.this, new TTSListener());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSpeech.shutdown();

    }
    private class TTSListener implements TextToSpeech.OnInitListener {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                int supported = mSpeech.setLanguage(Locale.UK);
                if (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                    Toast.makeText(getApplicationContext(), "Language unavailable", Toast.LENGTH_LONG).show();
                }else {
                    String speakStr=getString(R.string.end_slogan);
                    mSpeech.speak(speakStr, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        }
    }
}
