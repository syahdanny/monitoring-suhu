package com.mmdiyul.monitoringsuhu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
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
    private EditText textMinSuhu, textMaxSuhu, textMinKelembaban, textMaxKelembaban;

    private long period;
    private String minSuhu, maxSuhu, minKelembaban, maxKelembaban;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tigaPuluhMenit = findViewById(R.id.tigaPuluhMenit);
        satuJam = findViewById(R.id.satuJam);
        duaJam = findViewById(R.id.duaJam);
        enamJam = findViewById(R.id.enamJam);
        duaBelasJam = findViewById(R.id.duaBelasJam);
        textMinSuhu = findViewById(R.id.minSuhu);
        textMaxSuhu = findViewById(R.id.maxSuhu);
        textMinKelembaban = findViewById(R.id.minKelembaban);
        textMaxKelembaban = findViewById(R.id.maxKelembaban);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        Query setting = databaseReference.child("settings");
        setting.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                minSuhu = dataSnapshot.child("minSuhu").getValue().toString();
                maxSuhu = dataSnapshot.child("maxSuhu").getValue().toString();
                minKelembaban = dataSnapshot.child("minKelembaban").getValue().toString();
                maxKelembaban = dataSnapshot.child("maxKelembaban").getValue().toString();

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

                textMinSuhu.setText(minSuhu);
                textMaxSuhu.setText(maxSuhu);
                textMinKelembaban.setText(minKelembaban);
                textMaxKelembaban.setText(maxKelembaban);
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

        DatabaseReference dbMinSuhu = databaseReference.child("settings").child("minSuhu");
        dbMinSuhu.setValue(Double.parseDouble(minSuhu));

        DatabaseReference dbMaxSuhu = databaseReference.child("settings").child("maxSuhu");
        dbMaxSuhu.setValue(Double.parseDouble(maxSuhu));

        DatabaseReference dbMinKelembaban = databaseReference.child("settings").child("minKelembaban");
        dbMinKelembaban.setValue(Double.parseDouble(minKelembaban));

        DatabaseReference dbMaxKelembaban = databaseReference.child("settings").child("maxKelembaban");
        dbMaxKelembaban.setValue(Double.parseDouble(maxKelembaban));

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
