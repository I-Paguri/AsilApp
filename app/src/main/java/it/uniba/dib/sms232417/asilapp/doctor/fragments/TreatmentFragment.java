package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.api.Distribution;
import com.touchboarder.weekdaysbuttons.WeekdaysDataItem;
import com.touchboarder.weekdaysbuttons.WeekdaysDrawableProvider;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.entity.Medication;
import it.uniba.dib.sms232417.asilapp.entity.Treatment;
import it.uniba.dib.sms232417.asilapp.utilities.MappedValues;


public class TreatmentFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_treatment, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Get the parent layout
        LinearLayout parentLayout = view.findViewById(R.id.linearLayoutCardView);

        // Convert dp to pixels
        int dp85 = (int) (85 * Resources.getSystem().getDisplayMetrics().density);
        //int dp16 = (int) (16 * Resources.getSystem().getDisplayMetrics().density);
        //int dp8 = (int) (8 * Resources.getSystem().getDisplayMetrics().density);
        //int dp2 = (int) (2 * Resources.getSystem().getDisplayMetrics().density);

        int numberOfCards = 6;
        int i;

        for (i = 0; i < numberOfCards; i++) {
            // Create a new instance of MaterialCardView
            addTreatmentCardView();
        }

        // Get the last added card view
        LinearLayout linearLayoutTreatment = (LinearLayout) parentLayout.getChildAt(i - 1);

        // Get the current layout parameters of the card view
        LinearLayout.LayoutParams linearLayoutTreatmentLayoutParams = (LinearLayout.LayoutParams) linearLayoutTreatment.getLayoutParams();

        linearLayoutTreatmentLayoutParams.setMargins(0, 0, 0, dp85);
        // Apply the new layout parameters to the card view
        linearLayoutTreatment.setLayoutParams(linearLayoutTreatmentLayoutParams);

        final ExtendedFloatingActionButton fab = view.findViewById(R.id.fab);

        // register the nestedScrollView from the main layout
        NestedScrollView nestedScrollView = view.findViewById(R.id.nestedScrollView);

        // handle the nestedScrollView behaviour with OnScrollChangeListener
        // to extend or shrink the Extended Floating Action Button
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // the delay of the extension of the FAB is set for 12 items
                if (scrollY > oldScrollY + 12 && fab.isExtended()) {
                    fab.shrink();
                }

                // the delay of the extension of the FAB is set for 12 items
                if (scrollY < oldScrollY - 12 && !fab.isExtended()) {
                    fab.extend();
                }

                // if the nestedScrollView is at the first item of the list then the
                // extended floating action should be in extended state
                if (scrollY == 0) {
                    fab.extend();
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreatmentFormGeneralFragment treatmentFormGeneralFragment = new TreatmentFormGeneralFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, treatmentFormGeneralFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    protected void addTreatmentCardView() {
        String treatmentTarget, medicationName, notes;
        Integer howToTake, howRegularly;
        Date startDate, endDate;

        treatmentTarget = "Abbassare la febbricola";
        startDate = new Date();
        endDate = new Date();
        notes = "Prendere con molta acqua";

        ArrayList<WeekdaysDataItem> selectedWeekdays;

        medicationName = "Paracetamolo";
        howToTake = 0;
        howRegularly = 1;
        selectedWeekdays = new ArrayList<>();
        // Create a WeekdaysDataItem object for Monday
        // Define the parameters
        int position = 2; // Position of the weekday (e.g., 2 for Monday if Sunday is 1)
        int calendarId = Calendar.MONDAY; // Calendar ID of the weekday
        String label = "Monday"; // Label of the weekday
        Drawable drawable = getResources().getDrawable(R.drawable.healthcare); // Drawable for the weekday
        int textDrawableType = WeekdaysDrawableProvider.MW_RECT; // Type of the drawable (e.g., DAY, NIGHT)
        int numberOfLetters = 3; // Number of letters to display (e.g., 3 for "Mon")
        boolean selected = true; // Whether the weekday is selected

        // Instantiate the WeekdaysDataItem object
        WeekdaysDataItem monday = new WeekdaysDataItem(position, calendarId, label, drawable, textDrawableType, numberOfLetters, selected);
        // Add Monday to the selectedWeekdays list
        selectedWeekdays.add(monday);

        Medication medication1 = new Medication(medicationName, howToTake, howRegularly, selectedWeekdays);
        Medication medication2 = new Medication(medicationName, howToTake, howRegularly, selectedWeekdays);

        // every 2 weeks
        medication1.setIntervalSelectedType(1);
        medication1.setIntervalSelectedNumber(2);

        ArrayList<String> intakesTime = new ArrayList<>();
        intakesTime.add("08:00");
        intakesTime.add("12:00");
        medication1.setIntakesTime(intakesTime);
        medication2.setIntakesTime(intakesTime);

        ArrayList<String> quantities = new ArrayList<>();
        quantities.add("1/4");
        quantities.add("3");
        medication1.setQuantities(quantities);
        medication2.setQuantities(quantities);

        Treatment treatment = new Treatment(treatmentTarget, startDate, null);

        treatment.addMedication(medication1);
        treatment.addMedication(medication2);

        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams")
        View treatmentLayout = inflater.inflate(R.layout.treatment_layout, null);

        // Get the parent layout
        LinearLayout parentLayout = requireView().findViewById(R.id.linearLayoutCardView);


        // TREATMENT TARGET
        TextView treatmentTargetText = treatmentLayout.findViewById(R.id.treatmentTarget);
        treatmentTargetText.setText(treatmentTarget);

        // DATE
        TextView dateText = treatmentLayout.findViewById(R.id.dateText);
        if (treatment.getEndDateString().equals("")) {
            dateText.setText(treatment.getStartDateString() + " - " + getResources().getString(R.string.ongoing));
        } else {
            dateText.setText(treatment.getStartDateString() + " - " + treatment.getEndDateString());
        }

        // MEDICATIONS
        LinearLayout medicationsLayout = treatmentLayout.findViewById(R.id.linearLayoutMedications);
        medicationsLayout.addView(getMedicationLayout(medication1));
        medicationsLayout.addView(getMedicationLayout(medication2));

        parentLayout.addView(treatmentLayout);

        // NOTES
        if (notes == null || notes.equals("")) {
            LinearLayout linearLayoutNotes = treatmentLayout.findViewById(R.id.linearLayoutNotes);
            linearLayoutNotes.setVisibility(View.GONE);
        } else {
            TextView notesText = treatmentLayout.findViewById(R.id.notes);
            notesText.setText(notes);
        }

    }

    protected View getMedicationLayout(Medication medication) {
        MappedValues mappedValues = new MappedValues(requireContext());
        String medicationName, howToTake, howRegularly;
        ArrayList<WeekdaysDataItem> selectedWeekdays = null;


        medicationName = medication.getMedicationName();
        howToTake = medication.toStringHowToTake(requireContext());
        howRegularly = medication.toStringHowRegularly(requireContext());

        ArrayList<String> intakeTimes = new ArrayList<>();
        intakeTimes = medication.getIntakesTime();

        ArrayList<String> quantities = new ArrayList<>();
        quantities = medication.getQuantities();

        if (medication.getSelectedWeekdays() != null) {
            selectedWeekdays = medication.getSelectedWeekdays();
        }

        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View medicationLayout = inflater.inflate(R.layout.medication_layout, null);

        // MEDICATION NAME
        TextView medicationNameText = medicationLayout.findViewById(R.id.medicationName);
        medicationNameText.setText("\u2022 "+medicationName);

        // HOW REGULARLY
        TextView howRegularlyText = medicationLayout.findViewById(R.id.howRegularly);

        if (medication.getHowRegularly() == 0) {
            // Daily
            howRegularlyText.setText(howRegularly);
        } else {
            if (medication.getHowRegularly() == 1) {
                // Weekdays
                String selectedWeekdaysString;
                selectedWeekdaysString = "";

                for (WeekdaysDataItem selectedWeekday : medication.getSelectedWeekdays()) {
                    selectedWeekdaysString = selectedWeekdaysString + selectedWeekday.getLabel();
                    if (medication.getSelectedWeekdays().indexOf(selectedWeekday) != medication.getSelectedWeekdays().size() - 1) {
                        selectedWeekdaysString = selectedWeekdaysString + ", ";
                    } else {
                        if (medication.getSelectedWeekdays().size() == 1) {
                            selectedWeekdaysString = selectedWeekdaysString + " ";
                        } else {
                            selectedWeekdaysString = selectedWeekdaysString + getResources().getString(R.string.and);
                        }
                    }
                }

                howRegularlyText.setText(selectedWeekdaysString);
            } else {
                // Interval
                howRegularlyText.setText(mappedValues.getFormattedInterval(medication.getIntervalSelectedType(), medication.getIntervalSelectedNumber()));
            }
        }

        // INTAKES
        TextView intakesText = medicationLayout.findViewById(R.id.intakes);
        // Quantity How to take at time
        int size = intakeTimes.size();
        int i;
        int quantityNumber;

        String quantity;
        String intakeTime;
        String intakesString;
        intakesString = "";

        for (i = 0; i < size; i++) {
            quantity = quantities.get(i);
            intakeTime = intakeTimes.get(i);

            if (quantity.equals("1/4") || quantity.equals("1/2") || quantity.equals("3/4")) {
                quantityNumber = 1; // Not plural
            } else {
                quantityNumber = 2; // Plural
            }

            intakesString = intakesString + quantity + " " + (mappedValues.getFormattedHowToTake(mappedValues.getHowToTakeKey(howToTake), quantityNumber)).toLowerCase() + " " + requireContext().getResources().getString(R.string.at_time) + " " + intakeTime;

            if (i != size - 1) {
                intakesString = intakesString + "\n";
            }
        }

        intakesText.setText(intakesString);

        return medicationLayout;
    }


}
