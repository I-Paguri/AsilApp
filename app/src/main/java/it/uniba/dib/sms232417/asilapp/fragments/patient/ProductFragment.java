package it.uniba.dib.sms232417.asilapp.fragments.patient;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Map;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.databaseAdapter.DatabaseAdapterPatient;
import it.uniba.dib.sms232417.asilapp.adapters.viewAdapter.ProductAdapter;
import it.uniba.dib.sms232417.asilapp.entity.Medication;
import it.uniba.dib.sms232417.asilapp.entity.Treatment;
import it.uniba.dib.sms232417.asilapp.interfaces.OnTreatmentsCallback;

public class ProductFragment extends Fragment {

    private AutoCompleteTextView quantityInput;
    private Button confirmButton;
    private Toolbar toolbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = requireActivity().findViewById(R.id.toolbar);


        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        // Show home button
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable backIcon = getResources().getDrawable(R.drawable.arrow_back, null);
        // Set color filter
        backIcon.setColorFilter(getResources().getColor(R.color.md_theme_light_surface), PorterDuff.Mode.SRC_ATOP);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeAsUpIndicator(backIcon);

        // Set toolbar title
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.medications_to_purchase));
        // Change toolbar title text color
        toolbar.setTitleTextColor(getResources().getColor(R.color.md_theme_light_surface));

        // Set navigation click listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

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
                        ProductAdapter productAdapter = new ProductAdapter(productItem, getContext(), parentLayout);
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