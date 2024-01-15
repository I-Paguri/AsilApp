package it.uniba.dib.sms232417.asilapp.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.touchboarder.weekdaysbuttons.WeekdaysDataItem;

import java.util.ArrayList;

public class Medication implements Parcelable {
    private String medicationName;
    private String howToTake;
    private String howRegularly;
    private String intervalSelected;
    private ArrayList<WeekdaysDataItem> selectedWeekdays;
    private ArrayList<String> intakesTime;
    private ArrayList<String> quantities;

    public Medication(String medicationName, String howToTake, String howRegularly, ArrayList<WeekdaysDataItem> selectedWeekdays) {
        this.medicationName = medicationName;
        this.howToTake = howToTake;
        this.howRegularly = howRegularly;
        this.selectedWeekdays = selectedWeekdays;

        // Default values
        this.intervalSelected = "";
        this.intakesTime = new ArrayList<>();
        this.quantities = new ArrayList<>();
    }

    protected Medication(Parcel in) {
        medicationName = in.readString();
        howToTake = in.readString();
        howRegularly = in.readString();
        intervalSelected = in.readString();
        selectedWeekdays = in.createTypedArrayList(WeekdaysDataItem.CREATOR);
        intakesTime = in.createStringArrayList();
        quantities = in.createStringArrayList();
    }

    public static final Creator<Medication> CREATOR = new Creator<Medication>() {
        @Override
        public Medication createFromParcel(Parcel in) {
            return new Medication(in);
        }

        @Override
        public Medication[] newArray(int size) {
            return new Medication[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(medicationName);
        dest.writeString(howToTake);
        dest.writeString(howRegularly);
        dest.writeString(intervalSelected);
        dest.writeTypedList(selectedWeekdays);
        dest.writeStringList(intakesTime);
        dest.writeStringList(quantities);
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public String getHowToTake() {
        return howToTake;
    }

    public void setHowToTake(String howToTake) {
        this.howToTake = howToTake;
    }

    public String getHowRegularly() {
        return howRegularly;
    }

    public void setHowRegularly(String howRegularly) {
        this.howRegularly = howRegularly;
    }


    public ArrayList<WeekdaysDataItem> getSelectedWeekdays() {
        return selectedWeekdays;
    }

    public void addSelectedWeekday(WeekdaysDataItem selectedWeekday) {
        this.selectedWeekdays.add(selectedWeekday);
    }

    public void setSelectedWeekdays(ArrayList<WeekdaysDataItem> selectedWeekdays) {
        this.selectedWeekdays = selectedWeekdays;
    }

    public String getSelectedWeekdaysString() {
        String selectedWeekdaysString;
        selectedWeekdaysString = "";

        for (WeekdaysDataItem selectedWeekday : getSelectedWeekdays()) {
            selectedWeekdaysString = selectedWeekdaysString + selectedWeekday.getLabel();
            if (getSelectedWeekdays().indexOf(selectedWeekday) != getSelectedWeekdays().size() - 1) {
                selectedWeekdaysString = selectedWeekdaysString + ", ";
            }
        }

        return selectedWeekdaysString;
    }

    public String getIntervalSelected() {
        return intervalSelected;
    }

    public void setIntervalSelected(String intervalSelected) {
        this.intervalSelected = intervalSelected;
    }

    public ArrayList<String> getIntakesTime() {
        return intakesTime;
    }

    public void setIntakesTime(ArrayList<String> intakesTime) {
        this.intakesTime = intakesTime;
    }

    public void addIntakeTime(String intakeTime) {
        this.intakesTime.add(intakeTime);
    }

    public ArrayList<String> getQuantities() {
        return quantities;
    }

    public void setQuantities(ArrayList<String> quantities) {
        this.quantities = quantities;
    }

    public void addQuantity(String quantity) {
        this.quantities.add(quantity);
    }

    @Override
    public String toString() {
        String medicationString;

        medicationString = "Medication: " + getMedicationName() + "\n";
        medicationString = medicationString + "How to take: " + getHowToTake() + "\n";
        medicationString = medicationString + "How regularly: " + getHowRegularly() + "\n";

        if (!getIntervalSelected().isEmpty()) {
            medicationString = medicationString + "Interval selection: " + getIntervalSelected() + "\n";
        }

        if (!getSelectedWeekdays().isEmpty()) {
            medicationString = medicationString + "Selected weekdays: " + getSelectedWeekdaysString() + "\n";
        }

        medicationString = medicationString + "Intakes time: [" + String.join(", ", getIntakesTime()) + "]\n";
        medicationString = medicationString + "Quantities: [" + String.join(", ", getQuantities()) + "]\n";

        return medicationString;
    }
}
