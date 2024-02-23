package it.uniba.dib.sms232417.asilapp.entity.vitals;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BloodPressure {
    private int systolic;
    private int diastolic;
    private Date date;
    public BloodPressure(int systolic, int diastolic) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.date = new Date();
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
