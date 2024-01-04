package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import it.uniba.dib.sms232417.asilapp.R;


public class TreatmentFormMedicinesFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_treatment_form_medicines, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        // Find the AutoCompleteTextView in the layout
        AutoCompleteTextView medicinesList = view.findViewById(R.id.medicines_list);

        // Get the string array from the resources
        String[] medicines = getResources().getStringArray(R.array.medicines_list);

        // Create an ArrayAdapter using the string array and a default layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, medicines);

        // Set the ArrayAdapter to the AutoCompleteTextView
        medicinesList.setAdapter(adapter);



        super.onViewCreated(view, savedInstanceState);
    }
}