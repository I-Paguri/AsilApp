package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    float density;
    private MaterialCardView cardViewHeartRate, cardViewBloodPressure, cardViewTemperature, cardViewGlycemia;
    private ArrayList<HeartRate> heartRates;
    private ArrayList<BloodPressure> bloodPressures;
    private ArrayList<Temperature> temperatures;
    private ArrayList<Glycemia> glycemias;
    private int noVitals;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_measurements, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int i;
        int numberOfVitals = 4;

        noVitals = 0;

        bloodPressures = null;
        heartRates = null;
        temperatures = null;
        glycemias = null;

        density = getResources().getDisplayMetrics().density;

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

        cardViewHeartRate = view.findViewById(R.id.cardViewHeartRate);
        cardViewBloodPressure = view.findViewById(R.id.cardViewBloodPressure);
        cardViewTemperature = view.findViewById(R.id.cardViewTemperature);
        cardViewGlycemia = view.findViewById(R.id.cardViewGlycemia);


        // Getting vitals
        DatabaseAdapterUser dbAdapterUser = new DatabaseAdapterUser(requireContext());

        // HEART RATE
        dbAdapterUser.getVitals(patientUUID, "heart_rate", new OnGetValueFromDBInterface() {
            @Override
            public void onCallback(ArrayList<?> listOfValue) {
                heartRates = (ArrayList<HeartRate>) listOfValue;
                TextView heartRateDesc = view.findViewById(R.id.heartRateDesc);
                TextView heartRateDate = view.findViewById(R.id.heartRateDate);
                heartRateArchGauge = view.findViewById(R.id.arcGaugeHeartRate);
                if (heartRates.size() > 0) {
                    cardViewHeartRate.setVisibility(View.VISIBLE);

                    HeartRate lastHeartRate = heartRates.get(heartRates.size() - 1);


                    heartRateArchGauge.setValueColor(Color.parseColor("#FFFFFF"));

                    Range rangeRed1 = new Range();
                    rangeRed1.setColor(Color.parseColor("#9c4146"));
                    rangeRed1.setFrom(40.0);
                    rangeRed1.setTo(60.0);

                    Range rangeGreen = new Range();
                    rangeGreen.setColor(Color.parseColor("#00B20B"));
                    rangeGreen.setFrom(60.0);
                    rangeGreen.setTo(100.0);

                    Range rangeYellow = new Range();
                    rangeYellow.setColor(Color.parseColor("#E3E500"));
                    rangeYellow.setFrom(100.0);
                    rangeYellow.setTo(140.0);

                    Range rangeRed2 = new Range();
                    rangeRed2.setColor(Color.parseColor("#9c4146"));
                    rangeRed2.setFrom(140.0);
                    rangeRed2.setTo(200.0);

                    //add color ranges to gauge
                    heartRateArchGauge.addRange(rangeRed1);
                    heartRateArchGauge.addRange(rangeGreen);
                    heartRateArchGauge.addRange(rangeYellow);
                    heartRateArchGauge.addRange(rangeRed2);

                    heartRateArchGauge.setMinValue(40.0);
                    heartRateArchGauge.setMaxValue(200.0);
                    heartRateArchGauge.setValue(lastHeartRate.getValue());

                    heartRateDesc.setText(lastHeartRate.getValue() + " bpm");
                    heartRateDate.setText(getResources().getString(R.string.on_date).toLowerCase() + " " + lastHeartRate.getStringDate());
                } else {
                    noVitals++;

                    cardViewHeartRate.setVisibility(View.GONE);

                    if (noVitals == numberOfVitals) {
                        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View noTreatmentLayout = inflater.inflate(R.layout.no_vitals_found, null);
                        TextView noVitalsFoundSubtitle = noTreatmentLayout.findViewById(R.id.noVitalsFoundSubtitle);

                        if (user != null && user.equals("doctor")) {
                            noVitalsFoundSubtitle.setVisibility(View.GONE);
                        }
                        // Add the inflated layout to the parent layout
                        LinearLayout parentLayout = view.findViewById(R.id.linearLayoutMeasurements);
                        parentLayout.addView(noTreatmentLayout);
                    } else {
                        // Create a new LinearLayout.LayoutParams object
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                        );


                        int dp8 = (int) (8 * density + 0.5f);
                        int dp20 = (int) (20 * density + 0.5f);

                        params.setMargins(dp20, 0, dp20, dp8);

                        cardViewBloodPressure.setLayoutParams(params);
                    }
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
                bloodPressures = (ArrayList<BloodPressure>) listOfValue;
                TextView bloodPressureDesc = view.findViewById(R.id.bloodPressureDesc);
                TextView bloodPressureDate = view.findViewById(R.id.bloodPressureDate);

                if (bloodPressures.size() > 0) {
                    cardViewBloodPressure.setVisibility(View.VISIBLE);
                    /*
                    Green: < 80, < 120
                    Yellow: < 90 < 140
                    Orange: < 120, < 180
                    Red: >= 120, >= 180
                     */
                    BloodPressure lastBloodPressure = bloodPressures.get(bloodPressures.size() - 1);

                    pressureMultiGauge = view.findViewById(R.id.multiGaugePressure);

                    // Maximum pressure
                    Range greenRangeMax = new Range();
                    greenRangeMax.setColor(Color.parseColor("#00B20B"));
                    greenRangeMax.setFrom(70);
                    greenRangeMax.setTo(120);

                    Range yellowRangeMax = new Range();
                    yellowRangeMax.setColor(Color.parseColor("#E3E500"));
                    yellowRangeMax.setFrom(121);
                    yellowRangeMax.setTo(140);

                    Range orangeRangeMax = new Range();
                    orangeRangeMax.setColor(Color.parseColor("#FFA500"));
                    orangeRangeMax.setFrom(141);
                    orangeRangeMax.setTo(180);

                    Range redRangeMax = new Range();
                    redRangeMax.setColor(Color.parseColor("#9c4146"));
                    redRangeMax.setFrom(181);
                    redRangeMax.setTo(200);

                    pressureMultiGauge.addRange(greenRangeMax);
                    pressureMultiGauge.addRange(yellowRangeMax);
                    pressureMultiGauge.addRange(orangeRangeMax);
                    pressureMultiGauge.addRange(redRangeMax);

                    pressureMultiGauge.setMinValue(70);
                    pressureMultiGauge.setMaxValue(190);
                    pressureMultiGauge.setValue(bloodPressures.get(bloodPressures.size() - 1).getSystolic());

                    // Minimum pressure
                    Range greenRangeMin = new Range();
                    greenRangeMin.setColor(Color.parseColor("#00B20B"));
                    greenRangeMin.setFrom(60);
                    greenRangeMin.setTo(80);

                    Range yellowRangeMin = new Range();
                    yellowRangeMin.setColor(Color.parseColor("#E3E500"));
                    yellowRangeMin.setFrom(81);
                    yellowRangeMin.setTo(90);

                    Range orangeRangeMin = new Range();
                    orangeRangeMin.setColor(Color.parseColor("#FFA500"));
                    orangeRangeMin.setFrom(91);
                    orangeRangeMin.setTo(120);

                    Range redRangeMin = new Range();
                    redRangeMin.setColor(Color.parseColor("#9c4146"));
                    redRangeMin.setFrom(121);
                    redRangeMin.setTo(130);

                    pressureMultiGauge.addSecondRange(greenRangeMin);
                    pressureMultiGauge.addSecondRange(yellowRangeMin);
                    pressureMultiGauge.addSecondRange(orangeRangeMin);
                    pressureMultiGauge.addSecondRange(redRangeMin);

                    pressureMultiGauge.setSecondMinValue(50);
                    pressureMultiGauge.setSecondMaxValue(130);
                    pressureMultiGauge.setSecondValue(bloodPressures.get(bloodPressures.size() - 1).getDiastolic());


                    bloodPressureDesc.setText(lastBloodPressure.getSystolic() + "/" + lastBloodPressure.getDiastolic() + " mmHg");
                    bloodPressureDate.setText(getResources().getString(R.string.on_date).toLowerCase() + " " + lastBloodPressure.getStringDate());

                    // Third range does not matter, it is just to fill the gauge
                    Range rangeWhite = new Range();
                    rangeWhite.setColor(Color.parseColor("#ffffff"));
                    rangeWhite.setFrom(1);
                    rangeWhite.setTo(100);

                    pressureMultiGauge.addThirdRange(rangeWhite);
                    pressureMultiGauge.setThirdMinValue(1);
                    pressureMultiGauge.setThirdMaxValue(100);
                    pressureMultiGauge.setThirdValue(100);

                } else {
                    noVitals++;

                    cardViewBloodPressure.setVisibility(View.GONE);

                    if (noVitals == numberOfVitals) {
                        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View noTreatmentLayout = inflater.inflate(R.layout.no_vitals_found, null);
                        TextView noVitalsFoundSubtitle = noTreatmentLayout.findViewById(R.id.noVitalsFoundSubtitle);

                        if (user != null && user.equals("doctor")) {
                            noVitalsFoundSubtitle.setVisibility(View.GONE);
                        }
                        // Add the inflated layout to the parent layout
                        LinearLayout parentLayout = view.findViewById(R.id.linearLayoutMeasurements);
                        parentLayout.addView(noTreatmentLayout);
                    } else {
                        // Create a new LinearLayout.LayoutParams object
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                        );


                        int dp8 = (int) (8 * density + 0.5f);
                        int dp20 = (int) (20 * density + 0.5f);

                        params.setMargins(dp20, 0, dp20, dp8);

                        cardViewHeartRate.setLayoutParams(params);
                    }
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
                temperatures = (ArrayList<Temperature>) listOfValue;
                TextView temperatureDesc = view.findViewById(R.id.temperatureDesc);
                TextView temperatureDate = view.findViewById(R.id.temperatureDate);
                if (temperatures.size() > 0) {
                    cardViewTemperature.setVisibility(View.VISIBLE);

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

                    temperatureDate.setText(getResources().getString(R.string.on_date).toLowerCase() + " " + lastTemperature.getStringDate());
                } else {
                    noVitals++;
                    cardViewTemperature.setVisibility(View.GONE);
                    if (noVitals == numberOfVitals) {
                        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View noTreatmentLayout = inflater.inflate(R.layout.no_vitals_found, null);
                        TextView noVitalsFoundSubtitle = noTreatmentLayout.findViewById(R.id.noVitalsFoundSubtitle);

                        if (user != null && user.equals("doctor")) {
                            noVitalsFoundSubtitle.setVisibility(View.GONE);
                        }
                        // Add the inflated layout to the parent layout
                        LinearLayout parentLayout = view.findViewById(R.id.linearLayoutMeasurements);
                        parentLayout.addView(noTreatmentLayout);
                    } else {
                        // Create a new LinearLayout.LayoutParams object
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                        );


                        int dp8 = (int) (8 * density + 0.5f);
                        int dp20 = (int) (20 * density + 0.5f);

                        params.setMargins(dp20, 0, dp20, dp8);

                        cardViewGlycemia.setLayoutParams(params);
                    }
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
                glycemias = (ArrayList<Glycemia>) listOfValue;
                TextView glycemiaDesc = view.findViewById(R.id.glycemiaDesc);
                TextView glycemiaDate = view.findViewById(R.id.glycemiaDate);
                if (glycemias.size() > 0) {
                    cardViewGlycemia.setVisibility(View.VISIBLE);

                    Glycemia lastGlycemia = glycemias.get(glycemias.size() - 1);

                    glycemiaArchGauge = view.findViewById(R.id.arcGaugeGlycemia);

                    glycemiaArchGauge.setValueColor(Color.parseColor("#FFFFFF"));

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

                    glycemiaDesc.setText(lastGlycemia.getGlycemia() + " mg/dL");
                    glycemiaDate.setText(getResources().getString(R.string.on_date).toLowerCase() + " " + lastGlycemia.getStringDate());
                } else {
                    noVitals++;
                    cardViewGlycemia.setVisibility(View.GONE);

                    if (noVitals == numberOfVitals) {
                        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View noTreatmentLayout = inflater.inflate(R.layout.no_vitals_found, null);
                        TextView noVitalsFoundSubtitle = noTreatmentLayout.findViewById(R.id.noVitalsFoundSubtitle);

                        if (user != null && user.equals("doctor")) {
                            noVitalsFoundSubtitle.setVisibility(View.GONE);
                        }

                        // Add the inflated layout to the parent layout
                        LinearLayout parentLayout = view.findViewById(R.id.linearLayoutMeasurements);
                        parentLayout.addView(noTreatmentLayout);
                    } else {
                        // Create a new LinearLayout.LayoutParams object
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT
                        );


                        int dp8 = (int) (8*density + 0.5f);
                        int dp20 = (int) (20*density + 0.5f);

                        params.setMargins(dp20, 0, dp20, dp8);

                        cardViewTemperature.setLayoutParams(params);
                    }
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
                bundle.putParcelableArrayList("heartRates", heartRates);

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

                bundle.putParcelableArrayList("temperatures", temperatures);

                MeasurementsTrendFragment measurementsTrendFragment = new MeasurementsTrendFragment();
                measurementsTrendFragment.setArguments(bundle);
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, measurementsTrendFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        cardViewBloodPressure = view.findViewById(R.id.cardViewBloodPressure);
        cardViewBloodPressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putString("measureType", "blood_pressure");

                bundle.putParcelableArrayList("bloodPressures", bloodPressures);

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

                bundle.putParcelableArrayList("glycemias", glycemias);

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