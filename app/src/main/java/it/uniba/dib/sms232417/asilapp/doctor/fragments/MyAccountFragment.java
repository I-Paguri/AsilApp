package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import it.uniba.dib.sms232417.asilapp.MainActivity;
import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterDoctor;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterPatient;
import it.uniba.dib.sms232417.asilapp.auth.CryptoUtil;
import it.uniba.dib.sms232417.asilapp.auth.EntryActivity;
import it.uniba.dib.sms232417.asilapp.entity.Doctor;
import it.uniba.dib.sms232417.asilapp.entity.Patient;
import it.uniba.dib.sms232417.asilapp.utilities.StringUtils;

public class MyAccountFragment extends Fragment {
    Toolbar toolbar;
    Patient loggedPatient;
    Doctor loggedDoctor;
    final String NAME_FILE = "automaticLogin";
    DatabaseAdapterPatient dbAdapter;
    DatabaseAdapterDoctor dbAdapterDoctor;
    BottomNavigationView bottomNavigationView;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final int REQUEST_IMAGE_GALLERY = 2;

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

        loggedPatient = checkPatientLogged();
        loggedDoctor = checkDoctorLogged();

        bottomNavigationView = requireActivity().findViewById(R.id.nav_view);
        Button btnLogout = getView().findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loggedDoctor != null)
                    try {
                        onLogout(v, loggedDoctor.getEmail());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                else if (loggedPatient != null) {
                    try {
                        onLogout(v, loggedPatient.getEmail());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
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

        if (loggedPatient != null) {
            TextView txtName = getView().findViewById(R.id.txt_name);
            TextView txtSurname = getView().findViewById(R.id.txt_surname);
            TextView txtRegion = getView().findViewById(R.id.txt_region);

            // TextView txtage = getView().findViewById(R.id.txt_age);
            txtName.setText(loggedPatient.getNome());
            txtSurname.setText(loggedPatient.getCognome());
            txtRegion.setText(loggedPatient.getRegione());
            String dataNascita = loggedPatient.getDataNascita();

            // Gestione del click sull'immagine di profilo
            ImageView addProfilePic = getView().findViewById(R.id.add_profile_pic);
            addProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleAddProfilePicClick();
                }
            });

        } else if (loggedDoctor != null) {
            TextView txtName = getView().findViewById(R.id.txt_name);
            TextView txtSurname = getView().findViewById(R.id.txt_surname);
            TextView txtRegion = getView().findViewById(R.id.txt_region);

            txtName.setText(loggedDoctor.getNome());
            txtSurname.setText(loggedDoctor.getCognome());
            txtRegion.setText(loggedDoctor.getRegione());
        } else {
            RelativeLayout relativeLayout = getView().findViewById(R.id.not_logged_user);
            relativeLayout.setVisibility(View.VISIBLE);
        }


    }


    public void onLogout(View v, String email) throws Exception {
        dbAdapterDoctor = new DatabaseAdapterDoctor(getContext());
        dbAdapter = new DatabaseAdapterPatient(getContext());


        Toast.makeText(getContext(),
                "Logout effettuato",
                Toast.LENGTH_SHORT).show();
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(NAME_FILE, requireActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        CryptoUtil.deleteKey(email);

        File loggedPatientFile = new File(StringUtils.FILE_PATH_PATIENT_LOGGED);
        File loggedDoctorFile = new File(StringUtils.FILE_PATH_DOCTOR_LOGGED);
        if (loggedPatientFile.exists()) {
            loggedPatientFile.delete();
            dbAdapter.onLogout();
        }
        if (loggedDoctorFile.exists()) {
            loggedDoctorFile.delete();
            dbAdapterDoctor.onLogout();
        }

        Intent esci = new Intent(getContext(), EntryActivity.class);
        startActivity(esci);
        requireActivity().finish();
    }


    public Patient checkPatientLogged() {
        Patient loggedPatient;
        File loggedPatientFile = new File("/data/data/it.uniba.dib.sms232417.asilapp/files/loggedPatient");
        if (loggedPatientFile.exists()) {
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
        } else
            return null;

    }

    public Doctor checkDoctorLogged() {
        Doctor loggedDoctor;
        File loggedDoctorFile = new File("/data/data/it.uniba.dib.sms232417.asilapp/files/loggedDoctor");
        if (loggedDoctorFile.exists()) {
            try {
                FileInputStream fis = requireActivity().openFileInput(StringUtils.DOCTOR_LOGGED);
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
        } else
            return null;
    }

    public void handleAddProfilePicClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        boolean isProfilePicExists = false;
        if (isProfilePicExists) {
            builder.setTitle("Modifica immagine di profilo");
        } else {
            builder.setTitle("Aggiungi una foto profilo");
        }
        String[] options = {"Scatta fotografia", "Seleziona dalla galleria"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        // Gestire il clic su "Scatta fotografia"
                        checkCameraPermission();
                        break;
                    case 1:
                        // Gestire il clic su "Seleziona dalla galleria"
                        checkGalleryPermission();
                        break;
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Apri il fragment ProfileCameraFragment
            ProfileCameraFragment profileCameraFragment = new ProfileCameraFragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.nav_host_fragment_activity_main, profileCameraFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (requestCode == REQUEST_IMAGE_GALLERY && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Apri la galleria del dispositivo
            openGallery();
        } else {
            // Gestisci il caso in cui il permesso è stato negato
            // Puoi fornire un messaggio all'utente o prendere altre azioni appropriate
            Toast.makeText(getContext(), "Permesso negato", Toast.LENGTH_SHORT).show();
        }
    }
    public void checkCameraPermission(){

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != getActivity().getPackageManager().PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.CAMERA)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.attention);
                builder.setMessage(R.string.explain_permission_camera);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, 101);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.attention);
                builder.setMessage(R.string.explain_permission_camera);
                builder.setPositiveButton("OK", null);
                builder.show();

            }
        } else {
            // Apri il fragment ProfileCameraFragment
            ProfileCameraFragment profileCameraFragment = new ProfileCameraFragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.nav_host_fragment_activity_main, profileCameraFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }

    public void checkGalleryPermission(){

        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA) != getActivity().getPackageManager().PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.CAMERA)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.attention);
                builder.setMessage(R.string.explain_permission_gallery);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, 101);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                builder.show();

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.attention);
                builder.setMessage(R.string.explain_permission_gallery);
                builder.setPositiveButton("OK", null);
                builder.show();

            }
        } else {
            // Apri la galleria del dispositivo
            Intent i = new Intent(Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(i, REQUEST_IMAGE_GALLERY);
        }
    }

    // Aggiungi questo metodo alla tua classe per avviare l'intento della galleria dopo aver ottenuto il permesso
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            // L'utente ha selezionato un'immagine dalla galleria

        }
    }

}