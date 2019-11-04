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

    private int id;
    private double suhu;
    private double kelembaban;
    private String update;
    private double minSuhu, maxSuhu, minKelembaban, maxKelembaban;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ScrollView scrollView;
    private RecyclerView recyclerView;

    private List<Sensor> sensorList = new ArrayList<>();
    private SensorAdapter sensorAdapter;

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

                        sensorList.add(new Sensor(id, suhu, kelembaban, update));
                        index++;
                        lastChild--;
                    } catch (NullPointerException nullPointer) {
                        Log.d("Error: ", nullPointer.getMessage());
                    }
                }


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

        //chart
        lineChart = (LineChart)findViewById(R.id.chart);
        LineDataSet lineDataSet = new LineDataSet(getData(), "Inducesmile");
        lineDataSet.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        lineDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        final String[] months = new String[]{"Jan", "Feb", "Mar", "Apr"};
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return months[(int) value];
            }
        };
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);

        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setEnabled(false);

        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setGranularity(1f);

        LineData data = new LineData(lineDataSet);
        lineChart.setData(data);
        lineChart.animateX(2500);
        lineChart.invalidate();

    }
        private ArrayList getData(){
            ArrayList<Entry> entries = new ArrayList<>();
            entries.add(new Entry(0f, 4f));
            entries.add(new Entry(1f, 1f));
            entries.add(new Entry(2f, 2f));
            entries.add(new Entry(3f, 4f));
            return entries;
        }

    public void handleSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        finish();
    }


}
