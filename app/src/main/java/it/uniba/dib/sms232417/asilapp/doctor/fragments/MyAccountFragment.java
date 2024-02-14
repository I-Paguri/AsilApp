package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.UUID;

import it.uniba.dib.sms232417.asilapp.MainActivity;
import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterDoctor;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterPatient;
import it.uniba.dib.sms232417.asilapp.auth.CryptoUtil;
import it.uniba.dib.sms232417.asilapp.auth.EntryActivity;
import it.uniba.dib.sms232417.asilapp.entity.Doctor;
import it.uniba.dib.sms232417.asilapp.entity.Patient;
import it.uniba.dib.sms232417.asilapp.interfaces.OnProfileImageCallback;
import it.uniba.dib.sms232417.asilapp.utilities.StringUtils;

public class MyAccountFragment extends Fragment {
    Toolbar toolbar;
    Patient loggedPatient;
    Doctor loggedDoctor;
    final String NAME_FILE = "automaticLogin";
    DatabaseAdapterPatient dbAdapterPatient;
    DatabaseAdapterDoctor dbAdapterDoctor;
    BottomNavigationView bottomNavigationView;

    private StorageReference storageReference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_account, container, false);

        ImageView addProfilePicImageView = view.findViewById(R.id.add_profile_pic);
        addProfilePicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loggedPatient = checkPatientLogged();
        loggedDoctor = checkDoctorLogged();

        // Inizializza dbAdapterPatient e dbAdapterDoctor
        dbAdapterPatient = new DatabaseAdapterPatient(getContext());
        dbAdapterDoctor = new DatabaseAdapterDoctor(getContext());

        bottomNavigationView = requireActivity().findViewById(R.id.nav_view);
        Button btnLogout = getView().findViewById(R.id.btn_logout);

        // Verifica se il Bundle degli argomenti è null
        if (getArguments() != null) {
            // Ottieni l'URL dell'immagine del profilo dal bundle
            String profileImageUrl = getArguments().getString("profile_image_url");

            // Aggiorna l'immagine del profilo
            ImageView profileImageView = getView().findViewById(R.id.my_account);
            Glide.with(this)
                    .load(profileImageUrl)
                    .circleCrop()
                    .into(profileImageView);
        }

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

            // Ottieni l'URL dell'immagine del profilo dal database
            dbAdapterPatient.getProfileImage(loggedPatient.getUUID(), new OnProfileImageCallback() {
                @Override
                public void onCallback(String imageUrl) {
                    // Check if the profile image URL exists and is not empty before loading it
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(getContext())
                                .load(imageUrl)
                                .circleCrop()
                                .into((ImageView) getView().findViewById(R.id.my_account));
                    } else {
                        // If the profile image URL does not exist or is empty, load the default profile image
                        Glide.with(getContext())
                                .load(R.drawable.default_profile_image)
                                .circleCrop()
                                .into((ImageView) getView().findViewById(R.id.my_account));
                    }
                }

                @Override
                public void onCallbackError(Exception e) {
                    // If there is an error getting the profile image URL, load the default profile image
                    Glide.with(getContext())
                            .load(R.drawable.default_profile_image)
                            .circleCrop()
                            .into((ImageView) getView().findViewById(R.id.my_account));
                }
            });

        } else if (loggedDoctor != null) {
            TextView txtName = getView().findViewById(R.id.txt_name);
            TextView txtSurname = getView().findViewById(R.id.txt_surname);
            TextView txtRegion = getView().findViewById(R.id.txt_region);

            txtName.setText(loggedDoctor.getNome());
            txtSurname.setText(loggedDoctor.getCognome());
            txtRegion.setText(loggedDoctor.getRegione());

            // Gestione del click sull'immagine di profilo
            ImageView addProfilePic = getView().findViewById(R.id.add_profile_pic);
            addProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleAddProfilePicClick();
                }
            });

            // Ottieni l'URL dell'immagine del profilo dal database
            dbAdapterDoctor.getProfileImage(loggedDoctor.getEmail(), new OnProfileImageCallback() {
                @Override
                public void onCallback(String imageUrl) {
                    // Check if the profile image URL exists and is not empty before loading it
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(getContext())
                                .load(imageUrl)
                                .circleCrop()
                                .into((ImageView) getView().findViewById(R.id.my_account));
                    } else {
                        // If the profile image URL does not exist or is empty, load the default profile image
                        Glide.with(getContext())
                                .load(R.drawable.default_profile_image)
                                .circleCrop()
                                .into((ImageView) getView().findViewById(R.id.my_account));
                    }
                }

                @Override
                public void onCallbackError(Exception e) {
                    // If there is an error getting the profile image URL, load the default profile image
                    Glide.with(getContext())
                            .load(R.drawable.default_profile_image)
                            .circleCrop()
                            .into((ImageView) getView().findViewById(R.id.my_account));
                }
            });

        } else {
            RelativeLayout relativeLayout = getView().findViewById(R.id.not_logged_user);
            relativeLayout.setVisibility(View.VISIBLE);
        }

    }


    public void onLogout(View v, String email) throws Exception {
        dbAdapterDoctor = new DatabaseAdapterDoctor(getContext());
        dbAdapterPatient = new DatabaseAdapterPatient(getContext());


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
            dbAdapterPatient.onLogout();
        }
        if (loggedDoctorFile.exists()) {
            loggedDoctorFile.delete();
            dbAdapterDoctor.onLogout();
        }

        Intent esci = new Intent(getContext(), EntryActivity.class);
        startActivity(esci);
        requireActivity().finish();
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose an option")
                .setItems(new CharSequence[]{"Take Photo", "Choose from Gallery"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                // Take Photo
                                ImagePicker.with(MyAccountFragment.this)
                                        .cameraOnly()
                                        .start();
                                break;
                            case 1:
                                // Choose from Gallery
                                ImagePicker.with(MyAccountFragment.this)
                                        .galleryOnly()
                                        .start();
                                break;
                        }
                    }
                })
                .show();
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
                        ImagePicker.with(MyAccountFragment.this)
                                .cameraOnly()
                                .cropSquare()
                                .start();
                        break;
                    case 1:
                        // Gestire il clic su "Seleziona dalla galleria"
                        ImagePicker.with(MyAccountFragment.this)
                                .galleryOnly()
                                .cropSquare()
                                .start();
                        break;
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                if (loggedPatient != null) {
                    dbAdapterPatient.uploadImage(getContext(), bitmap, loggedPatient.getUUID());
                    dbAdapterPatient.updateProfileImage(loggedPatient.getUUID(), imageUri.toString());
                    // Update the profile image in the ImageView immediately after upload
                    Glide.with(getContext())
                            .load(imageUri)
                            .circleCrop()
                            .into((ImageView) getView().findViewById(R.id.my_account));
                } else if (loggedDoctor != null) {
                    dbAdapterDoctor.uploadImage(getContext(), bitmap, loggedDoctor.getEmail(), new OnProfileImageCallback() {
                        @Override
                        public void onCallback(String imageUrl) {
                            // Update the profile image in the database
                            dbAdapterDoctor.updateProfileImage(loggedDoctor.getEmail(), imageUrl);

                            // Update the profile image in the ImageView immediately after upload
                            Glide.with(getContext())
                                    .load(imageUrl)
                                    .circleCrop()
                                    .into((ImageView) getView().findViewById(R.id.my_account));

                            // Update the profile image in the ImageView of PatientFragment
                            // Assuming that PatientFragment is accessible from MyAccountFragment
                            ImageView profileImageViewPatient = getActivity().findViewById(R.id.imgProfile);
                            if (profileImageViewPatient != null) {
                                Glide.with(getContext())
                                        .load(imageUrl)
                                        .circleCrop()
                                        .into(profileImageViewPatient);
                            }
                        }

                        @Override
                        public void onCallbackError(Exception e) {
                            // Handle the error
                            Toast.makeText(getContext(), "Error uploading profile image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}