package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import it.uniba.dib.sms232417.asilapp.MainActivity;
import it.uniba.dib.sms232417.asilapp.R;

public class MyAccountFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_my_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle loggedUsers = getArguments();
        TextView name = getView().findViewById(R.id.txt_my_account);

        if (loggedUsers != null) {
            name.setText("Bentornato " + (String) loggedUsers.get("nome") + "\nQuesta è la tua area personale");
        } else {
             name.setText("Utente non autenticato");
        }

    }
}