package it.uniba.dib.sms232417.asilapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import it.uniba.dib.sms232417.asilapp.doctor.fragments.HomeFragment;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.HealthcareFragment;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.MeasureFragment;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.MyPatientsFragment;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.MyAccountFragment;


public class MainActivity extends AppCompatActivity {

    public static Context getContext() {
        return getContext();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Dati utente loggato
        Bundle loggedUser = getIntent().getExtras();

        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) { // replace with your actual menu item id
                selectedFragment = new HomeFragment();
            } else {
                if (itemId == R.id.navigation_wellness) { // replace with your actual menu item id
                    selectedFragment = new HealthcareFragment();
                } else {
                    if (itemId == R.id.navigation_my_patients) { // replace with your actual menu item id
                        selectedFragment = new MyPatientsFragment();
                    } else {
                        if (itemId == R.id.navigation_my_account) {
                            // replace with your actual menu item id
                            selectedFragment = new MyAccountFragment();
                            selectedFragment.setArguments(loggedUser);
                        } else {
                            if (itemId == R.id.navigation_measure)
                                selectedFragment = new MeasureFragment();
                        }
                    }

                }
            }
            // add more else-if here for other menu items

            if (selectedFragment != null) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, selectedFragment);
                transaction.commit();
            }

            return true;
        });

            // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.navigation_home); // replace with your actual menu item id
    }
}