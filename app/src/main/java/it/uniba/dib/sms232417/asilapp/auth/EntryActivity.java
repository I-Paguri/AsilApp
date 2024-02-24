package it.uniba.dib.sms232417.asilapp.auth;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;


import java.util.Map;

import it.uniba.dib.sms232417.asilapp.MainActivity;
import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterDoctor;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterPatient;
import it.uniba.dib.sms232417.asilapp.auth.default_login.DefaultLoginFragment;
import it.uniba.dib.sms232417.asilapp.auth.doctor.LoginDoctorCredentialFragment;
import it.uniba.dib.sms232417.asilapp.auth.doctor.LoginDoctorQrCodeFragment;
import it.uniba.dib.sms232417.asilapp.auth.patient.LoginFragment;
import it.uniba.dib.sms232417.asilapp.auth.patient.RegisterFragment;
import it.uniba.dib.sms232417.asilapp.auth.qr_code_auth.QRCodeAuth;
import it.uniba.dib.sms232417.asilapp.entity.Doctor;
import it.uniba.dib.sms232417.asilapp.entity.Patient;
import it.uniba.dib.sms232417.asilapp.interfaces.OnDoctorDataCallback;
import it.uniba.dib.sms232417.asilapp.interfaces.OnPatientDataCallback;
import it.uniba.dib.sms232417.asilapp.patientsFragments.MapsFragment;
import it.uniba.dib.sms232417.asilapp.thread_connection.InternetCheckThread;
import it.uniba.dib.sms232417.asilapp.utilities.StringUtils;

import javax.crypto.SecretKey;

public class EntryActivity extends AppCompatActivity {

    DatabaseAdapterPatient dbAdapterPatient;
    DatabaseAdapterDoctor dbAdapterDoctor;
    private boolean doubleBackToExitPressedOnce = false;

    androidx.appcompat.app.AlertDialog alertDialog;
    Context context;
    Handler handler;
    boolean tryAutomaticLogin;
    boolean isDialogShow = false;
    InternetCheckThread internetCheckThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_activity_layout);

        ImageView entryLogo = findViewById(R.id.entryLogo);
        // Check if device language is italian
        if(getResources().getConfiguration().locale.getLanguage().equals("it")) {
            entryLogo.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.entry_logo_it, null));
        } else {
            entryLogo.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.entry_logo, null));
        }

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.what == 0) {
                    if(!isDialogShow) {
                        showNoInternetDialog();
                        isDialogShow = true;
                    }
                    tryAutomaticLogin = false;
                }else if(msg.what == 1){
                    deleteMsgError();
                    if(!tryAutomaticLogin) {
                        checkAutomaticLogin();
                        tryAutomaticLogin = true;

                    }
                    isDialogShow = false;
                }
                return true;
            }
        });

        if(internetCheckThread == null || !internetCheckThread.isAlive()) {
            internetCheckThread = new InternetCheckThread(this, handler);
            internetCheckThread.start();
        }






    }

    public void replaceFragment(Fragment fragment) {
        if(!isFinishing() && !isDestroyed()) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.replace(R.id.fragment_container_login, fragment);

            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    private void checkAutomaticLogin() {
        Log.d("checkAutomaticLogin", "checkAutomaticLogin");
        final Patient[] loggedPatient = {null};

        RelativeLayout loading = findViewById(R.id.loading);
        loading.setVisibility(RelativeLayout.VISIBLE);

        FrameLayout frameLayout = findViewById(R.id.fragment_container_login);
        frameLayout.setVisibility(FrameLayout.GONE);

        if(frameLayout.getVisibility() == FrameLayout.GONE)
            Log.d("Visibility_auth", "GONE");

        SharedPreferences sharedPreferences = getSharedPreferences(StringUtils.AUTOMATIC_LOGIN, MODE_PRIVATE);
        Map<String,?> keys = sharedPreferences.getAll();
        if(keys.isEmpty()){

            loading.setVisibility(RelativeLayout.GONE);
            frameLayout.setVisibility(FrameLayout.VISIBLE);
            replaceFragment(new LoginDecisionFragment());
        }else {
            Log.d("SharedPreferences", "not null");
            String email = sharedPreferences.getString("email", null);
            String password = sharedPreferences.getString("password", null);
            String iv = sharedPreferences.getString("iv", null);
            boolean isDoctor = sharedPreferences.getBoolean("isDoctor", false);

            if (!isDoctor) {
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
                                    internetCheckThread.stopRunning();
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onCallbackError(Exception e, String Message) {
                                    e.printStackTrace();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(EntryActivity.this);
                                    builder.setTitle(R.string.error).setMessage(Message);
                                    builder.setPositiveButton(R.string.yes, null);
                                    builder.show();

                                }
                            });
                        }
                    }
                } else {
                    loading.setVisibility(RelativeLayout.GONE);
                    frameLayout.setVisibility(FrameLayout.VISIBLE);
                }
            } else
                checkAutomaticLoginDoctor();
        }
    }


    private void checkAutomaticLoginDoctor() {
        Log.d("checkAutomaticLoginDoctor", "checkAutomaticLoginDoctor");
        final Doctor[] loggedDoctor = {null};
        RelativeLayout loading = findViewById(R.id.loading);
        loading.setVisibility(RelativeLayout.VISIBLE);
        FrameLayout frameLayout = findViewById(R.id.fragment_container_login);
        frameLayout.setVisibility(FrameLayout.GONE);

        SharedPreferences sharedPreferences = getSharedPreferences(StringUtils.AUTOMATIC_LOGIN, MODE_PRIVATE);
        if(sharedPreferences == null){
            loading.setVisibility(RelativeLayout.GONE);
            frameLayout.setVisibility(FrameLayout.VISIBLE);
            replaceFragment(new LoginDecisionFragment());
        }else {

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
                                    internetCheckThread.stopRunning();
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
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_login);

        if(currentFragment instanceof LoginDoctorQrCodeFragment){
            replaceFragment(new LoginDoctorCredentialFragment());
        } else if (currentFragment instanceof LoginFragment) {
            replaceFragment(new LoginDecisionFragment());
        }else if(currentFragment instanceof RegisterFragment) {
            replaceFragment(new LoginFragment());
        }else if(currentFragment instanceof LoginDoctorCredentialFragment || currentFragment instanceof DefaultLoginFragment){
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
    private void showNoInternetDialog() {
        if(!isFinishing() && !isDestroyed()) {
            showMsgError();
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle(R.string.no_connection_title);
            builder.setMessage(R.string.no_connection_explain);
            builder.setPositiveButton(R.string.no_connection_button, (dialog, which) -> {
                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            });
            builder.setNegativeButton(R.string.no_connection_button_cancel, (dialog, which) -> {
                dialog.dismiss();
            });
            alertDialog = builder.show();
        }
    }

    private void deleteMsgError() {
        RelativeLayout relativeLayout = findViewById(R.id.noConnectionLayout);
        relativeLayout.setVisibility(RelativeLayout.GONE);
        if(alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }
    private void showMsgError() {
        RelativeLayout relativeLayout = findViewById(R.id.noConnectionLayout);
        TextView textView = findViewById(R.id.noConnectionEditText);
        textView.setText(R.string.no_connection);
        relativeLayout.setVisibility(RelativeLayout.VISIBLE);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("EntryActivity", "Permissions:"+String.valueOf(requestCode));
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1) {
            if(grantResults.length > 0 && grantResults[0] == getPackageManager().PERMISSION_DENIED) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                builder.setTitle(R.string.attention);
                builder.setMessage(R.string.explain_permission_camera);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onRequestPermissionsResult(1, permissions, grantResults);
                    }
                });
            }else if(requestCode == 10 && grantResults.length > 0 && grantResults[0] == getPackageManager().PERMISSION_GRANTED) {
                Fragment selectedFragment = new LoginDoctorQrCodeFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, selectedFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        }



    }
}
