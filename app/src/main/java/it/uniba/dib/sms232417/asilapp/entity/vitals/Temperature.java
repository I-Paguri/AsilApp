package it.uniba.dib.sms232417.asilapp.entity.vitals;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Temperature {
    private double value;
    private Date date;
    public Temperature(double value) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.date = new Date();
        this.value = value;

    }

    public Temperature() {
    }


    public double getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public String getStringDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        return dateFormat.format(this.date);
    }
}
