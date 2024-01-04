package it.uniba.dib.sms232417.asilapp.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

import it.uniba.dib.sms232417.asilapp.MainActivity;
import it.uniba.dib.sms232417.asilapp.R;


public class EntryActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    public static Context getContext() {
      return getContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_activity_layout);


        Log.d("EntryActivity", "onCreate: ");
        final String NAME_FILE = "automaticLogin";
        SharedPreferences sharedPreferences = getSharedPreferences(NAME_FILE, MODE_PRIVATE);
        if(sharedPreferences.getString("email",null) != null){
            Log.d("EntryActivity", "Accesso automatico");
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(sharedPreferences.getString("email",null),sharedPreferences.getString("password",null)).addOnSuccessListener(
                    authResult -> {
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    }

            );
        }else {
            Log.d("EntryActivity", "Accesso Fallito");
        }

        Fragment loginFragment = new LoginFragment();
        Fragment registerFragment = new RegisterFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_out_left, R.anim.slide_in_right);

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
