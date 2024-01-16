package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.badge.BadgeUtils;
import com.google.android.material.badge.ExperimentalBadgeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.entity.Doctor;
import it.uniba.dib.sms232417.asilapp.entity.Patient;
import it.uniba.dib.sms232417.asilapp.utilities.StringUtils;

public class HomeFragment extends Fragment {

    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private BottomNavigationView bottomNavigationView;
    private TextView txtusername;

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


    @OptIn(markerClass = ExperimentalBadgeUtils.class)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // This is called immediately after onCreateView() has returned, and fragment's view hierarchy has been instantiated.
        // You can use this method to do final initialization once these pieces are in place, such as retrieving views or restoring state.
        // Ottengo un riferimento alla cardView dei pazienti

        bottomNavigationView = requireActivity().findViewById(R.id.nav_view);

        toolbar = requireActivity().findViewById(R.id.toolbar);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        // Show home button
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        BadgeDrawable badgeDrawable = BadgeDrawable.create(requireContext());
        badgeDrawable.setNumber(10);


        // Set toolbar title
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.home));
        // Change toolbar title text color
        toolbar.setTitleTextColor(getResources().getColor(R.color.md_theme_light_surface));

        MaterialCardView cardViewMyPatients = view.findViewById(R.id.cardViewMyPatients);


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

        /*
        // Create the badge
        badgeDrawable.setNumber(10);

        badgeDrawable.setVerticalOffset(90);
        badgeDrawable.setHorizontalOffset(90);

        // Get the menu item view
        MenuItem menuItem = bottomNavigationView.getMenu().findItem(R.id.navigation_my_patients);
        View anchor = bottomNavigationView.findViewById(menuItem.getItemId());

        // Attach the badge
        BadgeUtils.attachBadgeDrawable(badgeDrawable, anchor);
        */

        txtusername = view.findViewById(R.id.txtUser_Name);
        Patient loggedPatient = checkPatientLogged();
        Doctor loggedDoctor = checkDoctorLogged();
        if (loggedPatient != null) {
            txtusername.setText(loggedPatient.getNome());
        } else if(loggedDoctor != null){
            txtusername.setText("Dottor "+loggedDoctor.getNome());
        }else{
            txtusername.setText("Utente");
        }

    }


    @Override
    public void onDetach() {
        super.onDetach();
        // This is called when the fragment is no longer attached to its activity.
        // This is where you can clean up any references to the activity.
    }
    public Patient checkPatientLogged(){
        Patient loggedPatient;
        File loggedPatientFile = new File("/data/data/it.uniba.dib.sms232417.asilapp/files/loggedPatient");
        if(loggedPatientFile.exists()){
            try {
                FileInputStream fis = requireActivity().openFileInput(StringUtils.PATIENT_LOGGED);
                ObjectInputStream ois = new ObjectInputStream(fis);
                loggedPatient = (Patient) ois.readObject();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            return loggedPatient;
        }else
            return null;

    }

    public Doctor checkDoctorLogged(){
        Doctor loggedDoctor;
        File loggedDoctorFile = new File("/data/data/it.uniba.dib.sms232417.asilapp/files/loggedDoctor");
        if(loggedDoctorFile.exists()){
            Log.d("FILE", "File esiste");
            try {
                FileInputStream fis = requireActivity().openFileInput(loggedDoctorFile.getName());
                ObjectInputStream ois = new ObjectInputStream(fis);
                loggedDoctor = (Doctor) ois.readObject();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            return loggedDoctor;
    }else
        return null;
    }

}