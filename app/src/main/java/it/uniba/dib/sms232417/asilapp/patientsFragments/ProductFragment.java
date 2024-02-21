package it.uniba.dib.sms232417.asilapp.patientsFragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterPatient;
import it.uniba.dib.sms232417.asilapp.adapters.ProductAdapter;
import it.uniba.dib.sms232417.asilapp.entity.Medication;
import it.uniba.dib.sms232417.asilapp.entity.Treatment;
import it.uniba.dib.sms232417.asilapp.interfaces.OnTreatmentsCallback;

public class ProductFragment extends Fragment {

    private AutoCompleteTextView quantityInput;
    private Button confirmButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        ImageView emptyListImage = view.findViewById(R.id.emptyMedicationImage);
        emptyListImage.setVisibility(View.GONE);
        TextView emptyListText = view.findViewById(R.id.emptyMedicationText);
        emptyListText.setVisibility(View.GONE);

        ArrayList<Medication> productItem = new ArrayList<>();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String patientUUID = currentUser.getUid();
            DatabaseAdapterPatient adapter = new DatabaseAdapterPatient(getContext());

            adapter.getTreatments(patientUUID, new OnTreatmentsCallback() {
                @Override
                public void onCallback(Map<String, Treatment> treatments) {
                    boolean hasUnboughtMedication = false;
                    if (!treatments.isEmpty()) {
                        for (Map.Entry<String, Treatment> entry : treatments.entrySet()) {
                            ArrayList<Medication> medications = entry.getValue().getMedications();
                            for (Medication medication : medications) {
                                if (!medication.getIsBought()) {
                                    hasUnboughtMedication = true;
                                    productItem.add(medication);
                                }
                            }
                        }
                    }
                    if (hasUnboughtMedication) {
                        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
                        ProductAdapter productAdapter = new ProductAdapter(productItem, getContext());
                        recyclerView.setAdapter(productAdapter);
                        emptyListImage.setVisibility(View.GONE);
                        emptyListText.setVisibility(View.GONE);
                    } else {
                        emptyListImage.setVisibility(View.VISIBLE);
                        emptyListText.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCallbackFailed(Exception e) {
                    System.out.println("Errore nel recupero dei trattamenti: " + e.getMessage());
                }
            });
        }
        return view;
    }
}