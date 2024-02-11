package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import it.uniba.dib.sms232417.asilapp.R;

public class CapturedImageFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_captured_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ottieni l'immagine catturata dal bundle
        Bitmap capturedImage = getArguments().getParcelable("captured_image");

        // Rotate the image by 90 degrees
        Bitmap rotatedImage = rotateImage(capturedImage, 90);

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
            }
        });
    }

    public Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
