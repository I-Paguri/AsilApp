package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.LinkedList;
import java.util.List;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterDoctor;
import it.uniba.dib.sms232417.asilapp.adapters.RecyclerListViewAdapter;
import it.uniba.dib.sms232417.asilapp.entity.Doctor;
import it.uniba.dib.sms232417.asilapp.entity.Patient;
import it.uniba.dib.sms232417.asilapp.interfaces.OnPatientListDataCallback;
import it.uniba.dib.sms232417.asilapp.utilities.listItem;

public class MyPatientsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerListViewAdapter adapter;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    Doctor doctor;
    List<Patient> myPatientsList;
    DatabaseAdapterDoctor dbAdapterDoctor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_patients, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = new Bundle();

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<listItem> list = new LinkedList<>();

        if (this.getArguments() != null) {
            doctor = (Doctor) this.getArguments().getParcelable("doctor");
        }

        dbAdapterDoctor = new DatabaseAdapterDoctor(getContext());

        if (doctor.getMyPatientsUUID() == null || doctor.getMyPatientsUUID().isEmpty()) {
            LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View noTreatmentLayout = inflater.inflate(R.layout.no_patients_layout, null);
            // Add the inflated layout to the parent layout
            LinearLayout parentLayout = view.findViewById(R.id.linearLayoutPatientsList);
            parentLayout.addView(noTreatmentLayout);
        } else {
            dbAdapterDoctor.getDoctorPatients(doctor.getMyPatientsUUID(), new OnPatientListDataCallback() {
                @Override
                public void onCallback(List<Patient> patientList) {
                    myPatientsList = patientList;

                    int[] indexUUID = new int[myPatientsList.size()];
                    for (int i = 0; i < myPatientsList.size(); i++) {
                        list.add(new listItem(myPatientsList.get(i).getNome() + " " + myPatientsList.get(i).getCognome(),
                                myPatientsList.get(i).getAge() + " " + getResources().getQuantityString(R.plurals.age,
                                        myPatientsList.get(i).getAge(), myPatientsList.get(i).getAge()),
                                R.drawable.my_account,
                                myPatientsList.get(i).getUUID()));
                    }

                    adapter = new RecyclerListViewAdapter(list, position -> {
                        // Handle item click here
                        listItem clickedItem = list.get(position);


                        // Open PatientFragment and pass the patient's name as an argument
                        PatientFragment patientFragment = new PatientFragment();

                        bundle.putString("patientUUID", clickedItem.getUUID());
                        bundle.putString("patientName", clickedItem.getTitle()); // assuming getTitle() gets the patient's name
                        bundle.putString("patientAge", clickedItem.getDescription());// assuming getSubtitle() gets the patient's age


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

                @Override
                public void onCallbackError(Exception exception, String message) {

                }
            });
        }

        bottomNavigationView = requireActivity().findViewById(R.id.nav_view);

        toolbar = requireActivity().findViewById(R.id.toolbar);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        // Show home button
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set home icon as back button
        Drawable homeIcon = getResources().getDrawable(R.drawable.home, null);
        // Set color filter
        homeIcon.setColorFilter(getResources().getColor(R.color.md_theme_light_surface), PorterDuff.Mode.SRC_ATOP);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeAsUpIndicator(homeIcon);

        // Set toolbar title
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.my_patients));
        // Change toolbar title text color
        toolbar.setTitleTextColor(getResources().getColor(R.color.md_theme_light_surface));

        // Set navigation click listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Navigate to HomeFragment
                bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, new HomeFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

}