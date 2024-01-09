package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        super.onViewCreated(view, savedInstanceState);
    }
}