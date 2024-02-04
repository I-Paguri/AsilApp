package it.uniba.dib.sms232417.asilapp.auth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Base64;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;


import java.util.List;

import it.uniba.dib.sms232417.asilapp.MainActivity;
import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterDoctor;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterPatient;
import it.uniba.dib.sms232417.asilapp.auth.doctor.LoginDoctorCredentialFragment;
import it.uniba.dib.sms232417.asilapp.auth.doctor.LoginDoctorQrCodeFragment;
import it.uniba.dib.sms232417.asilapp.auth.patient.LoginFragment;
import it.uniba.dib.sms232417.asilapp.auth.patient.RegisterFragment;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.HomeFragment;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.MeasureFragment;
import it.uniba.dib.sms232417.asilapp.entity.Doctor;
import it.uniba.dib.sms232417.asilapp.entity.Patient;
import it.uniba.dib.sms232417.asilapp.interfaces.OnDoctorDataCallback;
import it.uniba.dib.sms232417.asilapp.interfaces.OnPatientDataCallback;
import it.uniba.dib.sms232417.asilapp.utilities.StringUtils;

import javax.crypto.SecretKey;

public class EntryActivity extends AppCompatActivity {

    DatabaseAdapterPatient dbAdapterPatient;
    DatabaseAdapterDoctor dbAdapterDoctor;
    private boolean doubleBackToExitPressedOnce = false;

    Context context;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_activity_layout);

        context = this;

        checkAutomaticLogin();

        replaceFragment(new LoginDecisionFragment());


    }

    public void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        FrameLayout frameLayout = findViewById(R.id.fragment_container);
        if(frameLayout.getVisibility() == FrameLayout.GONE)
            frameLayout.setVisibility(FrameLayout.VISIBLE);

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
            }
        }
    }
    public void checkPermission() {
        try {
            if (ActivityCompat.checkSelfPermission(EntryActivity.this, android.Manifest.permission.CAMERA) != getPackageManager().PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(EntryActivity.this, android.Manifest.permission.CAMERA)) {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                    builder.setTitle(R.string.attention);
                    builder.setMessage(R.string.explain_permission_camera);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ActivityCompat.requestPermissions(EntryActivity.this, new String[]{android.Manifest.permission.CAMERA}, 101);
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();

                } else {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                    builder.setTitle(R.string.attention);
                    builder.setMessage(R.string.explain_permission_camera);
                    builder.setPositiveButton("OK", null);
                    builder.show();

                }
            } else {
                replaceFragment(new LoginDoctorQrCodeFragment());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if(currentFragment instanceof LoginDoctorQrCodeFragment){
            replaceFragment(new LoginDoctorCredentialFragment());
        } else if (currentFragment instanceof LoginFragment) {
            replaceFragment(new LoginDecisionFragment());
        }else if(currentFragment instanceof RegisterFragment) {
            replaceFragment(new LoginFragment());
        }else if(currentFragment instanceof LoginDoctorCredentialFragment){
            replaceFragment(new LoginDecisionFragment());
        }else if (currentFragment instanceof LoginDecisionFragment) {
            if (doubleBackToExitPressedOnce) {
                finish();

            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getResources().getString(R.string.press_back_again), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }

    }
}
