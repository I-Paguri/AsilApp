package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.transition.MaterialContainerTransform;
import com.touchboarder.weekdaysbuttons.WeekdaysDataItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterPatient;
import it.uniba.dib.sms232417.asilapp.entity.Medication;
import it.uniba.dib.sms232417.asilapp.entity.Treatment;
import it.uniba.dib.sms232417.asilapp.interfaces.OnTreatmentsCallback;
import it.uniba.dib.sms232417.asilapp.utilities.MappedValues;


public class TreatmentFragment extends Fragment {

    private String patientUUID;
    private String patientName;
    private String patientAge;
    private String user;
    private ExtendedFloatingActionButton fab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_treatment, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        patientUUID = "";
        patientName = "";
        patientAge = "";
        user = ""; // Type of user: "patient" or "doctor"

        // Create an instance of DatabaseAdapterPatient
        DatabaseAdapterPatient adapter = new DatabaseAdapterPatient(requireContext());

        if (this.getArguments() != null) {
            patientUUID = this.getArguments().getString("patientUUID");
            patientName = this.getArguments().getString("patientName");
            patientAge = this.getArguments().getString("patientAge");
            user = this.getArguments().getString("user");
            if (user == null) {
                user = "";
            }
        }

        adapter.getTreatments(patientUUID, new OnTreatmentsCallback() {
            @Override
            public void onCallback(Map<String, Treatment> treatments) {

                if (treatments == null || treatments.isEmpty()) {
                    LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View noTreatmentLayout = inflater.inflate(R.layout.no_treatments_found_layout, null);
                    // Add the inflated layout to the parent layout
                    LinearLayout parentLayout = view.findViewById(R.id.linearLayoutCardView);
                    parentLayout.addView(noTreatmentLayout);
                } else {
                    Iterator<Map.Entry<String, Treatment>> iterator = treatments.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, Treatment> entry = iterator.next();
                        String treatmentId = entry.getKey();
                        Treatment treatment = entry.getValue();

                        // Add the treatment to the parent layout
                        // Check if it is the last treatment
                        // if it is last treatment then a bottom margin of 85dp is added to the last treatment layout
                        addTreatmentCardView(treatmentId, treatment, !iterator.hasNext());
                    }
                }
            }

            @Override
            public void onCallbackFailed(Exception e) {
                Log.e("Error", "Failed to get treatments", e);
                LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View noTreatmentLayout = inflater.inflate(R.layout.no_treatments_found_layout, null);
                // Add the inflated layout to the parent layout
                LinearLayout parentLayout = view.findViewById(R.id.linearLayoutCardView);
                parentLayout.addView(noTreatmentLayout);
            }
        });

        fab = view.findViewById(R.id.fab);

        if ("patient".equals(user)) {
            fab.setVisibility(View.GONE);
        } else {

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
                    // Set the transition name for the FAB
                    fab.setTransitionName("shared_element_container");

                    // Create an instance of MaterialContainerTransform
                    MaterialContainerTransform transform = new MaterialContainerTransform();
                    transform.setDuration(600);
                    transform.setScrimColor(Color.TRANSPARENT);
                    transform.setAllContainerColors(requireContext().getResources().getColor(R.color.md_theme_light_surface));

                    // Set the shared element enter transition for the fragment
                    TreatmentFormGeneralFragment treatmentFormGeneralFragment = new TreatmentFormGeneralFragment();
                    treatmentFormGeneralFragment.setSharedElementEnterTransition(transform);

                    // Create a bundle and put patientUUID, patientName, and patientAge into it
                    Bundle bundle = new Bundle();
                    bundle.putString("patientUUID", patientUUID);
                    bundle.putString("patientName", patientName);
                    bundle.putString("patientAge", patientAge);

                    // Set the bundle as arguments to the fragment
                    treatmentFormGeneralFragment.setArguments(bundle);
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();

                    // Add the shared element to the transaction
                    transaction.addSharedElement(fab, fab.getTransitionName());

                    transaction.replace(R.id.nav_host_fragment_activity_main, treatmentFormGeneralFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }
    }


    protected void addTreatmentCardView(String treatmentId, Treatment treatment, boolean isLast) {
        String treatmentTarget, notes;
        int i;
        ArrayList<Medication> medications;
        Medication medication;

        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams")
        View treatmentLayout = inflater.inflate(R.layout.treatment_layout, null);

        float density = getResources().getDisplayMetrics().density;

        if (isLast) {
            int bottomMarginDp = 85;
            int bottomMarginPx = (int) (bottomMarginDp * density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, bottomMarginPx);
            treatmentLayout.setLayoutParams(params);
        }

        // Get the parent layout
        LinearLayout parentLayout = requireView().findViewById(R.id.linearLayoutCardView);

        // TREATMENT TARGET
        treatmentTarget = treatment.getTreatmentTarget();
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
        medications = treatment.getMedications();
        for (i = 0; i < medications.size(); i++) {
            medication = medications.get(i);
            medicationsLayout.addView(getMedicationLayout(medication));
        }

        parentLayout.addView(treatmentLayout);

        // NOTES
        notes = treatment.getNotes();
        if (notes == null || notes.equals("")) {
            LinearLayout linearLayoutNotes = treatmentLayout.findViewById(R.id.linearLayoutNotes);
            linearLayoutNotes.setVisibility(View.GONE);
        } else {
            TextView notesText = treatmentLayout.findViewById(R.id.notes);
            notesText.setText(notes);
        }

        // DELETE BUTTON
        // Find the delete button
        Button deleteButton = treatmentLayout.findViewById(R.id.deleteButton);

        if ("patient".equals(user)) {
            deleteButton.setVisibility(View.GONE);
            // set treatmentTarget textView marginTop to 16
            // Convert dp to pixels
            int startMarginPx = (int) (16 * density);
            int endMarginPx = (int) (4 * density);
            int topMarginPx = (int) (8 * density);
            int bottomMarginPx = (int) (8 * density);

            // Get the current LayoutParams and set the new margins
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) treatmentTargetText.getLayoutParams();
            params.setMargins(startMarginPx, topMarginPx, endMarginPx, bottomMarginPx);

            // Apply the new LayoutParams to the TextView
            treatmentTargetText.setLayoutParams(params);
        } else {


            // Create an instance of DatabaseAdapterPatient
            DatabaseAdapterPatient adapter = new DatabaseAdapterPatient(requireContext());
            // Set an OnClickListener for the delete button
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Delete the treatment from your data source
                    // You might need to create a method in your DatabaseAdapterPatient class to delete a treatment by its id
                    new MaterialAlertDialogBuilder(requireContext(), R.style.CustomMaterialDialog)
                            .setTitle(getResources().getString(R.string.delete_treatment))
                            .setMessage(getResources().getString(R.string.delete_treatment_msg))
                            .setNegativeButton(getResources().getString(R.string.keep), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Respond to negative button press
                                }
                            })
                            .setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Respond to positive button press
                                    adapter.deleteTreatment(patientUUID, treatmentId);

                                    // Remove the treatmentLayout from the parentLayout
                                    parentLayout.removeView(treatmentLayout);

                                    // If there are no more treatments, show the noTreatmentLayout
                                    if (parentLayout.getChildCount() == 0) {
                                        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View noTreatmentLayout = inflater.inflate(R.layout.no_treatments_found_layout, null);
                                        // Add the inflated layout to the parent layout
                                        parentLayout.addView(noTreatmentLayout);
                                    }

                                }
                            })
                            .create()
                            .show();

                }
            });
        }
    }

    protected View getMedicationLayout(Medication medication) {
        MappedValues mappedValues = new MappedValues(requireContext());
        String medicationName, howToTake, howRegularly;
        ArrayList<WeekdaysDataItem> selectedWeekdays;


        medicationName = medication.getMedicationName();
        howToTake = medication.toStringHowToTake(requireContext());
        howRegularly = medication.toStringHowRegularly(requireContext());

        ArrayList<String> intakeTimes;
        intakeTimes = medication.getIntakesTime();

        ArrayList<String> quantities;
        quantities = medication.getQuantities();

        if (medication.getSelectedWeekdays() != null) {
            selectedWeekdays = medication.getSelectedWeekdays();
        }

        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View medicationLayout = inflater.inflate(R.layout.medication_layout, null);

        // MEDICATION NAME
        TextView medicationNameText = medicationLayout.findViewById(R.id.medicationName);
        medicationNameText.setText("\u2022 " + medicationName);

        // HOW REGULARLY
        TextView howRegularlyText = medicationLayout.findViewById(R.id.howRegularly);

        if (medication.getHowRegularly() == 0) {
            // Daily
            howRegularlyText.setText(howRegularly);
        } else {
            if (medication.getHowRegularly() == 1) {
                // Interval
                howRegularlyText.setText(mappedValues.getFormattedInterval(medication.getIntervalSelectedType(), medication.getIntervalSelectedNumber()));
            } else {
                // Weekdays
                String selectedWeekdaysString;

                selectedWeekdaysString = medication.getSelectedWeekdaysString();

                howRegularlyText.setText(selectedWeekdaysString);
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

    public ExtendedFloatingActionButton getFab() {
        return fab;
    }


    @Override
    public void onResume() {
        super.onResume();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}
