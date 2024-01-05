package it.uniba.dib.sms232417.asilapp.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

import it.uniba.dib.sms232417.asilapp.MainActivity;
import it.uniba.dib.sms232417.asilapp.R;


public class EntryActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    final String NAME_FILE = "automaticLogin";
    public static Context getContext() {
      return getContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_activity_layout);




        SharedPreferences sharedPreferences = getSharedPreferences(NAME_FILE, MODE_PRIVATE);
        if(sharedPreferences.getString("email",null) != null){
            RelativeLayout relativeLayout = findViewById(R.id.loading);
            relativeLayout.setVisibility(RelativeLayout.VISIBLE);
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(sharedPreferences.getString("email",null),sharedPreferences.getString("password",null)).addOnSuccessListener(
                    authResult -> {
                            relativeLayout.setVisibility(RelativeLayout.GONE);
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);

                            finish();
                    }

            );
        }else {
            RelativeLayout relativeLayout = findViewById(R.id.loading);
            relativeLayout.setVisibility(RelativeLayout.GONE);
            FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
            fragmentContainer.setVisibility(FrameLayout.VISIBLE);
        }



        Fragment loginFragment = new LoginFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        fragmentTransaction.replace(R.id.fragment_container, loginFragment);
        fragmentTransaction.commit();

    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.replace(R.id.fragment_container,fragment);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void newActivityRunning(@SuppressWarnings("rawtypes") Class newActivityClass, Bundle additionalData){
        Intent intent = new Intent(this, newActivityClass);

        if (additionalData != null){
            intent.putExtras(additionalData);
        }

        startActivity(intent); //start a new activity
    }

}
