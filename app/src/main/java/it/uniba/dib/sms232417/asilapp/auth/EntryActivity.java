package it.uniba.dib.sms232417.asilapp.auth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Base64;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;


import it.uniba.dib.sms232417.asilapp.MainActivity;
import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterDoctor;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterPatient;
import it.uniba.dib.sms232417.asilapp.auth.doctor.LoginDoctorCredentialFragment;
import it.uniba.dib.sms232417.asilapp.auth.patient.LoginFragment;
import it.uniba.dib.sms232417.asilapp.entity.Doctor;
import it.uniba.dib.sms232417.asilapp.entity.Patient;
import it.uniba.dib.sms232417.asilapp.entity.interface_entity.OnDoctorDataCallback;
import it.uniba.dib.sms232417.asilapp.entity.interface_entity.OnPatientDataCallback;
import it.uniba.dib.sms232417.asilapp.utilities.StringUtils;

import javax.crypto.SecretKey;

public class EntryActivity extends AppCompatActivity {

    DatabaseAdapterPatient dbAdapterPatient;
    DatabaseAdapterDoctor dbAdapterDoctor;
    RelativeLayout decisionLogin;

    Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_activity_layout);

        context = this;

        checkAutomaticLogin();


        FrameLayout fragmentContainer = findViewById(R.id.fragment_container);

        MaterialButton btnPatient = findViewById(R.id.btnLoginPatient);
        MaterialButton btnDoctor = findViewById(R.id.btnLoginDoctor);

        btnPatient.setOnClickListener(v -> {
            decisionLogin.setVisibility(RelativeLayout.GONE);
            fragmentContainer.setVisibility(FrameLayout.VISIBLE);
            LoginFragment loginFragment = new LoginFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

            fragmentTransaction.replace(R.id.fragment_container, loginFragment);
            fragmentTransaction.commit();
        });

        btnDoctor.setOnClickListener(v -> {
            decisionLogin.setVisibility(RelativeLayout.GONE);
            fragmentContainer.setVisibility(FrameLayout.VISIBLE);
            LoginDoctorCredentialFragment loginFragment = new LoginDoctorCredentialFragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

            fragmentTransaction.replace(R.id.fragment_container, loginFragment);
            fragmentTransaction.commit();
        });

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


        RelativeLayout loading = findViewById(R.id.loading);
        loading.setVisibility(RelativeLayout.VISIBLE);
        
        SharedPreferences sharedPreferences = getSharedPreferences(StringUtils.AUTOMATIC_LOGIN, MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        String password = sharedPreferences.getString("password", null);
        String iv = sharedPreferences.getString("iv", null);
        boolean isDoctor = sharedPreferences.getBoolean("isDoctor", false);

        if(!isDoctor) {
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
                        dbAdapterPatient = new DatabaseAdapterPatient(context);
                        dbAdapterPatient.onLogin(email, password, new OnPatientDataCallback() {
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
                            public void onCallbackError(Exception e, String Message) {
                                e.printStackTrace();
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle(R.string.error).setMessage(Message);
                                builder.setPositiveButton(R.string.yes, null);
                                builder.show();

                            }
                        });
                    }
                }
            } else {
                loading.setVisibility(RelativeLayout.GONE);
                decisionLogin = findViewById(R.id.decision_login_layout);
                decisionLogin.setVisibility(RelativeLayout.VISIBLE);
            }
        }else
            checkAutomaticLoginDoctor();
    }

    private void checkAutomaticLoginDoctor() {
        final Doctor[] loggedDoctor = {null};


        RelativeLayout loading = findViewById(R.id.loading);
        loading.setVisibility(RelativeLayout.VISIBLE);

        SharedPreferences sharedPreferences = getSharedPreferences(StringUtils.AUTOMATIC_LOGIN, MODE_PRIVATE);
        String email = sharedPreferences.getString("email", null);
        String password = sharedPreferences.getString("password", null);
        String iv = sharedPreferences.getString("iv", null);
        boolean isDoctor = sharedPreferences.getBoolean("isDoctor", false);
        if (isDoctor) {
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

                        dbAdapterDoctor = new DatabaseAdapterDoctor(context);
                        dbAdapterDoctor.onLogin(email, password, new OnDoctorDataCallback() {

                            @Override
                            public void onCallback(Doctor doctor) {
                                RelativeLayout relativeLayout = findViewById(R.id.loading);
                                relativeLayout.setVisibility(RelativeLayout.GONE);
                                Intent intent = new Intent(EntryActivity.this, MainActivity.class);
                                intent.putExtra("loggedDoctor", (Parcelable) doctor);
                                startActivity(intent);
                                finish();
                            }

                            @Override
                            public void onCallbackError(Exception e, String Message) {
                                e.printStackTrace();
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle(R.string.error).setMessage(Message);
                                builder.setPositiveButton(R.string.yes, null);
                                builder.show();

                            }
                        });

                    }
                }
            } else {
                loading.setVisibility(RelativeLayout.GONE);
                decisionLogin = findViewById(R.id.decision_login_layout);
                decisionLogin.setVisibility(RelativeLayout.VISIBLE);
            }
        }
    }


}
