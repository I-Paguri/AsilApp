package it.uniba.dib.sms232417.asilapp.auth.qr_code_auth;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.annotations.NotNull;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.SecretKey;

import it.uniba.dib.sms232417.asilapp.MainActivity;
import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterDoctor;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterPatient;
import it.uniba.dib.sms232417.asilapp.auth.CryptoUtil;
import it.uniba.dib.sms232417.asilapp.auth.EntryActivity;
import it.uniba.dib.sms232417.asilapp.auth.qr_code_auth.QRCodeAuth;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.MeasureFragment;
import it.uniba.dib.sms232417.asilapp.entity.Doctor;
import it.uniba.dib.sms232417.asilapp.entity.Patient;
import it.uniba.dib.sms232417.asilapp.interfaces.OnDoctorDataCallback;
import it.uniba.dib.sms232417.asilapp.utilities.StringUtils;

public class QRCodeAuth extends Fragment {

    private ListenableFuture cameraProviderFuture;
    private ExecutorService cameraExecutor;
    private PreviewView previewView;
    private LettoreQr analyzer;

    DatabaseAdapterPatient dbAdapterPatient;

    private Patient loggedPatient;
    private boolean isBarcodeRead = false;
    int i = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_code, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        }

        previewView = getView().findViewById(R.id.previewCamera);

        ViewGroup.LayoutParams params = previewView.getLayoutParams();
        params.width = 1300;
        params.height = 1300;
        previewView.setLayoutParams(params);

        //this.getWindow().setFlags(1024, 1024);
        this.getActivity().getWindow().setFlags(1024, 1024);
        //Initialize the cameraExecutor
        cameraExecutor = Executors.newSingleThreadExecutor();
        //Initialize the cameraProviderFuture
        cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());

        analyzer = new LettoreQr();
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider processCameraProvider = (ProcessCameraProvider) cameraProviderFuture.get();
                    bindpreview(processCameraProvider);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }, ContextCompat.getMainExecutor(getContext()));
    }

    private void bindpreview(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        ImageCapture imagecapture = new ImageCapture.Builder().build();
        ImageAnalysis imageanalysis = new ImageAnalysis.Builder().build();
        imageanalysis.setAnalyzer(cameraExecutor, analyzer);
        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imagecapture, imageanalysis);

    }

    public class LettoreQr implements ImageAnalysis.Analyzer {
        private FragmentManager fragmentManager;

        // private ResultQrCodeDialog resultQrCodeDialog;
        public LettoreQr() {
        }

        @Override
        public void analyze(@NotNull ImageProxy imageProxy) {
            scanBarcode(imageProxy);


        }

        private void scanBarcode(ImageProxy imageProxy) {

            @SuppressLint("UnsafeOptInUsageError") Image img1 = imageProxy.getImage();
            assert img1 != null;
            InputImage inputImage = InputImage.fromMediaImage(img1, imageProxy.getImageInfo().getRotationDegrees());
            BarcodeScannerOptions options =
                    new BarcodeScannerOptions.Builder()
                            .setBarcodeFormats(
                                    Barcode.FORMAT_QR_CODE,
                                    Barcode.FORMAT_AZTEC)
                            .build();
            BarcodeScanner scanner = BarcodeScanning.getClient(options);

            Task<List<Barcode>> result = scanner.process(inputImage)
                    .addOnSuccessListener(new OnSuccessListener<List<Barcode>>() {
                        @Override
                        public void onSuccess(List<Barcode> barcodes) {
                            Log.d("isBarcodeRead", String.valueOf(isBarcodeRead));
                            if (!isBarcodeRead) {
                                readBarcodeData(barcodes);

                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Task failed with an exception
                            // ...
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<List<Barcode>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<Barcode>> task) {
                            imageProxy.close();
                        }
                    });

        }

        private void readBarcodeData(List<Barcode> barcodes) {
            for (Barcode barcode : barcodes) {
                Rect bounds = barcode.getBoundingBox();
                Point[] corners = barcode.getCornerPoints();

                String rawValue = barcode.getRawValue();

                int valueType = barcode.getValueType();
                // See API reference for complete list of supported types
                switch (valueType) {
                    case Barcode.TYPE_TEXT:
                        String token = barcode.getDisplayValue();
                        if (token != null) {
                            isBarcodeRead = true;

                            dbAdapterPatient = new DatabaseAdapterPatient(getContext());
                            dbAdapterPatient.connectToContainer(token,loggedPatient.getUUID());
                            LayoutInflater inflater = getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.dialog_progress, null);

                                // Crea il dialogo utilizzando il layout personalizzato
                                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
                            builder.setView(dialogView)
                                    .setNegativeButton(R.string.cancel_misuration,null);// Impedisce all'utente di chiudere il dialogo
                                // Mostra il dialogo
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            }
                }
            }
        }
    }
}


