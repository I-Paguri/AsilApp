package it.uniba.dib.sms232417.asilapp.entity;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Treatment implements Parcelable {
    private String treatmentTarget;
    private Date startDate, endDate;
    private ArrayList<Medication> medications;
    private String notes;

    public Treatment() {

    }

    public Treatment(String treatmentTarget, Date startDate, Date endDate) {
        this.treatmentTarget = treatmentTarget;
        this.startDate = startDate;
        this.endDate = endDate;

        // Default values
        this.medications = new ArrayList<>();
        this.notes = "";
    }

    protected Treatment(Parcel in) {
        treatmentTarget = in.readString();
    }

    public static final Creator<Treatment> CREATOR = new Creator<Treatment>() {
        @Override
        public Treatment createFromParcel(Parcel in) {
            return new Treatment(in);
        }

        @Override
        public Treatment[] newArray(int size) {
            return new Treatment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(treatmentTarget);
        dest.writeString(getStartDateString());
        dest.writeString(getEndDateString());
    }

    public String getTreatmentTarget() {
        return treatmentTarget;
    }

    public void setTreatmentTarget(String treatmentTarget) {
        this.treatmentTarget = treatmentTarget;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getStartDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy",Locale.getDefault());
        return dateFormat.format(startDate);
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getEndDateString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy",Locale.getDefault());
        String endDateString;
        endDateString = "";

        if (endDate != null) {
            endDateString = dateFormat.format(endDate);
        }

        return endDateString;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }


    public ArrayList<Medication> getMedications() {
        return medications;
    }

    public String getMedicationsString(Context context) {
        String medicationsString = "";

        for (Medication medication : medications) {
            medicationsString = medicationsString + medication.toString(context);
        }

        return medicationsString;
    }


    public void setMedications(ArrayList<Medication> medications) {
        this.medications = medications;
    }

    public void addMedication(Medication medication) {
        this.medications.add(medication);
    }

    public void removeMedicationAtIndex(int index) {
        this.medications.remove(index);
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        String treatmentString;

        treatmentString = "Target: " + getTreatmentTarget() + "\n";
        treatmentString = treatmentString + "Start date: " + getStartDateString() + "\n";
        if (!getEndDateString().isEmpty()) {
            treatmentString = treatmentString + "End date: " + getEndDateString() + "\n";
        }

        Log.d("ciao", medications.toString());

        treatmentString = treatmentString + "Medications: " + medications.toString() + "\n";


        if (!getNotes().isEmpty()) {
            treatmentString = treatmentString + "Notes: " + getNotes() + "\n";
        }

        return treatmentString;
    }


}
