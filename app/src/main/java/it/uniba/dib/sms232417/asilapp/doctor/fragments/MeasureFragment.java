package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.auth.QRCodeAuth;

public class MeasureFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_measure, container, false);
        Bundle qrData = getArguments();
        if (qrData != null) {
            String qrCode = qrData.getString("RisultatoQR");
            TextView dataQR = view.findViewById(R.id.last_measure_data);
            dataQR.setText(qrCode);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnMeasure = view.findViewById(R.id.btnMeasure);
        btnMeasure.setOnClickListener(v -> {
            onMeasureClick();
        });
    }

    private void onMeasureClick() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.nav_host_fragment_activity_main, new QRCodeAuth()).commit();
    }
}
