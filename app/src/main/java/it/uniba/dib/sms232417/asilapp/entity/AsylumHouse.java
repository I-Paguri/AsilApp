package it.uniba.dib.sms232417.asilapp.entity;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import com.google.firebase.firestore.GeoPoint;
import java.io.Serializable;
import java.util.ArrayList;

public class AsylumHouse implements Parcelable, Serializable {
    private String UUID;
    private String name;
    private GeoPoint coordinates;
    private String address;
    private ArrayList<String> rules;
    private int numberOfReviews;
    private double ratingAverage;

    public AsylumHouse() {
    }

    public AsylumHouse(String UUID, String name, GeoPoint coordinates, String address, ArrayList<String> rules, int numberOfReviews, double ratingAverage) {
        this.UUID = UUID;
        this.name = name;
        this.coordinates = coordinates;
        this.address = address;
        this.rules = rules;
        this.numberOfReviews = numberOfReviews;
        this.ratingAverage = ratingAverage;
    }

    public AsylumHouse(String UUID, String name, GeoPoint coordinates, String address) {
        this.UUID = UUID;
        this.name = name;
        this.coordinates = coordinates;
        this.address = address;
        this.rules = new ArrayList<>();
        this.numberOfReviews = 0;
        this.ratingAverage = 0;
    }

    protected AsylumHouse(Parcel in) {
        UUID = in.readString();
        name = in.readString();
        double lat = in.readDouble();
        double lon = in.readDouble();
        coordinates = new GeoPoint(lat, lon);
        address = in.readString();
        rules = in.createStringArrayList();
        numberOfReviews = in.readInt();
        ratingAverage = in.readDouble();
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
        double lat = coordinates.getLatitude();
        double lon = coordinates.getLongitude();
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeString(address);
        dest.writeStringList(rules);
        dest.writeInt(numberOfReviews);
        dest.writeDouble(ratingAverage);
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

    public GeoPoint getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(GeoPoint coordinates) {
        this.coordinates = coordinates;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<String> getRules() {
        return rules;
    }

    public void setRules(ArrayList<String> rules) {
        this.rules = rules;
    }

    public void addRule(String rule) {
        this.rules.add(rule);
    }

    public String getRulesToString() {
        StringBuilder rulesString = new StringBuilder();
        for (String rule : rules) {
            rulesString.append(rule).append("\n");
        }
        return rulesString.toString();
    }

    public void removeRule(String rule) {
        this.rules.remove(rule);
    }

    public int getNumberOfReviews() {
        return numberOfReviews;
    }

    public void setNumberOfReviews(int numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }

    public double getRatingAverage() {
        return ratingAverage;
    }

    public void setRatingAverage(double ratingAverage) {
        this.ratingAverage = ratingAverage;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder asylumHouse = new StringBuilder();

        asylumHouse.append("");
        asylumHouse.append("UUID: ").append(UUID).append("\n");
        asylumHouse.append("Name: ").append(name).append("\n");
        asylumHouse.append("Coordinates: ").append(coordinates).append("\n");
        asylumHouse.append("Address: ").append(address).append("\n");
        asylumHouse.append("Rules: ").append(getRulesToString());
        asylumHouse.append("Number of reviews: ").append(numberOfReviews).append("\n");
        asylumHouse.append("Rating average: ").append(ratingAverage).append("\n");

        return asylumHouse.toString();
    }
}
