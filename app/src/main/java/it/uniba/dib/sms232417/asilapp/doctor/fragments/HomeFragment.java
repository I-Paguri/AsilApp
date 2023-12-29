package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;


import it.uniba.dib.sms232417.asilapp.R;

public class HomeFragment extends Fragment {

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // This is called when the fragment is associated with its context.
        // You can use this method to initialize essential components of the fragment that rely on the context.
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // This is called immediately after onCreateView() has returned, and fragment's view hierarchy has been instantiated.
        // You can use this method to do final initialization once these pieces are in place, such as retrieving views or restoring state.
        // Ottengo un riferimento alla cardView dei pazienti
        MaterialCardView cardViewMyPatients = view.findViewById(R.id.cardViewMyPatients);
        // Ottengo un riferimento alla bottom navigation bar
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.nav_view);
        Toolbar toolbar = requireActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        // Show back button
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // Imposto il listener per la cardView dei pazienti
        cardViewMyPatients.setOnClickListener(v -> {
            // Quando viene cliccata la cardView dei pazienti, viene aperto il fragment dei pazienti
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment_activity_main, new MyPatientsFragment());
            transaction.addToBackStack(null);
            transaction.commit();
            // Quando viene aperto il fragment dei pazienti viene aggiornata l'icona selezionata nella bottom navigation bar
            bottomNavigationView.setSelectedItemId(R.id.navigation_my_patients);
        });


        // Ottengo un riferimento alla cardView dei pazienti
        MaterialCardView cardViewHealthcare = view.findViewById(R.id.cardViewHealthcare);
        // Ottengo un riferimento alla bottom navigation bar

        // Imposto il listener per la cardView dei pazienti
        cardViewHealthcare.setOnClickListener(v -> {
            // Quando viene cliccata la cardView dei pazienti, viene aperto il fragment dei pazienti
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.nav_host_fragment_activity_main, new HealthcareFragment());
            transaction.addToBackStack(null);
            transaction.commit();
            // Quando viene aperto il fragment dei pazienti viene aggiornata l'icona selezionata nella bottom navigation bar
            bottomNavigationView.setSelectedItemId(R.id.navigation_healthcare);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            requireActivity().getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // This is called when the fragment is no longer attached to its activity.
        // This is where you can clean up any references to the activity.
    }

}