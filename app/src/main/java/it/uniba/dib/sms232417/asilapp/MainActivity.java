package it.uniba.dib.sms232417.asilapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import it.uniba.dib.sms232417.asilapp.auth.qr_code_auth.QRCodeAuth;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.HomeFragment;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.HealthcareFragment;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.MeasureFragment;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.MyPatientsFragment;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.MyAccountFragment;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.PatientFragment;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.TreatmentFormGeneralFragment;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.TreatmentFormMedicationsFragment;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.TreatmentFragment;
import it.uniba.dib.sms232417.asilapp.entity.Doctor;
import it.uniba.dib.sms232417.asilapp.entity.Patient;
import it.uniba.dib.sms232417.asilapp.patientsFragments.ExpensesFragment;
import it.uniba.dib.sms232417.asilapp.thread_connection.InternetCheckThread;
import it.uniba.dib.sms232417.asilapp.thread_connection.NoConnectionFragment;
import it.uniba.dib.sms232417.asilapp.utilities.StringUtils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.xcode.onboarding.MaterialOnBoarding;
import com.xcode.onboarding.OnBoardingPage;
import com.xcode.onboarding.OnFinishLastPage;

public class MainActivity extends AppCompatActivity {

    InternetCheckThread internetCheckThread;
    AlertDialog alertDialog;
    Fragment selectedFragment = null;
    private Handler handler;
    private boolean isDialogShow = false;
    private boolean menuNoConnection = false;
    private boolean menuNormal = false;
    Patient loggedPatient;
    Doctor loggedDoctor;
    private boolean doubleBackToExitPressedOnce = false;
    private TreatmentFormMedicationsFragment treatmentFormMedicationsFragment;


    public static Context getContext() {
        return getContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Log.d("msg", String.valueOf(msg.what));
                if (msg.what == 0) {
                    if (!isDialogShow) {
                        showNoInternetDialog();
                        isDialogShow = true;
                        menuNormal = false;
                    }
                    changeMenu(0);

                } else if (msg.what == 1) {
                    changeMenu(1);
                    isDialogShow = false;
                    menuNoConnection = false;
                }
                return true;
            }
        });

        internetCheckThread = new InternetCheckThread(this, handler);
        internetCheckThread.start();

        setContentView(R.layout.activity_main);
        treatmentFormMedicationsFragment = new TreatmentFormMedicationsFragment();
        Intent intent = getIntent();
        loggedPatient = intent.getParcelableExtra("loggedPatient");
        loggedDoctor = intent.getParcelableExtra("loggedDoctor");

        ArrayList<OnBoardingPage> pages = new ArrayList<>();


        // Onboarding
        if (loggedPatient != null) {
            pages.add(new OnBoardingPage(R.drawable.measure_illustration,getResources().getString(R.string.streamlined_healthcare), getResources().getString(R.string.streamlined_healthcare_descr)));
            pages.add(new OnBoardingPage(R.drawable.diary_illustration,getResources().getString(R.string.your_digital_health_diary), getResources().getString(R.string.your_digital_health_diary_descr)));
            pages.add(new OnBoardingPage(R.drawable.maps3_illustration,getResources().getString(R.string.whats_nearby), getResources().getString(R.string.whats_nearby_descr)));
            pages.add(new OnBoardingPage(R.drawable.wallet_illustration,getResources().getString(R.string.wallet), getResources().getString(R.string.wallet_descr)));
            pages.add(new OnBoardingPage(R.drawable.videos_illustration,getResources().getString(R.string.recommended_videos), getResources().getString(R.string.recommended_videos_descr)));


            MaterialOnBoarding.setupOnBoarding(this, pages, new OnFinishLastPage() {
                @Override
                public void onNext() {
                    // Do whatever you want to do when the next button is clicked

                }

                public void finishOnBoarding() {
                    // Do whatever you want to do when the onboarding is finished
                }
            });

        }

        if (loggedDoctor != null) {
            pages.add(new OnBoardingPage(R.drawable.monitoring_illustration, getResources().getString(R.string.vital_sign_at_your_fingertips), getResources().getString(R.string.vital_sign_at_your_fingertips_descr)));
            pages.add(new OnBoardingPage(R.drawable.writing_illustration,getResources().getString(R.string.digital_treatments), getResources().getString(R.string.digital_treatments_descr)));
            pages.add(new OnBoardingPage(R.drawable.videos_illustration,getResources().getString(R.string.recommended_videos), getResources().getString(R.string.recommended_videos_descr)));

            MaterialOnBoarding.setupOnBoarding(this, pages, new OnFinishLastPage() {
                @Override
                public void onNext() {
                    // Do whatever you want to do when the next button is clicked

                }

                public void finishOnBoarding() {
                    // Do whatever you want to do when the onboarding is finished
                }
            });

        }

        changeMenu(1);
        updateIconProfileImage();

    }

    public void checkPermission() {
        try {
            if(ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA) != getPackageManager().PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA}, 1);
            }else {
                selectedFragment = new MeasureFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, selectedFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("MainActivity", "Permissions:"+String.valueOf(requestCode));
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1) {
            if(grantResults.length > 0 && grantResults[0] == getPackageManager().PERMISSION_DENIED) {
             MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
             builder.setTitle(R.string.attention);
             builder.setMessage(R.string.explain_permission_camera);
             builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkPermission();
                    }
                });
            }else {
                selectedFragment = new MeasureFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, selectedFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        }
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);

        if (fragment instanceof TreatmentFragment) {
            fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    /**
     * This method is called when the back button is pressed.
     * If the current fragment is HealthcareFragment, MyPatientsFragment, or MyAccountFragment, navigate to HomeFragment.
     * If the current fragment is HomeFragment, exit the app.
     */
    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);

        if (currentFragment instanceof HealthcareFragment || currentFragment instanceof MyPatientsFragment
                || currentFragment instanceof MyAccountFragment || currentFragment instanceof MeasureFragment || (currentFragment instanceof PatientFragment && loggedPatient != null)) {
            // If the current fragment is HealthcareFragment, MyPatientsFragment, or MyAccountFragment, navigate to HomeFragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.nav_host_fragment_activity_main, new HomeFragment());
            transaction.addToBackStack(null);
            transaction.commit();
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        } else {
            if (currentFragment instanceof TreatmentFormGeneralFragment) {
                // If the current fragment is TreatmentFormGeneralFragment, show a dialog
                new MaterialAlertDialogBuilder(this, R.style.CustomMaterialDialog)
                        .setTitle(getResources().getString(R.string.going_back))
                        .setMessage(getResources().getString(R.string.unsaved_changes))
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Respond to negative button press
                            }
                        })
                        .setPositiveButton(getResources().getString(R.string.go_back), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Respond to positive button press
                                // Navigate back
                                getSupportFragmentManager().popBackStack();
                            }
                        })
                        .create()
                        .show();
            } else if (currentFragment instanceof QRCodeAuth) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, new MeasureFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                if (currentFragment instanceof HomeFragment) {
                    if (doubleBackToExitPressedOnce) {
                        finish();
                        return;
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
    }

    private void showNoInternetDialog() {
        ImageView imageView = findViewById(R.id.wifi_off);
        imageView.setVisibility(ImageView.VISIBLE);
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

    private void deleteMsgError() {
        /*
        RelativeLayout relativeLayout = findViewById(R.id.noConnectionLayout);
        relativeLayout.setVisibility(RelativeLayout.GONE);

         */
        if (alertDialog != null && alertDialog.isShowing())
            alertDialog.dismiss();
        ImageView imageView = findViewById(R.id.wifi_off);
        imageView.setVisibility(ImageView.GONE);
    }

    public void changeMenu(int status) {
        if (status == 0) {
            if (!menuNoConnection) {
                Log.d("Passaggio a menu no connection", "Passaggio a menu no connection");
                BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
                bottomNavigationView.getMenu().clear();
                if (loggedPatient != null) {
                    bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_patient);
                    bottomNavigationView.setOnItemSelectedListener(item -> {
                        int itemId = item.getItemId();
                        if (itemId == R.id.navigation_home) {
                            selectedFragment = new NoConnectionFragment();
                        } else {
                            if (itemId == R.id.navigation_payments) {
                                selectedFragment = new NoConnectionFragment();
                            } else {
                                if (itemId == R.id.navigation_diary) {
                                    selectedFragment = new PatientFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("patientUUID", loggedPatient.getUUID());
                                    bundle.putString("patientName", loggedPatient.getNome());
                                    bundle.putString("patientAge", loggedPatient.getAge() + " " + getResources().getQuantityString(R.plurals.age,
                                            loggedPatient.getAge(), loggedPatient.getAge()));
                                    bundle.putString("user", "patient");

                                    selectedFragment.setArguments(bundle);
                                } else {
                                    if (itemId == R.id.navigation_my_account) {
                                        selectedFragment = new NoConnectionFragment();
                                    } else {
                                        if (itemId == R.id.navigation_measure) {
                                            selectedFragment = new NoConnectionFragment();
                                        }
                                    }
                                }
                            }
                        }
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.nav_host_fragment_activity_main, selectedFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        return true;
                    });

                } else if (loggedDoctor != null) {
                    bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_doctor);
                    bottomNavigationView.setOnItemSelectedListener(item -> {
                        int itemId = item.getItemId();
                        if (itemId == R.id.navigation_home) {
                            selectedFragment = new NoConnectionFragment();
                        } else {
                            if (itemId == R.id.navigation_my_patients) {
                                selectedFragment = new NoConnectionFragment();
                            } else {
                                if (itemId == R.id.navigation_my_account) {
                                    selectedFragment = new NoConnectionFragment();
                                }
                            }
                        }
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.nav_host_fragment_activity_main, selectedFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();

                        return true;
                    });

                }
                menuNoConnection = true;
            }
        } else {
            if (!menuNormal) {
                deleteMsgError();
                BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
                bottomNavigationView.getMenu().clear();
                Log.d("Passaggio a menu normale", "Passaggio a menu normale");
                if (loggedPatient != null) {
                    try {
                        FileOutputStream fos = openFileOutput(StringUtils.PATIENT_LOGGED, Context.MODE_PRIVATE);
                        ObjectOutputStream os = new ObjectOutputStream(fos);
                        os.writeObject(loggedPatient);
                        Log.d("Patient", "Patient saved");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_patient);

                    bottomNavigationView.setOnItemSelectedListener(item -> {

                        treatmentFormMedicationsFragment.setIntakeCount(1);
                        int itemId = item.getItemId();
                        /*
                         * If the selected item is HomeFragment, HealthcareFragment, MyPatientsFragment, or MyAccountFragment,
                         * replace the current fragment with the selected one.
                         * If the selected item is MeasureFragment, check the permission to use the camera.
                         */
                        if (itemId == R.id.navigation_home) {
                            selectedFragment = new HomeFragment();
                        } else {
                            if (itemId == R.id.navigation_payments) {
                                selectedFragment = new ExpensesFragment();
                            } else {
                                if (itemId == R.id.navigation_diary) {
                                    selectedFragment = new PatientFragment();
                                    Bundle bundle = new Bundle();

                                    bundle.putString("patientUUID", loggedPatient.getUUID());
                                    bundle.putString("patientName", loggedPatient.getNome());
                                    bundle.putString("patientAge", loggedPatient.getAge() + " " + getResources().getQuantityString(R.plurals.age,
                                            loggedPatient.getAge(), loggedPatient.getAge()));
                                    bundle.putString("user", "patient");

                                    selectedFragment.setArguments(bundle);
                                } else {
                                    if (itemId == R.id.navigation_my_account) {
                                        selectedFragment = new MyAccountFragment();


                                    } else {
                                        if (itemId == R.id.navigation_measure) {
                                            checkPermission();
                                        }
                                    }
                                }

                            }
                        }

                        if (selectedFragment != null) {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.replace(R.id.nav_host_fragment_activity_main, selectedFragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }

                        return true;
                    });

                    // Set default selection
                    bottomNavigationView.setSelectedItemId(R.id.navigation_home); // replace with your actual menu item id

                }
                if (loggedDoctor != null) {
                    try {
                        FileOutputStream fos = openFileOutput(StringUtils.DOCTOR_LOGGED, Context.MODE_PRIVATE);
                        ObjectOutputStream os = new ObjectOutputStream(fos);
                        os.writeObject(loggedDoctor);
                        Log.d("Doctor", "Doctor saved");
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_doctor);
                    bottomNavigationView.setOnItemSelectedListener(item -> {


                        treatmentFormMedicationsFragment.setIntakeCount(1);
                        int itemId = item.getItemId();
                        /*
                         * If the selected item is HomeFragment, HealthcareFragment, MyPatientsFragment, or MyAccountFragment,
                         * replace the current fragment with the selected one.
                         * If the selected item is MeasureFragment, check the permission to use the camera.
                         */
                        if (itemId == R.id.navigation_home) {
                            selectedFragment = new HomeFragment();
                        } else {
                            if (itemId == R.id.navigation_my_patients) {
                                selectedFragment = new MyPatientsFragment();
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("doctor", loggedDoctor);
                                selectedFragment.setArguments(bundle);
                            } else {
                                if (itemId == R.id.navigation_my_account) {
                                    selectedFragment = new MyAccountFragment();
                                }
                            }

                        }


                        if (selectedFragment != null) {
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.replace(R.id.nav_host_fragment_activity_main, selectedFragment);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }

                        return true;
                    });

                    // Set default selection
                    bottomNavigationView.setSelectedItemId(R.id.navigation_home); // replace with your actual menu item id

                }

            }
            menuNormal = true;
        }
    }

    public InternetCheckThread getInternetCheckThread() {
        return internetCheckThread;
    }

    // Method to update the bottom navigation icon my account with the url profile image
    public void updateIconProfileImage() {
        File file = new File(StringUtils.IMAGE_ICON);
        if (file.exists()) {
            Bitmap bitmap = new BitmapDrawable(getResources(), StringUtils.IMAGE_ICON).getBitmap();
            Drawable iconImage = new BitmapDrawable(getResources(), bitmap);

            BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
            Glide.with(this)
                    .load(iconImage)
                    .circleCrop()
                    .into(new CustomTarget<Drawable>() {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                            bottomNavigationView.getMenu().findItem(R.id.navigation_my_account).setIcon(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });

            bottomNavigationView.setItemIconTintList(null);
        }
    }

    @Override
    protected void onDestroy() {

        SharedPreferences sharedPreferences = getSharedPreferences(StringUtils.AUTOMATIC_LOGIN, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        // if shared preferences file exists
        if (sharedPreferences.getAll().isEmpty()) {
            File loggedPatientFile = new File(StringUtils.FILE_PATH_PATIENT_LOGGED);
            File loggedDoctorFile = new File(StringUtils.FILE_PATH_DOCTOR_LOGGED);
            Log.d("automaticLogin", "is empty");
            if (loggedPatientFile.exists()) {
                loggedPatientFile.delete();
            }
            if (loggedDoctorFile.exists()) {
                loggedDoctorFile.delete();
            }
        }

        super.onDestroy();
    }

}