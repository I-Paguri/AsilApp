package it.uniba.dib.sms232417.asilapp.utilities.vitals;

import java.text.SimpleDateFormat;

public class Glycemia {
    private double glycemia;
    private String date;

    public Glycemia(double glycemia) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.date = sdf.format(System.currentTimeMillis());
        this.glycemia = glycemia;
    }

    public Glycemia() {
    }

    public double getGlycemia() {
        return glycemia;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setGlycemia(int glycemia) {
        this.glycemia = glycemia;
    }
}
