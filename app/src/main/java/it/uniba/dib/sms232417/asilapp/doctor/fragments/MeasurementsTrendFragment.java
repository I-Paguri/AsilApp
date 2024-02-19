package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import it.uniba.dib.sms232417.asilapp.R;

public class MeasurementsTrendFragment extends Fragment {
    private String patientUUID;
    private String patientName;
    private String patientAge;
    private String measureType;
    private String user; // Type of user: "patient" or "doctor"

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_measurements_trend, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        patientUUID = "";
        patientName = "";
        patientAge = "";
        user = ""; // Type of user: "patient" or "doctor"
        measureType = ""; // Type of measure: "heartRate", "bloodPressure", "temperature", "glycemia"

        if (this.getArguments() != null) {
            patientUUID = this.getArguments().getString("patientUUID");
            patientName = this.getArguments().getString("patientName");
            patientAge = this.getArguments().getString("patientAge");
            user = this.getArguments().getString("user");
            if (user == null) {
                user = "";
            }
            measureType = this.getArguments().getString("measureType");
        }

        TextView txtMeasurementType = view.findViewById(R.id.txtMeasurementType);
        if (!measureType.isEmpty()) {
            switch (measureType) {
                case "heartRate":
                    txtMeasurementType.setText(getResources().getString(R.string.heart_rate));
                    break;
                case "bloodPressure":
                    txtMeasurementType.setText(getResources().getString(R.string.blood_pressure));
                    break;
                case "temperature":
                    txtMeasurementType.setText(getResources().getString(R.string.temperature));
                    break;
                case "glycemia":
                    txtMeasurementType.setText(getResources().getString(R.string.glycemia));
                    break;
                default:
                    txtMeasurementType.setText("");
                    break;
            }
        }

        Typeface ember_regular = ResourcesCompat.getFont(getContext(), R.font.ember_regular);

        LineChart lineChart = view.findViewById(R.id.lineChart);
        //lineChart.setBackgroundColor(getResources().getColor(R.color.md_theme_light_surface));
        lineChart.setDescription(null);


        // Generate random data
        Random random = new Random();
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            float value = 50 + random.nextFloat() * (120 - 50);
            entries.add(new Entry(i, value));
        }

        LineDataSet dataSet = new LineDataSet(entries, getResources().getString(R.string.heart_rate));
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setColor(getResources().getColor(R.color.md_theme_light_primary));
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTypeface(ember_regular);
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Format x-axis to display specific days
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        // Create a list of 10 days
        List<Date> days = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            days.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Convert days to string
        String[] daysString = new String[days.size() + 1];
        int i;
        for (i = 0; i < days.size(); i++) {
            daysString[i] = sdf.format(days.get(i));
        }

        daysString[i] = sdf.format(days.get(i-1));

        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return daysString[(int) value];
            }
        };

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTypeface(ember_regular);
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setLabelRotationAngle(-45);
        xAxis.setValueFormatter(formatter);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTypeface(ember_regular);
        YAxis rightAxis = lineChart.getAxisRight();
        leftAxis.setDrawGridLines(false); // Hide grid lines on the left Y-axis
        rightAxis.setDrawGridLines(false); // Hide grid lines on the right Y-axis
        rightAxis.setEnabled(false); // Disable the right Y-axis

        lineChart.invalidate(); // refresh


    }
}
