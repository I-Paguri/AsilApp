package it.uniba.dib.sms232417.asilapp.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class AsylumHouse implements Parcelable, Serializable {
    private String UUID;
    private String name;
    private String coordinates;
    private String address;
    private String rules;
    private int numberOfReviews;
    private float ratingAverage;

    public AsylumHouse() {
    }

    public AsylumHouse(String UUID, String name, String coordinates, String address, String rules, int numberOfReviews, float ratingAverage) {
        this.UUID = UUID;
        this.name = name;
        this.coordinates = coordinates;
        this.address = address;
        this.rules = rules;
        this.numberOfReviews = numberOfReviews;
        this.ratingAverage = ratingAverage;
    }

    protected AsylumHouse(Parcel in) {
        UUID = in.readString();
        name = in.readString();
        coordinates = in.readString();
        address = in.readString();
        rules = in.readString();
        numberOfReviews = in.readInt();
        ratingAverage = in.readFloat();
    }

    public static final Creator<AsylumHouse> CREATOR = new Creator<AsylumHouse>() {
        @Override
        public AsylumHouse createFromParcel(Parcel in) {
            return new AsylumHouse(in);
        }

        @Override
        public AsylumHouse[] newArray(int size) {
            return new AsylumHouse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(UUID);
        dest.writeString(name);
        dest.writeString(coordinates);
        dest.writeString(address);
        dest.writeString(rules);
        dest.writeInt(numberOfReviews);
        dest.writeFloat(ratingAverage);
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public int getNumberOfReviews() {
        return numberOfReviews;
    }

    public void setNumberOfReviews(int numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }

    public float getRatingAverage() {
        return ratingAverage;
    }

    public void setRatingAverage(float ratingAverage) {
        this.ratingAverage = ratingAverage;
    }
}
