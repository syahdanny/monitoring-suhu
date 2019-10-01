package com.mmdiyul.monitoringsuhu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private TextView greetings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ucapan selamat berdasarkan waktu.
        greetings = findViewById(R.id.greetings);

        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (currentHour < 10) {
            greetings.setText("Selamat Pagi!");
        } else if (currentHour < 15) {
            greetings.setText("Selamat Siang!");
        } else if (currentHour < 18) {
            greetings.setText("Selamat Sore!");
        } else {
            greetings.setText("Selamat Malam!");
        }
    }
}
