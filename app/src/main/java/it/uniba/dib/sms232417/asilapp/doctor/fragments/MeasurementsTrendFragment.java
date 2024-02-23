package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterUser;
import it.uniba.dib.sms232417.asilapp.entity.AsylumHouse;
import it.uniba.dib.sms232417.asilapp.entity.vitals.BloodPressure;
import it.uniba.dib.sms232417.asilapp.entity.vitals.Glycemia;
import it.uniba.dib.sms232417.asilapp.entity.vitals.HeartRate;
import it.uniba.dib.sms232417.asilapp.entity.vitals.Temperature;
import it.uniba.dib.sms232417.asilapp.interfaces.OnAsylumHouseDataCallback;
import it.uniba.dib.sms232417.asilapp.interfaces.OnAsylumHouseRatingCallback;
import it.uniba.dib.sms232417.asilapp.utilities.StringUtils;

public class MeasurementsTrendFragment extends Fragment {
    private String patientUUID;
    private String patientName;
    private String patientAge;
    private String measureType;
    private String user; // Type of user: "patient" or "doctor"
    private int numberOfMeasures;
    private ArrayList<HeartRate> heartRates;
    private ArrayList<BloodPressure> bloodPressures;
    private ArrayList<Temperature> temperatures;
    private ArrayList<Glycemia> glycemias;

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

        bloodPressures = null;
        heartRates = null;
        temperatures = null;
        glycemias = null;

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
        String formattedMeasurementType = "";
        if (!measureType.isEmpty()) {
            switch (measureType) {
                case "heart_rate":
                    formattedMeasurementType = getResources().getString(R.string.heart_rate);
                    heartRates = this.getArguments().getParcelableArrayList("heartRates");
                    break;
                case "blood_pressure":
                    formattedMeasurementType = getResources().getString(R.string.blood_pressure);
                    bloodPressures = this.getArguments().getParcelableArrayList("bloodPressures");
                    break;
                case "temperature":
                    formattedMeasurementType = getResources().getString(R.string.temperature);
                    temperatures = this.getArguments().getParcelableArrayList("temperatures");
                    break;
                case "glycemia":
                    formattedMeasurementType = getResources().getString(R.string.glycemia);
                    glycemias = this.getArguments().getParcelableArrayList("glycemias");
                    break;
                default:
                    txtMeasurementType.setText("");
                    break;
            }
        }

        txtMeasurementType.setText(formattedMeasurementType);

        Typeface ember_regular = ResourcesCompat.getFont(getContext(), R.font.ember_regular);

        LineChart lineChart = view.findViewById(R.id.lineChart);
        //lineChart.setBackgroundColor(getResources().getColor(R.color.md_theme_light_surface));
        lineChart.setDescription(null);

        numberOfMeasures = 8;

        if (!measureType.equals("blood_pressure")) {
            // Single line chart for heart rate, temperature and glycemia

            // Generate random data
            Random random = new Random();
            List<Entry> entries = new ArrayList<>();
            for (int i = 0; i < numberOfMeasures; i++) {
                float value = 50 + random.nextFloat() * (120 - 50);
                entries.add(new Entry(i, value));
            }

            LineDataSet dataSet = new LineDataSet(entries, formattedMeasurementType);
            dataSet.setLineWidth(3f);
            dataSet.setCircleRadius(4f);
            dataSet.setDrawCircleHole(false);
            dataSet.setColor(getResources().getColor(R.color.md_theme_light_primary));
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            dataSet.setValueTextSize(10f);
            dataSet.setValueTypeface(ember_regular);
            LineData lineData = new LineData(dataSet);
            lineChart.setData(lineData);
        } else {

            // Generate random data for minimum pressure
            Random randomMin = new Random();
            List<Entry> entriesMin = new ArrayList<>();
            for (int i = 0; i < numberOfMeasures; i++) {
                float value = 50 + randomMin.nextFloat() * (80 - 50);
                entriesMin.add(new Entry(i, value));
            }

            // Generate random data for maximum pressure
            Random randomMax = new Random();
            List<Entry> entriesMax = new ArrayList<>();
            for (int i = 0; i < numberOfMeasures; i++) {
                float value = 80 + randomMax.nextFloat() * (120 - 80);
                entriesMax.add(new Entry(i, value));
            }

            // Create LineDataSet for minimum pressure
            LineDataSet dataSetMin = new LineDataSet(entriesMin, getResources().getString(R.string.diastolic_pressure) + " (mmHg)");
            dataSetMin.setLineWidth(3f);
            dataSetMin.setCircleRadius(4f);
            dataSetMin.setDrawCircleHole(false);
            dataSetMin.setColor(getResources().getColor(R.color.md_theme_light_primary));
            dataSetMin.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            dataSetMin.setValueTextSize(10f);
            dataSetMin.setValueTypeface(ember_regular);

            // Create LineDataSet for maximum pressure
            LineDataSet dataSetMax = new LineDataSet(entriesMax, getResources().getString(R.string.systolic_pressure) + " (mmHg)");
            dataSetMax.setLineWidth(3f);
            dataSetMax.setCircleRadius(4f);
            dataSetMax.setDrawCircleHole(false);
            dataSetMax.setColor(getResources().getColor(R.color.pastel_red));
            dataSetMax.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            dataSetMax.setValueTextSize(10f);
            dataSetMax.setValueTypeface(ember_regular);

            // Add both data sets to LineData
            LineData lineData = new LineData(dataSetMin, dataSetMax);
            lineChart.setData(lineData);
        }

        // Format x-axis to display specific days
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();

        // Create a list of 10 days
        List<Date> days = new ArrayList<>();
        for (int i = 0; i < numberOfMeasures; i++) {
            days.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Convert days to string
        String[] daysString = new String[days.size()];
        int i;
        for (i = 0; i < days.size(); i++) {
            daysString[i] = sdf.format(days.get(i));
        }

        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index < 0 || index >= daysString.length) {
                    return ""; // return default value
                }
                return daysString[index];
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
