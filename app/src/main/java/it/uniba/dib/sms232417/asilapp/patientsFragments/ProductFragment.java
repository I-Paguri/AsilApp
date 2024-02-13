package it.uniba.dib.sms232417.asilapp.patientsFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterPatient;
import it.uniba.dib.sms232417.asilapp.adapters.ProductAdapter;
import it.uniba.dib.sms232417.asilapp.entity.Medication;
import it.uniba.dib.sms232417.asilapp.entity.Treatment;
import it.uniba.dib.sms232417.asilapp.interfaces.OnTreatmentsCallback;
import it.uniba.dib.sms232417.asilapp.patientsFragments.placeholder.OperationItem;

public class ProductFragment extends Fragment {

    private AutoCompleteTextView quantityInput;
    private Button confirmButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        List<OperationItem> productItem = new ArrayList<>();
        // Ottieni l'istanza di FirebaseAuth
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Ottieni l'utente attualmente loggato
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // Ottieni l'UUID dell'utente attualmente loggato
            String patientUUID = currentUser.getUid();

            // Crea un'istanza di DatabaseAdapterPatient
            DatabaseAdapterPatient adapter = new DatabaseAdapterPatient(getContext());


            // Chiama il metodo getTreatments
            adapter.getTreatments(patientUUID, new OnTreatmentsCallback() {
                @Override
                public void onCallback(Map<String, Treatment> treatments) {
                    // Questo blocco di codice viene eseguito quando i trattamenti sono stati recuperati con successo
                    // 'treatments' è una mappa che associa gli ID dei trattamenti agli oggetti Treatment

                    // Ad esempio, per stampare tutti i trattamenti:
                    for (Map.Entry<String, Treatment> entry : treatments.entrySet()) {
                        String treatmentId = entry.getKey();
                        Treatment treatment = entry.getValue();

                        // Ottieni la lista di Medication
                        ArrayList<Medication> medications = treatment.getMedications();

                        // Itera attraverso la lista di Medication
                        for (Medication medication : medications) {
                            // Ottieni il nome del farmaco
                            String medicationName = medication.getMedicationName();

                            // Fai qualcosa con il nome del farmaco
                            System.out.println("Medication:"+medicationName);
                            productItem.add(new OperationItem(medicationName, 10, "data", "Alimentari", "01/01/2021"));

                        }
                    }

                    RecyclerView recyclerView = view.findViewById(R.id.recyclerView);


                    // Set up the RecyclerView
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
                    ProductAdapter productAdapter = new ProductAdapter(productItem);
                    recyclerView.setAdapter(productAdapter);
                }

                @Override
                public void onCallbackFailed(Exception e) {
                    // Questo blocco di codice viene eseguito se c'è stato un errore nel recupero dei trattamenti
                    System.out.println("Errore nel recupero dei trattamenti: " + e.getMessage());
                }
            });

        }


        return view;
    }
}