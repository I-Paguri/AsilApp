package it.uniba.dib.sms232417.asilapp.utilities.vitals;

import java.text.SimpleDateFormat;

public class BloodPressure {
    private int systolic;
    private int diastolic;
    private String date;
    public BloodPressure(int systolic, int diastolic) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.date = sdf.format(new java.util.Date());
        this.systolic = systolic;
        this.diastolic = diastolic;
    }

    public BloodPressure() {
    }

    public int getSystolic() {
        return systolic;
    }

    public void setSystolic(int systolic) {
        this.systolic = systolic;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(int diastolic) {
        this.diastolic = diastolic;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
