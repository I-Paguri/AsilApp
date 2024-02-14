package it.uniba.dib.sms232417.asilapp.entity;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
import com.touchboarder.weekdaysbuttons.WeekdaysDataItem;

import java.io.Serializable;
import java.util.ArrayList;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.utilities.MappedValues;

public class Medication implements Parcelable, Serializable {
    private String medicationName;
    private Integer howToTake;
    private Integer howRegularly;
    private Integer intervalSelectedType; // day, week, month
    private Integer intervalSelectedNumber;
    @Exclude
    private ArrayList<WeekdaysDataItem> selectedWeekdays;
    private String selectedWeekdaysString;
    private ArrayList<String> intakesTime;
    private ArrayList<String> quantities;

    private boolean isBought;

    private int id;

    private static int idContatore = 0; // make this static

    public Medication() {

    }

    public Medication(String medicationName, Integer howToTake, Integer howRegularly, ArrayList<WeekdaysDataItem> selectedWeekdays) {
        this.medicationName = medicationName;
        this.howToTake = howToTake;
        this.howRegularly = howRegularly;
        this.selectedWeekdays = selectedWeekdays;
        this.selectedWeekdaysString = convertWeekdaysToString();
        this.isBought = false;
        this.id = idContatore++;


        // Default values
        this.intervalSelectedType = -1;
        this.intakesTime = new ArrayList<>();
        this.quantities = new ArrayList<>();
    }

    protected Medication(Parcel in) {
        medicationName = in.readString();
        howToTake = in.readInt();
        howRegularly = in.readInt();
        intervalSelectedNumber = in.readInt();
        intervalSelectedType = in.readInt();
        selectedWeekdays = in.createTypedArrayList(WeekdaysDataItem.CREATOR);
        selectedWeekdaysString = in.readString();
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
        dest.writeInt(howToTake);
        dest.writeInt(howRegularly);
        dest.writeInt(intervalSelectedNumber);
        dest.writeInt(intervalSelectedType);
        dest.writeTypedList(selectedWeekdays);
        dest.writeString(selectedWeekdaysString);
        dest.writeStringList(intakesTime);
        dest.writeStringList(quantities);
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public Integer getHowToTake() {
        return howToTake;
    }

    public String toStringHowToTake(Context context) {
        MappedValues mappedValues = new MappedValues(context);
        return mappedValues.getHowToTake(getHowToTake());
    }

    public void setHowToTake(Integer howToTake) {
        this.howToTake = howToTake;
    }

    public Integer getHowRegularly() {
        return howRegularly;
    }

    public String toStringHowRegularly(Context context) {
        MappedValues mappedValues = new MappedValues(context);
        return mappedValues.getHowRegularly(getHowRegularly());
    }

    public void setHowRegularly(Integer howRegularly) {
        this.howRegularly = howRegularly;
    }

    //get e set
    public boolean getIsBought() {
        return isBought;
    }

    public void setIsBought(boolean bought) {
        isBought = bought;
    }

    public int getId() {
        return id;
    }

    @Exclude

    public ArrayList<WeekdaysDataItem> getSelectedWeekdays() {
        return selectedWeekdays;
    }

    public void addSelectedWeekday(WeekdaysDataItem selectedWeekday) {
        this.selectedWeekdays.add(selectedWeekday);
        this.selectedWeekdaysString = convertWeekdaysToString();
    }

    @Exclude
    public void setSelectedWeekdays(ArrayList<WeekdaysDataItem> selectedWeekdays) {
        this.selectedWeekdays = selectedWeekdays;
        this.selectedWeekdaysString = convertWeekdaysToString();
    }


    public String convertWeekdaysToString() {
        String selectedWeekdaysString;
        selectedWeekdaysString = "";

        if (selectedWeekdays != null && !selectedWeekdays.isEmpty()) {
            for (WeekdaysDataItem selectedWeekday : selectedWeekdays) {
                selectedWeekdaysString = selectedWeekdaysString + selectedWeekday.getLabel();
                if (selectedWeekdays.indexOf(selectedWeekday) != selectedWeekdays.size() - 1) {
                    selectedWeekdaysString = selectedWeekdaysString + ", ";
                }
            }
        }


        return selectedWeekdaysString;
    }

    public String getSelectedWeekdaysString() {
        return selectedWeekdaysString;
    }

    public void setSelectedWeekdaysString(String selectedWeekdaysString) {
        this.selectedWeekdaysString = selectedWeekdaysString;
    }

    public Integer getIntervalSelectedType() {
        return intervalSelectedType;
    }

    public void setIntervalSelectedType(Integer intervalSelectedType) {
        this.intervalSelectedType = intervalSelectedType;
    }

    public Integer getIntervalSelectedNumber() {
        return intervalSelectedNumber;
    }

    public void setIntervalSelectedNumber(Integer intervalSelectedNumber) {
        this.intervalSelectedNumber = intervalSelectedNumber;
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

    public String toStringInterval(Context context) {
        String intervalString;
        MappedValues mappedValues = new MappedValues(context);

        intervalString = "";

        if (getIntervalSelectedType() != -1 && getIntervalSelectedNumber() != -1) {
            intervalString = context.getResources().getString(R.string.every) + " " + getIntervalSelectedNumber() + " " + mappedValues.getInterval(getIntervalSelectedType());
        }

        return intervalString;
    }

    @NonNull
    public String toString() {
        String medicationString;

        medicationString = "Medication: " + getMedicationName() + "\n";
        medicationString = medicationString + "How to take: " + getHowToTake() + "\n";
        medicationString = medicationString + "How regularly: " + getHowRegularly() + "\n";

        if (getIntervalSelectedType() != -1) {
            medicationString = medicationString + "Interval selection: [" + getIntervalSelectedNumber() + ", " + getIntervalSelectedType() + "]\n";
        }

        if (!selectedWeekdaysString.isEmpty()) {
            medicationString = medicationString + "Selected weekdays: " + selectedWeekdaysString + "\n";
        }

        medicationString = medicationString + "Intakes time: [" + String.join(", ", getIntakesTime()) + "]\n";
        medicationString = medicationString + "Quantities: [" + String.join(", ", getQuantities()) + "]\n";

        return medicationString;
    }

    @NonNull
    public String toString(Context context) {
        String medicationString;
        MappedValues mappedValues = new MappedValues(context);

        medicationString = "Medication: " + getMedicationName() + "\n";
        medicationString = medicationString + "How to take: " + mappedValues.getHowToTake(getHowToTake()) + "\n";
        medicationString = medicationString + "How regularly: " + mappedValues.getHowRegularly(getHowRegularly()) + "\n";

        if (getIntervalSelectedType() != -1) {
            //medicationString = medicationString + "Interval selection: " + getIntervalSelectedType() + "\n";
            String formattedSelectedType;

            if (mappedValues.getInterval(intervalSelectedType).equals(context.getResources().getString(R.string.day))) {
                // days
                formattedSelectedType = context.getResources().getQuantityString(R.plurals.days, intervalSelectedNumber, intervalSelectedNumber);
            } else {
                if (mappedValues.getInterval(intervalSelectedType).equals(context.getResources().getString(R.string.week))) {
                    // weeks
                    formattedSelectedType = context.getResources().getQuantityString(R.plurals.weeks, intervalSelectedNumber, intervalSelectedNumber);
                } else {
                    // months
                    formattedSelectedType = context.getResources().getQuantityString(R.plurals.months, intervalSelectedNumber, intervalSelectedNumber);
                }
            }

            medicationString = medicationString + "Interval selected: " + context.getResources().getString(R.string.every) + " " + intervalSelectedNumber + " " + formattedSelectedType + "\n";
        }

        if (!selectedWeekdaysString.isEmpty()) {
            medicationString = medicationString + "Selected weekdays: " + selectedWeekdaysString + "\n";
        }

        medicationString = medicationString + "Intakes time: [" + String.join(", ", getIntakesTime()) + "]\n";
        medicationString = medicationString + "Quantities: [" + String.join(", ", getQuantities()) + "]\n";

        return medicationString;
    }
}
