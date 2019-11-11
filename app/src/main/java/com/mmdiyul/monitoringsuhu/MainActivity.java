package com.mmdiyul.monitoringsuhu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mmdiyul.monitoringsuhu.adapter.SensorAdapter;
import com.mmdiyul.monitoringsuhu.model.Sensor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView greetings;
    private TextView textSuhu;
    private TextView textKelembaban;
    private TextView textUpdate;
    private TextView textAlert;
    private LinearLayout layoutAlert;
    private LineChart lineChart;

    private DatabaseReference databaseReference;

    private int id, tenId;
    private double suhu, tenSuhu;
    private double kelembaban, tenKelembaban;
    private String update, tenUpdate;
    private double minSuhu, maxSuhu, minKelembaban, maxKelembaban;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ScrollView scrollView;
    private RecyclerView recyclerView;

    private List<Sensor> sensorList = new ArrayList<>();
    private SensorAdapter sensorAdapter;
    private ArrayList<Entry> listSuhu = new ArrayList<>();
    private ArrayList<Entry> listKelembaban = new ArrayList<>();
    private ArrayList<LineDataSet> lines;

    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // bind
        greetings = findViewById(R.id.greetings);
        textSuhu = findViewById(R.id.textSuhu);
        textKelembaban = findViewById(R.id.textKelembaban);
        textUpdate = findViewById(R.id.textUpdate);
        textAlert = findViewById(R.id.textAlert);
        swipeRefreshLayout = findViewById(R.id.refresh);
        scrollView = findViewById(R.id.scrollView);
        layoutAlert = findViewById(R.id.forAlert);
        recyclerView = findViewById(R.id.recyclerView);

        // ucapan selamat berdasarkan waktu.
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

        // Database
        databaseReference = FirebaseDatabase.getInstance().getReference();
        // last data
        Query lastData = databaseReference.child("sensor");
        lastData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long lengthData = dataSnapshot.getChildrenCount();
                long lastChild = lengthData - 1;
                long tenLastChild = lengthData - 10;
                try {
                    suhu = Double.parseDouble(dataSnapshot.child(lastChild + "/suhu").getValue().toString());
                    kelembaban = Double.parseDouble(dataSnapshot.child(lastChild + "/kelembaban").getValue().toString());
                    update = dataSnapshot.child(lastChild + "/updatedAt").getValue().toString();

                    textSuhu.setText(suhu + " C");
                    textKelembaban.setText(kelembaban + " %");
                    textUpdate.setText("Last update : " +update);
                } catch (NullPointerException nullPointer) {
                    Log.d("Error: ", nullPointer.getMessage());
                }

                while (index != 10) {
                    try {
                        id = Integer.parseInt(dataSnapshot.child(lastChild + "/id").getValue().toString());
                        suhu = Double.parseDouble(dataSnapshot.child(lastChild + "/suhu").getValue().toString());
                        kelembaban = Double.parseDouble(dataSnapshot.child(lastChild + "/kelembaban").getValue().toString());
                        update = dataSnapshot.child(lastChild + "/updatedAt").getValue().toString();

                        tenId = Integer.parseInt(dataSnapshot.child(tenLastChild + "/id").getValue().toString());
                        tenSuhu = Double.parseDouble(dataSnapshot.child(tenLastChild + "/suhu").getValue().toString());
                        tenKelembaban = Double.parseDouble(dataSnapshot.child(tenLastChild + "/kelembaban").getValue().toString());
                        tenUpdate = dataSnapshot.child(tenLastChild + "/updatedAt").getValue().toString();

                        sensorList.add(new Sensor(tenId, tenSuhu, tenKelembaban, tenUpdate));
                        listSuhu.add(new Entry(Float.parseFloat(String.valueOf(index)), Float.parseFloat(String.valueOf(tenSuhu))));
                        listKelembaban.add(new Entry(Float.parseFloat(String.valueOf(index)), Float.parseFloat(String.valueOf(tenKelembaban))));

                        index++;
                        lastChild--;
                        tenLastChild++;
                    } catch (NullPointerException nullPointer) {
                        Log.d("Error: ", nullPointer.getMessage());
                    }
                }

                lineChart = (LineChart)findViewById(R.id.chart);

                LineDataSet lineDataSetSuhu = new LineDataSet(listSuhu, "Suhu");
                LineDataSet lineDataSetKelembaban = new LineDataSet(listKelembaban, "Kelembaban");

                lineDataSetSuhu.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                lineDataSetKelembaban.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));

                XAxis xAxis = lineChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                final String[] x = new String[]{"1", "2", "3", "4", "5", "6", "8", "9", "10"};
                ValueFormatter formatter = new ValueFormatter() {
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        return x[(int) value];
                    }
                };
                xAxis.setGranularity(1f);
                xAxis.setValueFormatter(formatter);

                YAxis yAxisRight = lineChart.getAxisRight();
                yAxisRight.setEnabled(false);

                YAxis yAxisLeft = lineChart.getAxisLeft();
                yAxisLeft.setGranularity(1f);

                LineData data = new LineData(lineDataSetSuhu, lineDataSetKelembaban);
                lineChart.setData(data);
                lineChart.animateX(500);
                lineChart.invalidate();

                DatabaseReference settings = FirebaseDatabase.getInstance().getReference().child("settings");
                settings.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        minSuhu = Double.parseDouble(dataSnapshot.child("minSuhu").getValue().toString());
                        maxSuhu = Double.parseDouble(dataSnapshot.child("maxSuhu").getValue().toString());
                        minKelembaban = Double.parseDouble(dataSnapshot.child("minKelembaban").getValue().toString());
                        maxKelembaban = Double.parseDouble(dataSnapshot.child("maxKelembaban").getValue().toString());

                        if (suhu < minSuhu) {
                            layoutAlert.setVisibility(LinearLayout.VISIBLE);
                            textAlert.setText("Suhu kurang dari batas minimum!");
                            if (kelembaban < minKelembaban) {
                                textAlert.setText("Suhu dan kelembaban kurang dari batas minimum!");
                            } else if (kelembaban > maxKelembaban) {
                                textAlert.setText("Suhu kurang dari batas minimum dan kelembaban lebih dari batas maksimum!");
                            }
                        } else if (suhu > maxSuhu) {
                            layoutAlert.setVisibility(LinearLayout.VISIBLE);
                            textAlert.setText("Suhu lebih dari batas maksimum!");
                            if (kelembaban < minKelembaban) {
                                textAlert.setText("Suhu lebih dari batas maksimum dan kelembaban kurang dari batas minimum!");
                            } else if (kelembaban > maxKelembaban) {
                                textAlert.setText("Suhu dan kelembaban lebih dari batas maksimum!");
                            }
                        } else if (kelembaban < minKelembaban) {
                            layoutAlert.setVisibility(LinearLayout.VISIBLE);
                            textAlert.setText("Kelembaban kurang dari batas minimum!");
                        } else if (kelembaban > maxKelembaban) {
                            layoutAlert.setVisibility(LinearLayout.VISIBLE);
                            textAlert.setText("Kelembaban lebih dari batas maksimum!");
                        } else {
                            layoutAlert.setVisibility(LinearLayout.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                databaseReference.child("settings/refresh").setValue(1);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1500);
            }
        });
        //

//        sensorList.add(new Sensor(id,kelembaban,suhu,update));

        // scrollview
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY();
                if (scrollY == 0) {
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });

        // set recycler
        this.recyclerView = findViewById(R.id.recyclerView);
        sensorAdapter = new SensorAdapter(sensorList, this);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.line));
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(sensorAdapter);
    }

    public void handleSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }


}
