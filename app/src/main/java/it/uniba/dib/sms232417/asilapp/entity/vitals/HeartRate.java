package it.uniba.dib.sms232417.asilapp.entity.vitals;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HeartRate implements Parcelable {
    private int value;
    private Date date;
    public HeartRate(int value) {
       this.date = new Date();
       this.value = value;

    }
    //Per recupero dal DB

    public HeartRate() {
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "HeartRate{" +
                "value=" + value +
                ", date='" + date + '\'' +
                '}';
    }
    public String getStringDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy",Locale.getDefault());
        return dateFormat.format(this.date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(value);
        dest.writeLong(date.getTime());
    }
}
