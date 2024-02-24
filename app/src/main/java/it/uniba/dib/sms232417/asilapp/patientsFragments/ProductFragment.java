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
import android.widget.LinearLayout;
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

import org.w3c.dom.Text;

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
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

                    LinearLayout parentLayout = requireView().findViewById(R.id.parentLayout);
                    TextView titleText = parentLayout.findViewById(R.id.medicationTitle);
                    TextView subtitleText = parentLayout.findViewById(R.id.medicationSubtitle);
                    if (hasUnboughtMedication) {
                        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
                        ProductAdapter productAdapter = new ProductAdapter(productItem, getContext());
                        recyclerView.setAdapter(productAdapter);
                        recyclerView.setVisibility(View.VISIBLE);
                        titleText.setVisibility(View.VISIBLE);
                        subtitleText.setVisibility(View.VISIBLE);
                    } else {

                        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
                        recyclerView.setVisibility(View.GONE);

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        float density = getResources().getDisplayMetrics().density;
                        int dp100 = (int) (100 * density);
                        layoutParams.setMargins(0, dp100, 0, 0);
                        parentLayout.setLayoutParams(layoutParams);

                        titleText = parentLayout.findViewById(R.id.medicationTitle);
                        titleText.setVisibility(View.GONE);
                        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View noTreatmentLayout = inflater.inflate(R.layout.no_medications_layout, null);
                        parentLayout.addView(noTreatmentLayout);
                    }
                }

                @Override
                public void onCallbackFailed(Exception e) {
                    System.out.println("Errore nel recupero dei trattamenti: " + e.getMessage());
                }
            });
        }
    }
}