package it.uniba.dib.sms232417.asilapp.utilities.vitals;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Temperature {
    private double value;
    private String date;
    public Temperature(double value) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.date = sdf.format(new Date());
        this.value = value;

    }


    public double getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
