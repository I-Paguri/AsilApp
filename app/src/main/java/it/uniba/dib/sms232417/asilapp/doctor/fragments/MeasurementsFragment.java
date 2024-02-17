package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ekn.gruzer.gaugelibrary.ArcGauge;
import com.ekn.gruzer.gaugelibrary.Range;

import it.uniba.dib.sms232417.asilapp.R;


public class MeasurementsFragment extends Fragment {
    private ArcGauge heartRateArchGauge, pressureArchGauge, lightArchGauge;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_measurements, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        //set min max and current value
        heartRateArchGauge.setMinValue(40.0);
        heartRateArchGauge.setMaxValue(200.0);
        heartRateArchGauge.setValue(80.0);

        pressureArchGauge.setMinValue(40.0);
        pressureArchGauge.setMaxValue(200.0);
        pressureArchGauge.setValue(50.0);

        lightArchGauge.setMinValue(0.0);
        lightArchGauge.setMaxValue(100.0);
        lightArchGauge.setValue(150.0);
    }


}