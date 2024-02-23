package it.uniba.dib.sms232417.asilapp.entity.vitals;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Temperature implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeDouble(value);
        dest.writeLong(date.getTime());
    }
}
