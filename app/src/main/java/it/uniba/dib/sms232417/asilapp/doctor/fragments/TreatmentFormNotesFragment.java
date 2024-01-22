package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.kofigyan.stateprogressbar.StateProgressBar;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterPatient;
import it.uniba.dib.sms232417.asilapp.entity.Treatment;

public class TreatmentFormNotesFragment extends Fragment {

    private Treatment treatment;
    private String notesString;
    private String patientUUID;
    private String patientName;
    private String patientAge;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String[] descriptionData = new String[]{getResources().getString(R.string.planning), getResources().getString(R.string.medications), getResources().getString(R.string.notes)};

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_treatment_form_notes, container, false);


        StateProgressBar stateProgressBar = (StateProgressBar) view.findViewById(R.id.state_progress_bar);
        stateProgressBar.setStateDescriptionData(descriptionData);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        patientName = "";
        patientAge = "";
        patientUUID = "";

        treatment = null;
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            treatment = bundle.getParcelable("treatment");
            patientUUID = bundle.getString("patientUUID");
            patientName = bundle.getString("patientName");
            patientAge = bundle.getString("patientAge");
        }

        Button btnContinue = requireView().findViewById(R.id.goNext);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get notes
                TextInputEditText notes = requireView().findViewById(R.id.notesEditText);

                notesString = "";
                if (!notes.getText().toString().isEmpty()) {
                    notesString = notes.getText().toString();
                    treatment.setNotes(notesString);
                }


                // ADD TREATMENT TO DB
                DatabaseAdapterPatient dbAdapter = new DatabaseAdapterPatient(getContext());
                dbAdapter.addTreatment(patientUUID, treatment);

                PatientFragment patientFragment = new PatientFragment();
                // Create a new bundle to pass the selected tab index
                Bundle bundleWithSelectedTab = new Bundle();
                bundleWithSelectedTab.putString("patientUUID", patientUUID);
                bundleWithSelectedTab.putString("patientName", patientName);
                bundleWithSelectedTab.putString("patientAge", patientAge);
                bundleWithSelectedTab.putInt("selectedTab", 1); // 1 is the index of the Treatment tab


                patientFragment.setArguments(bundleWithSelectedTab);
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, patientFragment);

                //transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        Button btnBack = requireView().findViewById(R.id.goBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    fragmentManager.popBackStack();
                }
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

}