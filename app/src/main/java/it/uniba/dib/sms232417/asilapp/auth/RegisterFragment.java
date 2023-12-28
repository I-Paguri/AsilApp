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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import it.uniba.dib.sms232417.asilapp.MainActivity;
import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.entity.Utente;

public class RegisterFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth mAuth;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.register_fragment_layout, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.INVISIBLE);

        TextView login = (TextView) getView().findViewById(R.id.txtLogin);
        Button register = (Button) getView().findViewById(R.id.btnRegister);
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

                if(email.isEmpty() || password.isEmpty() || nome.isEmpty() || cognome.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Error")
                            .setMessage(R.string.empty_fields)
                            .create();
                    builder.show();
                }else
                    onRegisterUsers(v,email,password,nome,cognome);
            }
        });


    }

    public void onLogin(View v) {
        ((EntryActivity) getActivity()).replaceFragment(new LoginFragment());
    }

    public void onRegisterUsers(View v, String email, String password, String nome, String cognome) {


            ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
            progressBar.setVisibility(ProgressBar.VISIBLE);

            Utente utente = new Utente(nome,cognome,email,password);

            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();

            db.collection("users")
                    .whereEqualTo("email", utente.getEmail())
                    .get()
                    .addOnCompleteListener(taskCheckUser -> {
                                if (taskCheckUser.isSuccessful() && taskCheckUser.getResult().isEmpty()) {
                                    mAuth.createUserWithEmailAndPassword(utente.getEmail(), utente.getPassword())
                                            .addOnCompleteListener(task ->{
                                                if(task.isSuccessful()){
                                                    Map<String, Object> user = new HashMap<>();
                                                    user.put("nome", utente.getNome());
                                                    user.put("cognome", utente.getCognome());
                                                    user.put("email", utente.getEmail());
                                                    user.put("password", utente.getPassword());

                                                    db.collection("users")
                                                            .document(utente.getEmail())
                                                            .set(user)
                                                            .addOnSuccessListener(task1 ->{
                                                                Bundle bundle = new Bundle();
                                                                bundle.putSerializable("email", utente.getEmail());
                                                                bundle.putSerializable("nome", utente.getNome());

                                                                progressBar.setVisibility(ProgressBar.INVISIBLE);

                                                                Intent intent = new Intent(getContext(), MainActivity.class);
                                                                intent.putExtras(bundle);
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



