package it.uniba.dib.sms232417.asilapp.patientsFragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.PieDataSet;

import com.google.android.material.card.MaterialCardView;
import com.mancj.slimchart.SlimChart;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms232417.asilapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnalysesExpensesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnalysesExpensesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AnalysesExpensesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AnalysesExpensesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnalysesExpensesFragment newInstance(String param1, String param2) {
        AnalysesExpensesFragment fragment = new AnalysesExpensesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_analyses_expenses, container, false);


        // Prendo i riferimenti alle textview e al grafico a torta
            PieChart pieChart = view.findViewById(R.id.pieChart);
            TextView paragraph1 = view.findViewById(R.id.resocontoSpesaFarmaci);
            TextView paragraph2 = view.findViewById(R.id.resocontoSpesaTerapie);
            TextView paragraph3 = view.findViewById(R.id.resocontoSpesaTrattamenti);
            TextView paragraph4 = view.findViewById(R.id.resocontoSpesaEsami);



        // Creazione grafico a torta
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(50, "Spese Farmaci"));
        entries.add(new PieEntry(20, "Spese Terapie"));
        entries.add(new PieEntry(30, "Spese Trattamenti"));
        entries.add(new PieEntry(29, "Spese Esami"));

        final float[] totalSum = new float[1];
        for (PieEntry entry : entries) {
            totalSum[0] += entry.getValue();
        }

      //definisco il dataset del grafico a torta
        PieDataSet set = new PieDataSet(entries, "");
        // Definisci i colori del grafico a torta
        int color1 = Color.parseColor("#D8E2FF");
        int color2 = Color.parseColor("#9CCAFF");
        int color3 = Color.parseColor("#003256");
        int color4 = Color.parseColor("#0062A1");

        //Imposta i colori delle fette del grafico a torta
        set.setColors(new int[] {color1, color2, color3, color4});


        PieData data = new PieData(set);
        data.setDrawValues(false);
        pieChart.setData(data);
        pieChart.setDrawEntryLabels(false);

        pieChart.setCenterText("Totale: " + Float.toString(totalSum[0]));


        // Get the data from the PieChart
        PieData pieData = pieChart.getData();
        if (pieData != null) {
            PieDataSet pieDataSet = (PieDataSet) pieData.getDataSet();
            if (pieDataSet != null) {
                 entries = pieDataSet.getEntriesForXValue(0f);
                // Set the text of each TextView based on the data of the PieChart
                if (entries.size() >= 0) {
                    paragraph1.setText(formatEntry(entries.get(0)));
                    paragraph2.setText(formatEntry(entries.get(1)));
                    paragraph3.setText(formatEntry(entries.get(2)));
                    paragraph4.setText(formatEntry(entries.get(3)));
                }
            }
        }

        // Aggiungi un listener per i valori selezionati
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                // Ottieni l'etichetta dell'elemento selezionato
                String label = ((PieEntry) e).getLabel();

                // Visualizza l'etichetta al centro del grafico a torta
                pieChart.setCenterText(label);
            }


            @Override
            public void onNothingSelected() {
                pieChart.setCenterText("Totale: " + totalSum[0]);
            }

        });
        // Imposta il grafico a torta come un "Ring Chart"
        pieChart.setHoleRadius(70f); // 70% del raggio
        pieChart.setTransparentCircleRadius(80f); // 80% del raggio
        pieChart.animateY(1400, Easing.EaseInOutQuad); // Animazione di rotazione

        // Modifica la legenda
        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE); // Imposta la forma degli indicatori di colore come cerchi
        legend.setFormSize(10f); // Imposta la dimensione degli indicatori di colore
        legend.setTextSize(14f); // Imposta la dimensione del testo
        legend.setWordWrapEnabled(true); // Abilita l'inviluppo di parole
        legend.setMaxSizePercent(0.5f); // Imposta la dimensione massima della legenda come il 50% della dimensione del grafico

        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate(); // refresh

        return view;
    }


    private String formatEntry(PieEntry entry) {
        return "Le tue " + entry.getLabel() + " sono state: " + entry.getValue() +" €";
    }


}

