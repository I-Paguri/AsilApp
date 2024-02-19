package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ekn.gruzer.gaugelibrary.ArcGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.google.android.material.card.MaterialCardView;
import com.mackhartley.roundedprogressbar.ProgressTextFormatter;
import com.mackhartley.roundedprogressbar.RoundedProgressBar;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.patientsFragments.AnalysesExpensesFragment;


public class MeasurementsFragment extends Fragment {

    private String patientUUID;
    private String patientName;
    private String patientAge;
    private String user; // Type of user: "patient" or "doctor"
    private ArcGauge heartRateArchGauge, pressureArchGauge, lightArchGauge;
    private MaterialCardView cardViewHeartRate, cardViewPressure, cardViewTemperature, cardViewLight;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_measurements, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        patientUUID = "";
        patientName = "";
        patientAge = "";
        user = ""; // Type of user: "patient" or "doctor"

        if (this.getArguments() != null) {
            patientUUID = this.getArguments().getString("patientUUID");
            patientName = this.getArguments().getString("patientName");
            patientAge = this.getArguments().getString("patientAge");
            user = this.getArguments().getString("user");
            if (user == null) {
                user = "";
            }
        }

        //get the gauge view
        heartRateArchGauge = view.findViewById(R.id.arcGaugeHeartRate);
        pressureArchGauge = view.findViewById(R.id.arcGaugePressure);
        lightArchGauge = view.findViewById(R.id.arcGaugeLight);


        Range range = new Range();
        range.setColor(Color.parseColor("#ce0000"));
        range.setFrom(40.0);
        range.setTo(60.0);

        Range range2 = new Range();
        range2.setColor(Color.parseColor("#E3E500"));
        range2.setFrom(60.0);
        range2.setTo(100.0);

        Range range3 = new Range();
        range3.setColor(Color.parseColor("#00b20b"));
        range3.setFrom(100.0);
        range3.setTo(200.0);

        //add color ranges to gauge
        heartRateArchGauge.addRange(range);
        heartRateArchGauge.addRange(range2);
        heartRateArchGauge.addRange(range3);

        pressureArchGauge.addRange(range);
        pressureArchGauge.addRange(range2);
        pressureArchGauge.addRange(range3);

        lightArchGauge.addRange(range);
        lightArchGauge.addRange(range2);
        lightArchGauge.addRange(range3);

        //set min max and current temperature
        heartRateArchGauge.setMinValue(40.0);
        heartRateArchGauge.setMaxValue(200.0);
        heartRateArchGauge.setValue(80.0);

        pressureArchGauge.setMinValue(40.0);
        pressureArchGauge.setMaxValue(200.0);
        pressureArchGauge.setValue(50.0);

        lightArchGauge.setMinValue(0.0);
        lightArchGauge.setMaxValue(100.0);
        lightArchGauge.setValue(150.0);


        RoundedProgressBar thermometer = view.findViewById(R.id.thermometer);

        // Define the temperature outside the ProgressTextFormatter
        float temperature = 36; // the temperature you want to map

        thermometer.setProgressTextFormatter(new ProgressTextFormatter() {
            @NonNull
            @Override
            public String getProgressText(float v) {
                // Return the temperature as the progress text
                return (int) temperature + " °C";
            }

            @NonNull
            @Override
            public String getMinWidthString() {
                return "100 °C";
            }
        });

        // Calculate the progress percentage based on the temperature
        // The temperature range is 34 - 42
        // The progress percentage range is 0 - 100

        int minTemperature = 34;
        int maxTemperature = 42;
        float progressPercentage = (temperature - minTemperature) * 100 / (maxTemperature - minTemperature);

        // Ensure progressPercentage is within [0, 100]
        progressPercentage = Math.max(0, Math.min(100, progressPercentage));

        thermometer.setProgressPercentage(progressPercentage, true);


        cardViewHeartRate = view.findViewById(R.id.cardViewHeartRate);
        cardViewHeartRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeasurementsTrendFragment measurementsTrendFragment = new MeasurementsTrendFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, measurementsTrendFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

    }


}