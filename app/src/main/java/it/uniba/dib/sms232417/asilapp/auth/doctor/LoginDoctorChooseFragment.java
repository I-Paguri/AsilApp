package it.uniba.dib.sms232417.asilapp.auth.doctor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.button.MaterialButton;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.auth.EntryActivity;

public class LoginDoctorChooseFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.doctor_login_choose_login_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialButton loginButton = view.findViewById(R.id.btnLoginDoctorCredentials);
        MaterialButton qrButton = view.findViewById(R.id.btnLoginDoctorQrCode);
        TextView register = view.findViewById(R.id.lbl_create_account);
        ImageView imageView = view.findViewById(R.id.backArrow);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EntryActivity) getActivity()).replaceFragment(new LoginDoctorCredentialFragment());
            }
        });
        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EntryActivity) getActivity()).replaceFragment(new LoginDoctorQrCodeFragment());
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EntryActivity) getActivity()).replaceFragment(new RegistrationDoctorFragment());
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EntryActivity.class);
                startActivity(intent);
            }
        });
    }
}
