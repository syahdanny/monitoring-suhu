package com.mmdiyul.monitoringsuhu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private RadioButton tigaPuluhMenit, satuJam, duaJam, enamJam, duaBelasJam;

    private long period;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tigaPuluhMenit = findViewById(R.id.tigaPuluhMenit);
        satuJam = findViewById(R.id.satuJam);
        duaJam = findViewById(R.id.duaJam);
        enamJam = findViewById(R.id.enamJam);
        duaBelasJam = findViewById(R.id.duaBelasJam);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        Query setting = databaseReference.child("settings");
        setting.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                period = Long.parseLong(dataSnapshot.child("period").getValue().toString());

                if (period == 1800000) {
                    tigaPuluhMenit.setChecked(true);
                } else if (period == 3600000) {
                    satuJam.setChecked(true);
                } else if (period == 7200000) {
                    duaJam.setChecked(true);
                } else if (period == 21600000) {
                    enamJam.setChecked(true);
                } else if (period == 43200000) {
                    duaBelasJam.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void handleSimpan(View view) {
        if (tigaPuluhMenit.isChecked()) {
            period = 1800000;
        } else if (satuJam.isChecked()) {
            period = 3600000;
        } else if (duaJam.isChecked()) {
            period = 7200000;
        } else if (enamJam.isChecked()) {
            period = 21600000;
        } else if (duaBelasJam.isChecked()) {
            period = 43200000;
        }

        DatabaseReference settings = databaseReference.child("settings").child("period");
        settings.setValue(period);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);

        Toast.makeText(this, "Pengaturan tersimpan!", Toast.LENGTH_SHORT).show();
    }
}
