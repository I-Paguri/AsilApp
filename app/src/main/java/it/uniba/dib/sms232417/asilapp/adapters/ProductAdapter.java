package it.uniba.dib.sms232417.asilapp.adapters;



import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.entity.Expenses;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(holder.productCheckbox.getContext());
        Resources res = context.getResources();
        builder.setTitle(res.getString(R.string.insert_details));

        // Inflate the custom layout
        LayoutInflater inflater = (LayoutInflater) holder.productCheckbox.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_product_adapter, null);

        // Get references to the input fields
        final TextInputEditText amountInput = dialogView.findViewById(R.id.amount_autocomplete);
        final TextInputEditText dateInput = dialogView.findViewById(R.id.dateInput);



        // Set up the category AutoCompleteTextView with an ArrayAdapter
        String[] categories = holder.productCheckbox.getContext().getResources().getStringArray(R.array.categories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(holder.productCheckbox.getContext(), android.R.layout.simple_dropdown_item_1line, categories);


        // Set up the dateInput field to show a DatePickerDialog when clicked
        dateInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(holder.productCheckbox.getContext(),
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // Format the date and set it as the text of dateInput
                        String date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year1;
                        dateInput.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });

        builder.setView(dialogView);

// Set up the buttons
        builder.setPositiveButton(res.getString(R.string.confirm), null);
        builder.setNegativeButton(res.getString(R.string.cancel), (dialog, which) -> {
            dialog.cancel();
            holder.productCheckbox.setChecked(false);
        });

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            Button button = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {

                try {
                    // Il tuo codice esistente va qui
            String category = "FARMACI";
            String amount = amountInput.getText().toString();
            String date = dateInput.getText().toString();



            // Get references to the TextInputLayouts
            TextInputLayout amountLayout = dialogView.findViewById(R.id.amount_layout);
            TextInputLayout dateLayout = dialogView.findViewById(R.id.date_layout);

            // Validate the amount field
            if (amount.isEmpty()) {
                // Show an error message
                amountLayout.setError(res.getString(R.string.field_required_error));
                return;
            } else {
                amountLayout.setError(null);
            }

            // Validate the date field
            if (date.isEmpty()) {
                // Show an error message
                dateLayout.setError(res.getString(R.string.field_required_error));
                return;
            } else {
                dateLayout.setError(null);
            }




            // Create an instance of DatabaseAdapterPatient
            DatabaseAdapterPatient adapter2 = new DatabaseAdapterPatient(holder.productCheckbox.getContext());

            // Get the instance of FirebaseAuth
            FirebaseAuth auth = FirebaseAuth.getInstance();

            // Get the currently logged in user
            FirebaseUser currentUser = auth.getCurrentUser();

            if (currentUser != null) {
                // Get the UUID of the currently logged in user
                String patientUUID = currentUser.getUid();

                // Convert the amount to a double
                double amountDouble;
                try {
                    amountDouble = Double.parseDouble(amount);
                } catch (NumberFormatException e) {
                    // Show an error message
                    Toast.makeText(holder.productCheckbox.getContext(), res.getString(R.string.valid_amount), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Convert the date to a Date object
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                Date dateObject;
                try {
                    dateObject = format.parse(date);
                } catch (ParseException e) {
                    // Show an error message

                    Toast.makeText(holder.productCheckbox.getContext(), res.getString(R.string.enter_valid_date), Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create an Expenses object
                Expenses.Category categoryEnum = Expenses.Category.valueOf(category.toUpperCase());
                Expenses expense = new Expenses(categoryEnum, amountDouble, dateObject);

                // Call the addExpense method
                adapter2.addExpense(patientUUID, expense);
                Log.d("Firestore", "Expense added successfully");

                // Remove the item from the product list
                BoughtProduct(medications.get(position).getId(), context);
                medications.remove(position);

                Toast.makeText(holder.productCheckbox.getContext(), res.getString(R.string.medication_purchased_successfully), Toast.LENGTH_SHORT).show();

                // Notify the adapter that the data has changed
                notifyDataSetChanged();
            } else {
                // Show an error message
                Toast.makeText(holder.productCheckbox.getContext(), res.getString(R.string.error_user_not_found), Toast.LENGTH_SHORT).show();
            }

                    dialog.dismiss();
                } catch (Exception e) {
                    // Stampa l'eccezione nel log
                    Log.e("ProductAdapter", "Eccezione nel pulsante Conferma", e);
                }

            });
        });

        // Aggiungi un listener per quando il dialog viene cancellato
        dialog.setOnCancelListener(dialogInterface -> {
            // Deseleziona la checkbox
            holder.productCheckbox.setChecked(false);
        });

        dialog.show();

/*
        builder.setNegativeButton("Annulla", (dialog, which) -> {
            dialog.cancel();
            holder.productCheckbox.setChecked(false);
        });

        builder.show();

    }
*/
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