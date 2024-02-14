package it.uniba.dib.sms232417.asilapp.adapters;

import static it.uniba.dib.sms232417.asilapp.MainActivity.getContext;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.entity.Medication;
import it.uniba.dib.sms232417.asilapp.entity.Treatment;
import it.uniba.dib.sms232417.asilapp.interfaces.OnTreatmentsCallback;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private ArrayList<Medication> medications;
    private Context context;

    public ProductAdapter(ArrayList<Medication> medications, Context context) {
        this.medications = medications;
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);

        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Medication medication = medications.get(position);
        holder.description.setText(medication.getMedicationName());
        holder.productCheckbox.setOnCheckedChangeListener(null); // Imposta il listener a null per evitare comportamenti inaspettati
        holder.productCheckbox.setChecked(medication.getIsBought()); // Imposta lo stato della checkbox in base al prodotto


        // Aggiungi un listener alla checkbox
        holder.productCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                handleCheckboxChecked(holder, position);
            }
        });
    }
    @Override
    public int getItemCount() {
        return medications.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView description;
        CheckBox productCheckbox;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            description = itemView.findViewById(R.id.description);
            productCheckbox = itemView.findViewById(R.id.product_checkbox);
        }
    }


    private void handleCheckboxChecked(ProductViewHolder holder, int position) {
        // Crea un AlertDialog con un AutoCompleteTextView
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.productCheckbox.getContext());
        builder.setTitle("Inserisci un valore");

        final AutoCompleteTextView input = new AutoCompleteTextView(holder.productCheckbox.getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Aggiungi un bottone per confermare
        builder.setPositiveButton("Conferma", (dialog, which) -> {
            String value = input.getText().toString();
            // Fai qualcosa con il valore inserito dall'utente

            // Crea un secondo AlertDialog per chiedere la categoria
            AlertDialog.Builder categoryBuilder = new AlertDialog.Builder(holder.productCheckbox.getContext());
            categoryBuilder.setTitle("Inserisci una categoria");

            // Inflate the dialog_for_product.xml layout and get the reference to the AutoCompleteTextView
            LayoutInflater inflater = (LayoutInflater) holder.productCheckbox.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);                    View dialogView = inflater.inflate(R.layout.dialog_for_product, null);
            final AutoCompleteTextView categoryInput = dialogView.findViewById(R.id.Categories_list);

            String[] categories = holder.productCheckbox.getContext().getResources().getStringArray(R.array.categories);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(holder.productCheckbox.getContext(), android.R.layout.simple_dropdown_item_1line, categories);
            categoryInput.setAdapter(adapter);
            categoryBuilder.setView(dialogView);

            // Aggiungi un bottone per confermare la categoria
            categoryBuilder.setPositiveButton("Conferma", (categoryDialog, categoryWhich) -> {
                String category = categoryInput.getText().toString();
                // Fai qualcosa con la categoria inserita dall'utente

                // Rimuovi l'elemento dalla lista di prodotti

                 BoughtProduct(medications.get(position).getId(), context);

                 medications.remove(position);

                // Notifica all'adapter che i dati sono cambiati
                notifyDataSetChanged();
            });

            categoryBuilder.setNegativeButton("Annulla", (categoryDialog, categoryWhich) -> {
                categoryDialog.cancel();
                holder.productCheckbox.setChecked(false);
            });

            categoryBuilder.show();
        });

        builder.setNegativeButton("Annulla", (dialog, which) -> {
            dialog.cancel();
            holder.productCheckbox.setChecked(false);
        });

        builder.show();
    }

    public void BoughtProduct(int idBought, Context context){


        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Ottieni l'utente attualmente loggato
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // Ottieni l'UUID dell'utente attualmente loggato
            String patientUUID = currentUser.getUid();

            // Crea un'istanza di DatabaseAdapterPatient
            DatabaseAdapterPatient adapter = new DatabaseAdapterPatient(context);


            // Chiama il metodo getTreatments
            adapter.getTreatments(patientUUID, new OnTreatmentsCallback() {
                @Override
                public void onCallback(Map<String, Treatment> treatments) {
                    // Questo blocco di codice viene eseguito quando i trattamenti sono stati recuperati con successo
                    // 'treatments' è una mappa che associa gli ID dei trattamenti agli oggetti Treatment


                    for (Map.Entry<String, Treatment> entry : treatments.entrySet()) {
                        String treatmentId = entry.getKey();
                        Treatment treatment = entry.getValue();

                        // Ottieni la lista di Medication
                        ArrayList<Medication> medications = treatment.getMedications();

                        // Itera attraverso la lista di Medication
                        for (Medication medication : medications) {


                           if (medication.getId()==(idBought)){

                               Log.d("BoughtProduct", "BoughtProduct: "+medication.getId());

                                medication.setIsBought(true);

                               FirebaseFirestore db = FirebaseFirestore.getInstance();
                               db.collection("patient")
                                       .document(patientUUID)
                                       .collection("treatments")
                                       .document(treatmentId)
                                       .update("medications",medications)
                                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void aVoid) {
                                               System.out.println("DocumentSnapshot successfully updated!");
                                           }
                                       })
                                       .addOnFailureListener(new OnFailureListener() {
                                           @Override
                                           public void onFailure(@NonNull Exception e) {
                                               System.out.println("Error updating document"+ e.getMessage());
                                           }
                                       });

                           }


                            }


                        }
                }

                @Override
                public void onCallbackFailed(Exception e) {
                    // Questo blocco di codice viene eseguito se c'è stato un errore nel recupero dei trattamenti
                    System.out.println("Errore nel recupero dei trattamenti: " + e.getMessage());
                }
            });

        }
    }


}