package it.uniba.dib.sms232417.asilapp.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

import org.mindrot.jbcrypt.BCrypt;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.security.KeyStore;


import it.uniba.dib.sms232417.asilapp.MainActivity;
import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapter;
import it.uniba.dib.sms232417.asilapp.auth.patient.LoginFragment;
import it.uniba.dib.sms232417.asilapp.entity.Patient;
import it.uniba.dib.sms232417.asilapp.entity.interface_entity.OnPatientDataCallback;
import it.uniba.dib.sms232417.asilapp.utilities.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EntryActivity extends AppCompatActivity {

    DatabaseAdapter dbAdapter;
    public static Context getContext() {
      return getContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_activity_layout);

        checkAutomaticLogin();

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

    private void checkAutomaticLogin() {

        final Patient[] loggedPatient = {null};

        SharedPreferences sharedPreferences = getSharedPreferences(StringUtils.AUTOMATIC_LOGIN, MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        String password = sharedPreferences.getString("password", null);
        String iv = sharedPreferences.getString("iv", null);

        if (email != null) {
            if (password != null) {

                byte[] encryptPassword = Base64.decode(password, Base64.DEFAULT);
                byte[] ivByte = Base64.decode(iv, Base64.DEFAULT);
                try {
                    SecretKey secretKey = CryptoUtil.loadSecretKey(email);
                    byte[] decryptData = CryptoUtil.decryptWithKey(secretKey, encryptPassword, ivByte);
                    password = new String(decryptData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (password != null) {
                    dbAdapter = new DatabaseAdapter();
                    dbAdapter.onLogin(email, password, new OnPatientDataCallback() {
                        @Override
                        public void onCallback(Patient patient) {
                            RelativeLayout relativeLayout = findViewById(R.id.loading);
                            relativeLayout.setVisibility(RelativeLayout.GONE);
                            Intent intent = new Intent(EntryActivity.this, MainActivity.class);
                            intent.putExtra("loggedPatient", (Parcelable) patient);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onCallbackError(Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }else{
            RelativeLayout relativeLayout = findViewById(R.id.loading);
            relativeLayout.setVisibility(RelativeLayout.GONE);
            FrameLayout fragmentContainer = findViewById(R.id.fragment_container);
            fragmentContainer.setVisibility(FrameLayout.VISIBLE);
        }
    }

}
