package it.uniba.dib.sms232417.asilapp.auth;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.Firebase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.MeasureFragment;

public class QRCodeAuth extends Fragment{

    FirebaseFirestore db;
    private ListenableFuture cameraProviderFuture;
    private ExecutorService cameraExecutor;
    private PreviewView previewView;
    private MyImageAnalyzer analyzer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_code, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        previewView = getView().findViewById(R.id.previewCamera);
        //this.getWindow().setFlags(1024, 1024);
        this.getActivity().getWindow().setFlags(1024, 1024);
        //Initialize the cameraExecutor
        cameraExecutor = Executors.newSingleThreadExecutor();
        //Initialize the cameraProviderFuture
        cameraProviderFuture = ProcessCameraProvider.getInstance(getContext());

        analyzer = new MyImageAnalyzer();
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

    public class MyImageAnalyzer implements ImageAnalysis.Analyzer {
        private FragmentManager fragmentManager;

       // private ResultQrCodeDialog resultQrCodeDialog;
        public MyImageAnalyzer() {}

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
                            readBarcodeData(barcodes);
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

    }
    private void readBarcodeData(List<Barcode> barcodes) {
        for (Barcode barcode: barcodes) {
            Rect bounds = barcode.getBoundingBox();
            Point[] corners = barcode.getCornerPoints();

            String rawValue = barcode.getRawValue();

            int valueType = barcode.getValueType();
            // See API reference for complete list of supported types
            switch (valueType) {
                case Barcode.TYPE_TEXT:
                    String uuid = barcode.getDisplayValue();

                    /*
                    Map<String,Object> UUID = new HashMap<>();
                    UUID.put("UUID",uuid);
                    email =

                    db.collection("qr_code_auth")
                            .document("userSign_"+uuid)
                            .set()

                    Bundle bundle = new Bundle();
                    bundle.putString("RisultatoQR", text);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    MeasureFragment newMeasureFragment = new MeasureFragment();
                    newMeasureFragment.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_main,newMeasureFragment).commit();

                    */
            }
        }

    }
}

