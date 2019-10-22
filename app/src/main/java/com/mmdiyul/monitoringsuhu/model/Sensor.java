package com.mmdiyul.monitoringsuhu.model;

public class Sensor {
    private int id;
    private double suhu;
    private double kelembaban;
    private String update;

    public Sensor(int id, double suhu, double kelembaban, String update) {
        this.id = id;
        this.suhu = suhu;
        this.kelembaban = kelembaban;
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
