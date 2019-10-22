package com.mmdiyul.monitoringsuhu.model;

public class Sensor {
    private int id;
    private double kelembaban;
    private double suhu;
    private String update;

    public Sensor(int id, double kelembaban, double suhu, String update) {
        this.id = id;
        this.kelembaban = kelembaban;
        this.suhu = suhu;
        this.update = update;
    }

    public int getId() {
        return id;
    }

    public double getKelembaban() {
        return kelembaban;
    }

    public double getSuhu() {
        return suhu;
    }

    public String getUpdate() {
        return update;
    }
}
