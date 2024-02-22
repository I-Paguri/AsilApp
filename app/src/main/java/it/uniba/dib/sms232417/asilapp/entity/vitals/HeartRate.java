package it.uniba.dib.sms232417.asilapp.entity.vitals;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HeartRate {
    private int value;
    private String date;
    public HeartRate(int value) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.date = sdf.format(new Date());
        this.value = value;

    }
    //Per recupero dal DB
    public HeartRate(int value, String date) {
        this.value = value;
        this.date = date;
    }

    public HeartRate() {
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "HeartRate{" +
                "value=" + value +
                ", date='" + date + '\'' +
                '}';
    }
}