package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.kofigyan.stateprogressbar.StateProgressBar;

import it.uniba.dib.sms232417.asilapp.R;

public class TreatmentFormNotesFragment extends Fragment {

    private String[] descriptionData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        descriptionData = new String[]{getResources().getString(R.string.planning), getResources().getString(R.string.medications), getResources().getString(R.string.notes)};

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_treatment_form_notes, container, false);


        StateProgressBar stateProgressBar = (StateProgressBar) view.findViewById(R.id.state_progress_bar);
        stateProgressBar.setStateDescriptionData(descriptionData);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        Button btnContinue = requireView().findViewById(R.id.goNext);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get notes
                TextInputEditText notes = requireView().findViewById(R.id.notesEditText);

                String notesString = "";
                if (!notes.getText().toString().isEmpty()) {
                    notesString = notes.getText().toString();

                } else {
                    // Save notes
                }
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