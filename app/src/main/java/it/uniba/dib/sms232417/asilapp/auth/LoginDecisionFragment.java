package it.uniba.dib.sms232417.asilapp.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.auth.doctor.LoginDoctorCredentialFragment;
import it.uniba.dib.sms232417.asilapp.auth.patient.LoginFragment;
import it.uniba.dib.sms232417.asilapp.auth.patient.RegisterFragment;

public class LoginDecisionFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.login_decision_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialButton btnPatient = getView().findViewById(R.id.btnLoginPatient);
        MaterialButton btnDoctor = getView().findViewById(R.id.btnLoginDoctor);

        btnPatient.setOnClickListener(v -> {
            ((EntryActivity) getActivity()).replaceFragment(new LoginFragment());
        });

        btnDoctor.setOnClickListener(v -> {
            ((EntryActivity) getActivity()).replaceFragment(new LoginDoctorCredentialFragment());
        });
    }


}
