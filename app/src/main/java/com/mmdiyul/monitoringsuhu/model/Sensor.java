package com.mmdiyul.monitoringsuhu.model;

public class Sensor {
    private float kelembaban;
    private float suhu;
    private String update;

    public Sensor(float kelembaban, float suhu, String update) {
        this.kelembaban = kelembaban;
        this.suhu = suhu;
        this.update = update;
    }

    public float getKelembaban() {
        return kelembaban;
    }

    public void setKelembaban(float kelembaban) {
        this.kelembaban = kelembaban;
    }

    public float getSuhu() {
        return suhu;
    }

    public void setSuhu(float suhu) {
        this.suhu = suhu;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }
}
