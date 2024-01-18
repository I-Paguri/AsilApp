package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
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
import com.touchboarder.weekdaysbuttons.WeekdaysDataItem;

import java.util.ArrayList;
import java.util.Date;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.entity.Medication;
import it.uniba.dib.sms232417.asilapp.entity.Treatment;


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
        String treatmentTarget, medicationName, howToTake, howRegularly;
        Date startDate, endDate;

        treatmentTarget = "Abbassare la febbre";
        startDate = new Date();
        endDate = new Date();

        ArrayList< WeekdaysDataItem > selectedWeekdays;

        medicationName = "Paracetamolo";
        howToTake = "Supposta";
        howRegularly = "Ogni giorno";
        selectedWeekdays = new ArrayList<>();

        Medication medication1 = new Medication(medicationName, howToTake, howRegularly, selectedWeekdays);
        Medication medication2 = new Medication(medicationName, howToTake, howRegularly, selectedWeekdays);

        Treatment treatment = new Treatment(treatmentTarget, startDate, endDate);

        treatment.addMedication(medication1);
        treatment.addMedication(medication2);

        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View treatmentLayout = inflater.inflate(R.layout.treatment_layout, null);

        // Get the parent layout
        LinearLayout parentLayout = requireView().findViewById(R.id.linearLayoutCardView);

        // Get the titleText TextView from the inflated layout
        TextView titleText = treatmentLayout.findViewById(R.id.treatmentTarget);
        // Set the new text for the titleText TextView
        titleText.setText(treatmentTarget);
        TextView dateText = treatmentLayout.findViewById(R.id.dateText);
        dateText.setText(treatment.getStartDateString() + " - " + treatment.getEndDateString());



        // Add the new layout to the parent layout at the index of the "Add Intake" button
        parentLayout.addView(treatmentLayout);
    }
}
