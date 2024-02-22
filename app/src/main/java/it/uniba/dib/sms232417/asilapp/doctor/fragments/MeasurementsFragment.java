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
import android.view.animation.Animation;

import com.ekn.gruzer.gaugelibrary.ArcGauge;
import com.ekn.gruzer.gaugelibrary.MultiGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.google.android.material.card.MaterialCardView;
import com.mackhartley.roundedprogressbar.ProgressTextFormatter;
import com.mackhartley.roundedprogressbar.RoundedProgressBar;

import java.util.ArrayList;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterUser;
import it.uniba.dib.sms232417.asilapp.interfaces.OnGetValueFromDBInterface;
import it.uniba.dib.sms232417.asilapp.entity.vitals.BloodPressure;
import it.uniba.dib.sms232417.asilapp.entity.vitals.Glycemia;
import it.uniba.dib.sms232417.asilapp.entity.vitals.HeartRate;
import it.uniba.dib.sms232417.asilapp.entity.vitals.Temperature;


public class MeasurementsFragment extends Fragment {

    private String patientUUID;
    private String patientName;
    private String patientAge;
    private String user; // Type of user: "patient" or "doctor"
    private ArcGauge heartRateArchGauge, glycemiaArchGauge;
    private MultiGauge pressureMultiGauge;
    private MaterialCardView cardViewHeartRate, cardViewPressure, cardViewTemperature, cardViewGlycemia;

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


        // Getting vitals
        DatabaseAdapterUser dbAdapterUser = new DatabaseAdapterUser(requireContext());

        // HEART RATE
        dbAdapterUser.getVitals(patientUUID, "heart_rate", new OnGetValueFromDBInterface() {
            @Override
            public void onCallback(ArrayList<?> listOfValue) {
                ArrayList<HeartRate> heartRates = (ArrayList<HeartRate>) listOfValue;
                if (heartRates.size() > 0) {
                    HeartRate lastHeartRate = heartRates.get(heartRates.size() - 1);

                    heartRateArchGauge = view.findViewById(R.id.arcGaugeHeartRate);

                    Range range = new Range();
                    range.setColor(Color.parseColor("#9c4146"));
                    range.setFrom(40.0);
                    range.setTo(60.0);

                    Range range2 = new Range();
                    range2.setColor(Color.parseColor("#00B20B"));
                    range2.setFrom(60.0);
                    range2.setTo(100.0);

                    Range range3 = new Range();
                    range3.setColor(Color.parseColor("#E3E500"));
                    range3.setFrom(100.0);
                    range3.setTo(140.0);

                    Range range4 = new Range();
                    range4.setColor(Color.parseColor("#9c4146"));
                    range4.setFrom(140.0);
                    range4.setTo(200.0);

                    heartRateArchGauge.setMinValue(40.0);
                    heartRateArchGauge.setMaxValue(200.0);
                    heartRateArchGauge.setValue(lastHeartRate.getValue());


                    //add color ranges to gauge
                    heartRateArchGauge.addRange(range);
                    heartRateArchGauge.addRange(range2);
                    heartRateArchGauge.addRange(range3);
                    heartRateArchGauge.addRange(range4);
                }
            }

            @Override
            public void onCallbackError(Exception exception, String message) {
                Log.d("measurementFragment", "does not exists");
            }
        });

        // BLOOD PRESSURE
        dbAdapterUser.getVitals(patientUUID, "blood_pressure", new OnGetValueFromDBInterface() {
            @Override
            public void onCallback(ArrayList<?> listOfValue) {
                ArrayList<BloodPressure> bloodPressures = (ArrayList<BloodPressure>) listOfValue;
                if (bloodPressures.size() > 0) {
                    BloodPressure lastBloodPressure = bloodPressures.get(bloodPressures.size() - 1);

                    pressureMultiGauge = view.findViewById(R.id.multiGaugePressure);

                    // Minimum pressure
                    Range minRange = new Range();
                    minRange.setColor(Color.parseColor("#006781"));
                    minRange.setFrom(40);
                    minRange.setTo(90);

                    pressureMultiGauge.setMinValue(40);
                    pressureMultiGauge.setMaxValue(90);
                    pressureMultiGauge.addRange(minRange);
                    pressureMultiGauge.setValue(bloodPressures.get(bloodPressures.size() - 1).getDiastolic());

                    // Maximum pressure
                    Range maxRange = new Range();
                    maxRange.setColor(Color.parseColor("#9c4146"));
                    maxRange.setFrom(90);
                    maxRange.setTo(140);
                    pressureMultiGauge.addSecondRange(maxRange);
                    pressureMultiGauge.setSecondMinValue(70);
                    pressureMultiGauge.setSecondMaxValue(180);
                    pressureMultiGauge.setSecondValue(bloodPressures.get(bloodPressures.size() - 1).getSystolic());


                    // Third range does not matter, it is just to fill the gauge
                    Range rangeWhite = new Range();
                    rangeWhite.setColor(Color.parseColor("#ffffff"));
                    rangeWhite.setFrom(1);
                    rangeWhite.setTo(100);

                    pressureMultiGauge.addThirdRange(rangeWhite);
                    pressureMultiGauge.setThirdMinValue(1);
                    pressureMultiGauge.setThirdMaxValue(100);
                    pressureMultiGauge.setThirdValue(100);
                }
            }

            @Override
            public void onCallbackError(Exception exception, String message) {

            }
        });

        // TEMPERATURE
        dbAdapterUser.getVitals(patientUUID, "temperature", new OnGetValueFromDBInterface() {
            @Override
            public void onCallback(ArrayList<?> listOfValue) {
                ArrayList<Temperature> temperatures = (ArrayList<Temperature>) listOfValue;
                if (temperatures.size() > 0) {
                    Temperature lastTemperature = temperatures.get(temperatures.size() - 1);

                    RoundedProgressBar thermometer = view.findViewById(R.id.thermometer);

                    // Define the temperature outside the ProgressTextFormatter
                    double temperature = lastTemperature.getValue();

                    thermometer.setProgressTextFormatter(new ProgressTextFormatter() {
                        @NonNull
                        @Override
                        public String getProgressText(float v) {
                            // Return the temperature as the progress text
                            return String.format("%.1f °C", temperature);
                        }

                        @NonNull
                        @Override
                        public String getMinWidthString() {
                            return "42.9 °C";
                        }
                    });

                    // Calculate the progress percentage based on the temperature
                    // The temperature range is 34 - 42
                    // The progress percentage range is 0 - 100

                    int minTemperature = 34;
                    int maxTemperature = 42;
                    double progressPercentage = (temperature - minTemperature) * 100 / (maxTemperature - minTemperature);

                    // Ensure progressPercentage is within [0, 100]
                    progressPercentage = Math.max(0, Math.min(100, progressPercentage));

                    thermometer.setProgressPercentage(progressPercentage, true);
                }
            }

            @Override
            public void onCallbackError(Exception exception, String message) {

            }
        });

        // GLYCEMIA
        dbAdapterUser.getVitals(patientUUID, "glycemia", new OnGetValueFromDBInterface() {
            @Override
            public void onCallback(ArrayList<?> listOfValue) {
                ArrayList<Glycemia> glycemias = (ArrayList<Glycemia>) listOfValue;
                if (glycemias.size() > 0) {
                    Glycemia lastGlycemia = glycemias.get(glycemias.size() - 1);

                    glycemiaArchGauge = view.findViewById(R.id.arcGaugeGlycemia);

                    Range rangeGreen = new Range();
                    rangeGreen.setColor(Color.parseColor("#00B20B"));
                    rangeGreen.setFrom(70.0);
                    rangeGreen.setTo(100);

                    Range rangeYellow = new Range();
                    rangeYellow.setColor(Color.parseColor("#E3E500"));
                    rangeYellow.setFrom(100.0);
                    rangeYellow.setTo(140);

                    Range rangeRed = new Range();
                    rangeRed.setColor(Color.parseColor("#9c4146"));
                    rangeRed.setFrom(140.0);
                    rangeRed.setTo(220);

                    Range rangeRed2 = new Range();
                    rangeRed2.setColor(Color.parseColor("#9c4146"));
                    rangeRed2.setFrom(50.0);
                    rangeRed2.setTo(70);

                    glycemiaArchGauge.addRange(rangeGreen);
                    glycemiaArchGauge.addRange(rangeYellow);
                    glycemiaArchGauge.addRange(rangeRed);
                    glycemiaArchGauge.addRange(rangeRed2);

                    glycemiaArchGauge.setMinValue(50.0);
                    glycemiaArchGauge.setMaxValue(220.0);
                    glycemiaArchGauge.setValue(lastGlycemia.getGlycemia());
                }
            }

            @Override
            public void onCallbackError(Exception exception, String message) {

            }
        });

        Bundle bundle = new Bundle();
        bundle.putString("patientUUID", patientUUID);
        bundle.putString("patientName", patientName);
        bundle.putString("patientAge", patientAge);
        bundle.putString("user", user);

        cardViewHeartRate = view.findViewById(R.id.cardViewHeartRate);
        cardViewHeartRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("measureType", "heart_rate");

                MeasurementsTrendFragment measurementsTrendFragment = new MeasurementsTrendFragment();
                measurementsTrendFragment.setArguments(bundle);
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, measurementsTrendFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        cardViewTemperature = view.findViewById(R.id.cardViewTemperature);
        cardViewTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("measureType", "temperature");

                MeasurementsTrendFragment measurementsTrendFragment = new MeasurementsTrendFragment();
                measurementsTrendFragment.setArguments(bundle);
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, measurementsTrendFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        cardViewPressure = view.findViewById(R.id.cardViewBloodPressure);
        cardViewPressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("measureType", "blood_pressure");

                MeasurementsTrendFragment measurementsTrendFragment = new MeasurementsTrendFragment();
                measurementsTrendFragment.setArguments(bundle);
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, measurementsTrendFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        cardViewGlycemia = view.findViewById(R.id.cardViewGlycemia);
        cardViewGlycemia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("measureType", "glycemia");

                MeasurementsTrendFragment measurementsTrendFragment = new MeasurementsTrendFragment();
                measurementsTrendFragment.setArguments(bundle);
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, measurementsTrendFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

    }


}