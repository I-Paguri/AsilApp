package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import it.uniba.dib.sms232417.asilapp.R;

public class CapturedImageFragment extends Fragment {
    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // get the Firebase storage reference
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        return inflater.inflate(R.layout.fragment_captured_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ottieni l'immagine catturata e l'orientamento dal bundle
        Bitmap capturedImage = getArguments().getParcelable("captured_image");

        // Ottieni l'orientamento dell'immagine
        int orientation = getArguments().getInt("orientation");

        // Ruota l'immagine in base all'orientamento
        Matrix matrix = new Matrix();
        matrix.postRotate(orientation);
        Bitmap rotatedImage = Bitmap.createBitmap(capturedImage, 0, 0, capturedImage.getWidth(), capturedImage.getHeight(), matrix, true);

        // Visualizza l'immagine catturata
        ImageView imageView = getView().findViewById(R.id.captured_image);
        imageView.setImageBitmap(rotatedImage);

        // Imposta il listener dell'icona di chiusura
        ImageView closeButton = getView().findViewById(R.id.close_camera);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Naviga indietro al ProfileCameraFragment
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_main, new ProfileCameraFragment()).commit();
            }
        });

        // Imposta il listener dell'icona "done"
        ImageView doneIcon = getView().findViewById(R.id.capture_done);
        doneIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gestisci il clic sull'icona "done"
                // Ad esempio, potresti salvare l'immagine e navigare indietro al ProfileCameraFragment
                uploadImage(rotatedImage);
            }
        });
    }

    private void uploadImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());

        UploadTask uploadTask = ref.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getContext(), "Upload failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Passa l'URL dell'immagine al MyAccountFragment
                        Bundle bundle = new Bundle();
                        bundle.putString("profile_image_url", uri.toString());

                        MyAccountFragment myAccountFragment = new MyAccountFragment();
                        myAccountFragment.setArguments(bundle);

                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_main, myAccountFragment).commit();
                    }
                });
            }
        });
    }
}