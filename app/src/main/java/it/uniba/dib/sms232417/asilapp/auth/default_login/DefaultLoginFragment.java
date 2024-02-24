package it.uniba.dib.sms232417.asilapp.auth.default_login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import it.uniba.dib.sms232417.asilapp.MainActivity;
import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterDoctor;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterPatient;
import it.uniba.dib.sms232417.asilapp.auth.EntryActivity;
import it.uniba.dib.sms232417.asilapp.auth.doctor.LoginDoctorCredentialFragment;
import it.uniba.dib.sms232417.asilapp.auth.patient.LoginFragment;
import it.uniba.dib.sms232417.asilapp.entity.Doctor;
import it.uniba.dib.sms232417.asilapp.entity.Patient;
import it.uniba.dib.sms232417.asilapp.interfaces.OnDoctorDataCallback;
import it.uniba.dib.sms232417.asilapp.interfaces.OnPatientDataCallback;

public class DefaultLoginFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.default_user_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialButton btnPatient = getView().findViewById(R.id.btnLoginPatient_default);
        MaterialButton btnDoctor = getView().findViewById(R.id.btnLoginDoctor_default);

        DatabaseAdapterDoctor dbAdapter = new DatabaseAdapterDoctor(getContext());
        DatabaseAdapterPatient dbAdapterPatient = new DatabaseAdapterPatient(getContext());

        btnPatient.setOnClickListener(v -> {
            //Paziente Marco Rubino
           dbAdapterPatient.onLogin("vitomarcorubino.universita@gmail.com","Asilapp!", new OnPatientDataCallback() {
               @Override
               public void onCallback(Patient patient) {
                   Intent intent = new Intent(getContext(), MainActivity.class);
                   intent.putExtra("loggedPatient", (Parcelable) patient);
                   startActivity(intent);
               }

               @Override
               public void onCallbackError(Exception exception, String message) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
               }
           });


        });

        btnDoctor.setOnClickListener(v -> {
            //Dottore Marco Rubino
            dbAdapter.getDoctor("MXgaKjAin1Y00PO2FRExsWLftF83", new OnDoctorDataCallback() {
                @Override
                public void onCallback(Doctor doctor) {

                    Log.d("DefaultLoginFragment", "onCallback: " + doctor.toString());
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra("loggedDoctor", (Parcelable) doctor);
                    startActivity(intent);
                }

                @Override
                public void onCallbackError(Exception exception, String message) {

                }
            });

        });

    }
}
