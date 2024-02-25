package it.uniba.dib.sms232417.asilapp.fragments.patient;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.List;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.databaseAdapter.DatabaseAdapterPatient;
import it.uniba.dib.sms232417.asilapp.entity.Expenses;
import it.uniba.dib.sms232417.asilapp.entity.Patient;
import it.uniba.dib.sms232417.asilapp.interfaces.OnPatientDataCallback;


public class AddExpensesFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_add_expenses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button button = view.findViewById(R.id.add_button);


        // Trova l'AutoCompleteTextView per la categoria
        AutoCompleteTextView categoryInput = view.findViewById(R.id.categories);

        // Ottieni l'array di stringhe "categories" dalle risorse
        String[] categories = getResources().getStringArray(R.array.categories);

        // Crea un ArrayAdapter con le categorie
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, categories);

        // Imposta l'adapter sull'AutoCompleteTextView
        categoryInput.setAdapter(adapter);

        // Trova il TextInputEditText per l'importo
        TextInputEditText amountInput = view.findViewById(R.id.amount_autocomplete);

        // Gestione del DatePicker
        final TextInputEditText dateInput = view.findViewById(R.id.dateInput);
        dateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dateInput.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });


        // Imposta un OnClickListener per il pulsante
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Ottieni il testo dalle viste di input
                String category = categoryInput.getText().toString();
                String amountString = amountInput.getText().toString();
                String dateString = dateInput.getText().toString();

                // Trova i TextInputLayout per la categoria, l'importo e la data
                TextInputLayout categoryLayout = view.findViewById(R.id.category_layout);
                TextInputLayout amountLayout = view.findViewById(R.id.amount_layout);
                TextInputLayout dateLayout = view.findViewById(R.id.date_layout);

                // Controlla se i campi di input sono vuoti
                if (category.isEmpty()) {
                    // Mostra un messaggio di errore
                    categoryLayout.setError(getString(R.string.field_required_error));
                    return;
                } else {
                    categoryLayout.setError(null);
                }

                if (amountString.isEmpty()) {
                    // Mostra un messaggio di errore
                    amountLayout.setError(getString(R.string.field_required_error));
                    return;
                } else {
                    amountLayout.setError(null);
                }

                if (dateString.isEmpty()) {
                    // Mostra un messaggio di errore
                    dateLayout.setError(getString(R.string.field_required_error));
                    return;
                } else {
                    dateLayout.setError(null);
                }

                // Crea un AlertDialog con un layout personalizzato
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();

                View view = inflater.inflate(R.layout.dialog_layout, null);

                TextView title = view.findViewById(R.id.dialog_title);
                TextView message = view.findViewById(R.id.dialog_message);
                builder.setTitle(getResources().getString(R.string.add_expense_dialog_title));
                builder.setMessage(getResources().getString(R.string.add_expense_dialog_message));

                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // L'utente ha confermato, quindi aggiungi la spesa

                                // Converte la stringa in un double
                                double amount;
                                try {
                                    amount = Double.parseDouble(amountString);
                                } catch (NumberFormatException e) {
                                    // Mostra un messaggio di errore
                                    Toast.makeText(getActivity(), getResources().getString(R.string.valid_amount), Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // Convert the amount to a String
                                String amountString = amountInput.getText().toString();

                                // Check if the amount has more than two decimal places
                                if (amountString.contains(".")) {
                                    String[] parts = amountString.split("\\.");
                                    if (parts[1].length() > 2) {
                                        // Show an error message
                                        amountLayout.setError(getResources().getString(R.string.decimals_error));
                                        return;
                                    }
                                }

                                // Crea un oggetto SimpleDateFormat per il formato della data
                                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

                                // Converte la stringa in un oggetto Date
                                Date date;
                                try {
                                    date = format.parse(dateString);
                                } catch (ParseException e) {
                                    // Mostra un messaggio di errore
                                    Toast.makeText(getActivity(), getResources().getString(R.string.enter_valid_date), Toast.LENGTH_SHORT).show();                                    return;
                                }

                                Expenses.Category categoryEnum = Expenses.Category.fromString(category);

                                // Stampa le informazioni nel log
                                Log.d("Info", "Categoria: " + categoryEnum + ", Spesa: " + amount + ", Data: " + date);
                                Expenses expense = new Expenses(categoryEnum, amount, date);

                                // Get the instance of FirebaseAuth
                                FirebaseAuth auth = FirebaseAuth.getInstance();

                                // Get the currently logged in user
                                FirebaseUser currentUser = auth.getCurrentUser();

                                if (currentUser != null) {
                                    // Get the UUID of the currently logged in user
                                    String patientUUID = currentUser.getUid();

                                    // Create an instance of DatabaseAdapterPatient
                                    DatabaseAdapterPatient adapter = new DatabaseAdapterPatient(getContext());

                                    // Call the addExpense method
                                    adapter.addExpense(patientUUID, expense);
                                    Log.d("Firestore", "Expense added successfully");

                                    //Mostro un messaggio di successo
                                    Toast.makeText(getActivity(), getResources().getString(R.string.added_successfully), Toast.LENGTH_SHORT).show();


                                } else {
                                    // Show an error message
                                    Toast.makeText(getActivity(), getResources().getString(R.string.error_user_not_found), Toast.LENGTH_SHORT).show();
                                }


                                // Svuota i campi di input
                                categoryInput.setText("");
                                amountInput.setText("");
                                dateInput.setText("");
                            }
                        })
                        .setNegativeButton(android.R.string.no, null);

                builder.create().show();
            }
        });
    }
}






