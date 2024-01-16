package it.uniba.dib.sms232417.asilapp.auth.doctor;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;

import javax.crypto.SecretKey;

import it.uniba.dib.sms232417.asilapp.MainActivity;
import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapter;
import it.uniba.dib.sms232417.asilapp.auth.CryptoUtil;
import it.uniba.dib.sms232417.asilapp.auth.EntryActivity;
import it.uniba.dib.sms232417.asilapp.entity.Doctor;
import it.uniba.dib.sms232417.asilapp.entity.interface_entity.OnDoctorDataCallback;
import it.uniba.dib.sms232417.asilapp.utilities.StringUtils;

public class RegistrationDoctorFragment extends Fragment {

    String strDataNascita = "";
    DatabaseAdapter dbAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.doctor_registration_fragment_layout, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextInputEditText name = view.findViewById(R.id.txtName);
        TextInputEditText surname = view.findViewById(R.id.txtSurname);
        TextInputEditText numeroRegistrazione = view.findViewById(R.id.txtNumerodiRegistrazione);
        TextInputEditText email = view.findViewById(R.id.txtEmail);
        TextInputEditText password = view.findViewById(R.id.txtPassword);
        TextInputEditText confirmPassword = view.findViewById(R.id.txtPasswordConf);
        MaterialButton btnDataNascita = view.findViewById(R.id.date_of_birth);
        AutoCompleteTextView region = (AutoCompleteTextView) getView().findViewById(R.id.autoComplete_country);
        AutoCompleteTextView specialization = (AutoCompleteTextView) getView().findViewById(R.id.autoComplete_specialization);
        TextView loginDoctor = view.findViewById(R.id.txtLoginDoctor);
        Button btnRegister = view.findViewById(R.id.btnRegister);
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
                        strDataNascita = materialDatePicker.getHeaderText();

                    }
                });
                materialDatePicker.addOnNegativeButtonClickListener(
                        dialog -> {
                            btnDataNascita.setText(R.string.birth_date);
                            strDataNascita = "";
                        });
            }
        }
        );


        String[] specialization_array = getResources().getStringArray(R.array.specialization_arry);
        ArrayAdapter<String> s_adapter = new ArrayAdapter<String>(getContext(), R.layout.dropdown_item_specialization, specialization_array);
        specialization.setAdapter(s_adapter);

        String[] countries = getResources().getStringArray(R.array.countries_array);
        ArrayAdapter<String> r_adapter = new ArrayAdapter<String>(getContext(), R.layout.dropdown_item_region, countries);
        region.setAdapter(r_adapter);

        loginDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EntryActivity) getActivity()).replaceFragment(new LoginDoctorChooseFragment());
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString().isEmpty()){
                    name.setError(getString(R.string.empty_fields_nome));
                }else if(surname.getText().toString().isEmpty()){
                    surname.setError(getString(R.string.empty_fields_cognome));
                }else if(numeroRegistrazione.getText().toString().isEmpty()){
                    numeroRegistrazione.setError(getString(R.string.empty_fields_registration_number));
                }else if(email.getText().toString().isEmpty()){
                    email.setError(getString(R.string.empty_fields_email));
                }else if(password.getText().toString().isEmpty()){
                    password.setError(getString(R.string.empty_fields_password));
                }else if(confirmPassword.getText().toString().isEmpty()){
                    confirmPassword.setError(getString(R.string.empty_fields_password));
                }else if(!password.getText().toString().equals(confirmPassword.getText().toString())){
                    confirmPassword.setError(getString(R.string.password_not_match));
                }else if(strDataNascita.isEmpty()){
                    btnDataNascita.setError(getString(R.string.empty_fields_data));
                }else if(region.getText().toString().isEmpty()){
                    region.setError(getString(R.string.empty_fields_region));
                }else if(specialization.getText().toString().isEmpty()){
                    specialization.setError(getString(R.string.empty_fields_specialization));
                }else{
                    onRegister(name.getText().toString(), surname.getText().toString(), numeroRegistrazione.getText().toString(), email.getText().toString(), password.getText().toString(), strDataNascita, region.getText().toString(), specialization.getText().toString());
                }
            }
        });
    }

    private void onRegister(String nome, String cognome, String numeroRegistrazione, String email, String password, String strDataNascita, String regione,String specializzazione) {
        ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        dbAdapter = new DatabaseAdapter();
        dbAdapter.onRegistrationDoctor(nome, cognome, email, strDataNascita, regione, specializzazione, numeroRegistrazione, password, new OnDoctorDataCallback() {
            @Override
            public void onCallback(Doctor doctor) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.save_password).setMessage(R.string.save_password_explain);
                builder.setPositiveButton(R.string.yes, (dialog, which) -> {

                    SharedPreferences.Editor editor = requireActivity().getSharedPreferences(StringUtils.AUTOMATIC_LOGIN, requireActivity().MODE_PRIVATE).edit();
                    editor.putString("email", doctor.getEmail());

                    //Encrypt password con chiave simmetrica e salva su file
                    byte[] encryptedPassword = new byte[0];
                    byte[] iv = new byte[0];
                    try {
                        CryptoUtil.generateandSaveSecretKey(doctor.getEmail());
                        SecretKey secretKey = CryptoUtil.loadSecretKey(doctor.getEmail());
                        Pair<byte[], byte[]> encryptionResult = CryptoUtil.encryptWithKey(secretKey, password.getBytes());
                        encryptedPassword = encryptionResult.first;
                        iv = encryptionResult.second;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    editor.putString("password", Base64.encodeToString(encryptedPassword, Base64.DEFAULT));
                    editor.putString("iv", Base64.encodeToString(iv, Base64.DEFAULT));
                    editor.commit();

                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra("loggedDoctor", (Parcelable) doctor);
                    startActivity(intent);
                    progressBar.setVisibility(ProgressBar.INVISIBLE);

            });
            }

            @Override
            public void onCallbackError(Exception exception) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.registration_failed)
                        .setTitle(R.string.error)
                        .setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
                progressBar.setVisibility(ProgressBar.INVISIBLE);
            }

        });
    }
}
