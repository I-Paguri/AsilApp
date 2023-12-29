package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.RecyclerListViewAdapter;
import it.uniba.dib.sms232417.asilapp.utilities.listItem;

public class MyPatientsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerListViewAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_patients, container, false);
    }

    @Nullable
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        // Show back button
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        recyclerView = view.findViewById(R.id.recyclerView);

        List<listItem> list = new LinkedList<>();
        list.add(new listItem("Police", "Don't stand so close to me"));
        list.add(new listItem("Rihanna", "Love the way you lie"));
        list.add(new listItem("Marco Mengoni", "L'essenziale"));

        // Set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerListViewAdapter(list, position -> {
            // Handle item click here
            listItem clickedItem = list.get(position);

            // Open PatientFragment and pass the patient's name as an argument
            PatientFragment patientFragment = new PatientFragment();
            Bundle bundle = new Bundle();
            bundle.putString("patientName", clickedItem.getTitle()); // assuming getTitle() gets the patient's name
            patientFragment.setArguments(bundle);

            // Replace current fragment with PatientFragment
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.nav_host_fragment_activity_main, patientFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        });
        recyclerView.setAdapter(adapter);
    }

}