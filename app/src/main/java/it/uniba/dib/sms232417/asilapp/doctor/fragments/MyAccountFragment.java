package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SharedMemory;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

import it.uniba.dib.sms232417.asilapp.MainActivity;
import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.auth.EntryActivity;

public class MyAccountFragment extends Fragment {
    Toolbar toolbar;
    final String NAME_FILE = "automaticLogin";
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    BottomNavigationView bottomNavigationView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_my_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomNavigationView = requireActivity().findViewById(R.id.nav_view);
        Button btnLogout = getView().findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogout(v);
            }
        });

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
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.my_account));
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

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            db = FirebaseFirestore.getInstance();
            db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                   TextView txtName = getView().findViewById(R.id.txt_name);
                   TextView txtSurname = getView().findViewById(R.id.txt_surname);
                   TextView txtRegion = getView().findViewById(R.id.txt_region);
                   TextView txtage = getView().findViewById(R.id.txt_age);

                   txtName.setText("Nome: "+task.getResult().getString("nome"));
                   txtSurname.setText("Cognome: "+task.getResult().getString("cognome"));
                   txtRegion.setText("Paese di provenienza: "+ task.getResult().getString("regione"));
                   /*
                   Calendar today = Calendar.getInstance();
                   Calendar dataNascita = Calendar.getInstance();

                   dataNascita.setTime(task.getResult().getDate("dataNascita"));
                   txtage.setText("Età: "+(today.get(Calendar.YEAR)-dataNascita.get(Calendar.YEAR))+" anni");
                     */
                }
            });
        }else {
             RelativeLayout relativeLayout = getView().findViewById(R.id.not_logged_user);
             relativeLayout.setVisibility(View.VISIBLE);
        }

    }
    public void onLogout(View v) {
            mAuth.signOut();
            Toast.makeText(getContext(),
                    "Logout effettuato",
                    Toast.LENGTH_SHORT).show();
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(NAME_FILE, requireActivity().MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            Intent esci = new Intent(getContext(), EntryActivity.class);
            startActivity(esci);
        }

}