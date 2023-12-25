package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;


import it.uniba.dib.sms232417.asilapp.R;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Ottengo un riferimento alla cardView dei pazienti
        MaterialCardView cardViewMyPatients = view.findViewById(R.id.cardViewMyPatients);
        // Ottengo un riferimento alla bottom navigation bar
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.nav_view);

        // Imposto il listener per la cardView dei pazienti
        cardViewMyPatients.setOnClickListener(v -> {
            // Quando viene cliccata la cardView dei pazienti, viene aperto il fragment dei pazienti
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment_activity_main, new MyPatientsFragment());
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
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment_activity_main, new HealthcareFragment());
            transaction.commit();
            // Quando viene aperto il fragment dei pazienti viene aggiornata l'icona selezionata nella bottom navigation bar
            bottomNavigationView.setSelectedItemId(R.id.navigation_wellness);
        });


        return view;
    }
}