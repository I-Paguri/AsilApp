package it.uniba.dib.sms232417.asilapp.auth.doctor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Base64;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import javax.crypto.SecretKey;

import it.uniba.dib.sms232417.asilapp.MainActivity;
import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterDoctor;
import it.uniba.dib.sms232417.asilapp.auth.CryptoUtil;
import it.uniba.dib.sms232417.asilapp.auth.EntryActivity;
import it.uniba.dib.sms232417.asilapp.entity.Doctor;
import it.uniba.dib.sms232417.asilapp.interfaces.OnDoctorDataCallback;
import it.uniba.dib.sms232417.asilapp.utilities.StringUtils;

public class LoginDoctorCredentialFragment extends Fragment {

    DatabaseAdapterDoctor dbAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.doctor_login_credential_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imageView = view.findViewById(R.id.backArrow);
        MaterialButton btnLogin = view.findViewById(R.id.btnLogin);
        TextInputEditText email = view.findViewById(R.id.txtEmail);
        TextInputEditText password = view.findViewById(R.id.txtPassword);
        MaterialButton btnLoginQr = view.findViewById(R.id.btnLoginDoctorQrCode);
        TextView txtForgotPassword = view.findViewById(R.id.txtForgetPass);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EntryActivity.class);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Nascondere la tastiera
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                String email = ((TextInputEditText) getView().findViewById(R.id.txtEmail)).getText().toString();
                String password = ((TextInputEditText) getView().findViewById(R.id.txtPassword)).getText().toString();

                if (email.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Error")
                            .setMessage(R.string.empty_fields_email)
                            .create();
                    builder.setPositiveButton("Ok", null);
                    builder.show();
                } else if (password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Error")
                            .setMessage(R.string.empty_fields_password)
                            .create();
                    builder.setPositiveButton("Ok", null);
                    builder.show();
                } else{

                    onLogin(v, email, password);
                };
            }
        });

        btnLoginQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EntryActivity) getActivity()).checkPermission();
            }
        });


    }

    private void onLogin(View v, String email, String password) {
        ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        dbAdapter = new DatabaseAdapterDoctor(getContext());
        dbAdapter.onLogin(email, password, new OnDoctorDataCallback() {
            @Override
            public void onCallback(Doctor doctor) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.save_password).setMessage(R.string.save_password_explain);
                builder.setPositiveButton(R.string.yes, (dialog, which) -> {

                    SharedPreferences.Editor editor = requireActivity().getSharedPreferences(StringUtils.AUTOMATIC_LOGIN, requireActivity().MODE_PRIVATE).edit();
                    editor.putString("email", doctor.getEmail());
                    editor.putBoolean("isDoctor", true);
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

                builder.setNegativeButton(R.string.no, (dialog, which) -> {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra("loggedDoctor", (Parcelable) doctor);
                    startActivity(intent);
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                });
                builder.show();
            }

            @Override
            public void onCallbackError(Exception e, String message) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.error).setMessage(message);
                builder.setPositiveButton(R.string.yes, null);
                builder.show();

            }
        });

    }
}
