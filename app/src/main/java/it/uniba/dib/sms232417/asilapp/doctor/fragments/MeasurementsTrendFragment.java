package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.List;
import java.util.Locale;
import java.util.Random;

import it.uniba.dib.sms232417.asilapp.R;

public class MeasurementsTrendFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_measurements_trend, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

        LineDataSet dataSet = new LineDataSet(entries, "Heart rate");
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        String[] days = new String[10];
        for (int i = 0; i < 10; i++) {
            days[i] = sdf.format(calendar.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return days[(int) value];
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
