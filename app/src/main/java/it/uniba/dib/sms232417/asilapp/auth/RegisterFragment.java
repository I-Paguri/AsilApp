package it.uniba.dib.sms232417.asilapp.auth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import it.uniba.dib.sms232417.asilapp.MainActivity;
import it.uniba.dib.sms232417.asilapp.R;


public class RegisterFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    String dataNascita;
    String regione;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.register_fragment_layout, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


        TextView login = (TextView) getView().findViewById(R.id.txtLogin);
        Button register = (Button) getView().findViewById(R.id.btnRegister);
        MaterialButton btnDataNascita = (MaterialButton) getView().findViewById(R.id.date_of_birth);
        AutoCompleteTextView region = (AutoCompleteTextView) getView().findViewById(R.id.autoComplete_country);

        String[] countries = getResources().getStringArray(R.array.countries_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.dropdown_item_region, countries);
        region.setAdapter(adapter);
        btnDataNascita.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
                        builder.setTitleText("Select a Date");
                        MaterialDatePicker materialDatePicker = builder.build();
                        materialDatePicker.show(getFragmentManager(), "DATE_PICKER");
                        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                            @Override
                            public void onPositiveButtonClick(Object selection) {
                                btnDataNascita.setText(materialDatePicker.getHeaderText());
                                dataNascita = materialDatePicker.getHeaderText();
                            }
                        });
                        materialDatePicker.addOnNegativeButtonClickListener(
                                dialog -> {
                                    btnDataNascita.setText(R.string.bird_date);
                                    dataNascita = "";
                                }
                        );
                    }
                }
        );
        region.setOnItemClickListener((parent, view1, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            regione = selected;
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogin(v);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                View focusedView = requireActivity().getCurrentFocus();

                if (focusedView != null) {
                    imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                }
                String email = ((TextView) getView().findViewById(R.id.txtEmail)).getText().toString();
                String password = ((TextView) getView().findViewById(R.id.txtPassword)).getText().toString();
                String nome = ((TextView) getView().findViewById(R.id.txtName)).getText().toString();
                String cognome = ((TextView) getView().findViewById(R.id.txtSurname)).getText().toString();

                if(email.isEmpty() || password.isEmpty() || nome.isEmpty() || cognome.isEmpty()||dataNascita.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Error")
                            .setMessage(R.string.empty_fields)
                            .create();
                    builder.show();
                }else
                    onRegisterUsers(v,email,password,nome,cognome, dataNascita, regione);
            }
        });



    }

    public void onLogin(View v) {
        ((EntryActivity) getActivity()).replaceFragment(new LoginFragment());
    }

    public void onRegisterUsers(View v, String email, String password, String nome, String cognome,String dataNascita, String regione) {


            ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
            progressBar.setVisibility(ProgressBar.VISIBLE);


            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();

            db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(taskCheckUser -> {
                                if (taskCheckUser.isSuccessful() && taskCheckUser.getResult().isEmpty()) {
                                    mAuth.createUserWithEmailAndPassword(email, password)
                                            .addOnCompleteListener(task ->{
                                                if(task.isSuccessful()){
                                                    Map<String, Object> user = new HashMap<>();
                                                    user.put("nome", nome);
                                                    user.put("cognome", cognome);
                                                    user.put("email", email);
                                                    user.put("password", password);
                                                    user.put("dataNascita", dataNascita);
                                                    user.put("regione", regione);

                                                    db.collection("users")
                                                            .document(mAuth.getCurrentUser().getUid())
                                                            .set(user)
                                                            .addOnSuccessListener(task1 ->{
                                                                progressBar.setVisibility(ProgressBar.INVISIBLE);

                                                                Intent intent = new Intent(getContext(), MainActivity.class);
                                                                startActivity(intent);

                                                            })
                                                            .addOnFailureListener(task2 ->{
                                                                progressBar.setVisibility(ProgressBar.INVISIBLE);
                                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                                builder.setTitle("Error")
                                                                        .setMessage(R.string.registration_failed)
                                                                        .create();
                                                                builder.show();

                                                            });

                                                }
                                            });
                                }else {
                                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("Error")
                                            .setMessage(R.string.user_already_exists)
                                            .create();
                                    builder.show();
                                }

                            }
                    );

        }


}



