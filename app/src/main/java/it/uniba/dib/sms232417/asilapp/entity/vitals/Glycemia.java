package it.uniba.dib.sms232417.asilapp.entity.vitals;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Glycemia implements Parcelable {
    private double glycemia;
    private Date date;

    public Glycemia(double glycemia) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        this.date = new Date();
        this.glycemia = glycemia;
    }

    public Glycemia() {
    }

    public double getGlycemia() {
        return glycemia;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setGlycemia(int glycemia) {
        this.glycemia = glycemia;
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
        dest.writeDouble(glycemia);
        dest.writeLong(date.getTime());
    }
}
