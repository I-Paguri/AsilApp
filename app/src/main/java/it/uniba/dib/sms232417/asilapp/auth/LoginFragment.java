package it.uniba.dib.sms232417.asilapp.auth;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

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

        ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.INVISIBLE);

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
                            db.collection("users")
                                    .document(utente.getEmail())
                                    .get()
                                    .addOnSuccessListener(task2 -> {
                                        if (task2.exists()) {
                                            String nome =  task2.get("nome").toString();
                                            String email = task2.get("email").toString();

                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("email", email);bundle.putSerializable("nome", nome);

                                            progressBar.setVisibility(ProgressBar.INVISIBLE);

                                            Intent intent = new Intent(getContext(), MainActivity.class);
                                            intent.putExtras(bundle);
                                            startActivity(intent);

                                        }

                                    });
                        }else {
                            progressBar.setVisibility(ProgressBar.INVISIBLE);
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("Error")
                                    .setMessage(R.string.something_wrong)
                                    .create();
                            builder.show();
                            }
                    })
                .addOnFailureListener(task -> {
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Error")
                            .setMessage(R.string.user_not_exists)
                            .create();
                    builder.show();;
                });
                };
        }

