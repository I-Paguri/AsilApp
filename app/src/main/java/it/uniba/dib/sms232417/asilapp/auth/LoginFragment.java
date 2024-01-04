package it.uniba.dib.sms232417.asilapp.auth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import it.uniba.dib.sms232417.asilapp.MainActivity;
import it.uniba.dib.sms232417.asilapp.R;

public class LoginFragment extends Fragment {

    FirebaseAuth mAuth;
    final String NAME_FILE = "automaticLogin";
    FirebaseFirestore db;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.login_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView forgetPass = (TextView) getView().findViewById(R.id.txtForgetPass);
        TextView register = (TextView) getView().findViewById(R.id.txtRegister);
        Button login = (Button) getView().findViewById(R.id.btnLogin);
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onForgettPass(v);
            }

        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegister(v);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email = (EditText) getView().findViewById(R.id.txtEmail);
                EditText password = (EditText) getView().findViewById(R.id.txtPassword);
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                View focusedView = requireActivity().getCurrentFocus();
                if (focusedView != null) {
                    imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
                }
                if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Error")
                            .setMessage(R.string.empty_fields)
                            .create();
                    builder.show();
                }else
                    onLogin(v, email.getText().toString(), password.getText().toString());
            }
        });
    }

    private void onForgettPass(View V) {
        Toast.makeText(getContext(),
                "Hai Dimenticato la Password",
                Toast.LENGTH_SHORT).show();
    }

    private void onRegister(View V) {
        ((EntryActivity) getActivity()).replaceFragment(new RegisterFragment());
    }

    private void onLogin(View V, String emailIns, String password) {
        ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.VISIBLE);

        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(emailIns, password)
                .addOnCompleteListener(task ->  {
                        if (task.isSuccessful()) {
                            FirebaseUser utente = mAuth.getCurrentUser();
                            db = FirebaseFirestore.getInstance();
                            //Da modificare
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Do you want to save your password?")
                                    .setMessage("If you save your password, you will not have to enter it again when you log in.");
                            builder.setPositiveButton("Yes", (dialog, which) -> {
                                SharedPreferences sharedPref = requireActivity().getSharedPreferences(NAME_FILE,Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("email", emailIns);
                                editor.putString("password", password);
                                Log.d("LoginFragment", "Salvataggio effettuato");
                                editor.apply();
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                            });
                            builder.show();
                            builder.setNegativeButton("No", (dialog, which) -> {
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                            });




                        }else {
                            progressBar.setVisibility(ProgressBar.GONE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Error")
                                    .setMessage(R.string.something_wrong)
                                    .create();
                            builder.show();
                            }
                    })
                .addOnFailureListener(task -> {
                    progressBar.setVisibility(ProgressBar.GONE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Error")
                            .setMessage(R.string.user_not_exists)
                            .create();
                    builder.show();;
                });
                };
        }

