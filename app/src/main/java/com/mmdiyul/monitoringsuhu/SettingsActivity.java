package com.mmdiyul.monitoringsuhu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;

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
}
