package it.uniba.dib.sms232417.asilapp.patientsFragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.List;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterPatient;
import it.uniba.dib.sms232417.asilapp.entity.Expenses;
import it.uniba.dib.sms232417.asilapp.entity.Patient;
import it.uniba.dib.sms232417.asilapp.interfaces.OnPatientDataCallback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddExpensesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddExpensesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddExpensesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddExpensesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddExpensesFragment newInstance(String param1, String param2) {
        AddExpensesFragment fragment = new AddExpensesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_expenses, container, false);

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
                    categoryLayout.setError("Questo campo è obbligatorio");
                    return;
                } else {
                    categoryLayout.setError(null);
                }

                if (amountString.isEmpty()) {
                    // Mostra un messaggio di errore
                    amountLayout.setError("Questo campo è obbligatorio");
                    return;
                } else {
                    amountLayout.setError(null);
                }

                if (dateString.isEmpty()) {
                    // Mostra un messaggio di errore
                    dateLayout.setError("Questo campo è obbligatorio");
                    return;
                } else {
                    dateLayout.setError(null);
                }

                // Converte la stringa in un double
                double amount;
                try {
                    amount = Double.parseDouble(amountString);
                } catch (NumberFormatException e) {
                    // Mostra un messaggio di errore
                    Toast.makeText(getActivity(), "Per favore, inserisci un importo valido", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Crea un oggetto SimpleDateFormat per il formato della data
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

                // Converte la stringa in un oggetto Date
                Date date;
                try {
                    date = format.parse(dateString);
                } catch (ParseException e) {
                    // Mostra un messaggio di errore
                    Toast.makeText(getActivity(), "Per favore, inserisci una data valida", Toast.LENGTH_SHORT).show();
                    return;
                }
                Expenses.Category categoryEnum = Expenses.Category.valueOf(category.toUpperCase());

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

// With this code
                    adapter.addExpense(patientUUID, expense);
                    Log.d("Firestore", "Expense added successfully");

                } else {
                    // Show an error message
                    Toast.makeText(getActivity(), "Errore: utente non trovato", Toast.LENGTH_SHORT).show();
                }

                //Mostro un messaggio di successo
                Toast.makeText(getActivity(), "Spesa aggiunta con successo", Toast.LENGTH_SHORT).show();

                // Svuota i campi di input
                categoryInput.setText("");
                amountInput.setText("");
                dateInput.setText("");
            }
        });


        return view;
    }
}






