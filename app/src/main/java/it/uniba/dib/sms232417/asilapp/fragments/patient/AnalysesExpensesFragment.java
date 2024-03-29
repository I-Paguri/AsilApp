package it.uniba.dib.sms232417.asilapp.fragments.patient;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ekn.gruzer.gaugelibrary.ArcGauge;
import com.ekn.gruzer.gaugelibrary.Range;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.highlight.Highlight;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.databaseAdapter.DatabaseAdapterPatient;
import it.uniba.dib.sms232417.asilapp.entity.Expenses;
import it.uniba.dib.sms232417.asilapp.interfaces.OnExpensesListCallback;

public class AnalysesExpensesFragment extends Fragment {

    private Toolbar toolbar;

    public AnalysesExpensesFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_analyses_expenses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = requireActivity().findViewById(R.id.toolbar);


        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        // Show home button
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable backIcon = getResources().getDrawable(R.drawable.arrow_back, null);
        // Set color filter
        backIcon.setColorFilter(getResources().getColor(R.color.md_theme_light_surface), PorterDuff.Mode.SRC_ATOP);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeAsUpIndicator(backIcon);

        // Set toolbar title
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.expense_analysis));
        // Change toolbar title text color
        toolbar.setTitleTextColor(getResources().getColor(R.color.md_theme_light_surface));

        // Set navigation click listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        // Prendo i riferimenti alle textview e al grafico a torta
        PieChart pieChart = view.findViewById(R.id.pieChart);
        // Prima di recuperare i dati, imposta un messaggio di caricamento personalizzato
        pieChart.setNoDataText("Caricamento dati...");


        TextView paragraph1 = view.findViewById(R.id.resocontoSpesaFarmaci);
        TextView paragraph2 = view.findViewById(R.id.resocontoSpesaTerapie);
        TextView paragraph3 = view.findViewById(R.id.resocontoSpesaTrattamenti);
        TextView paragraph4 = view.findViewById(R.id.resocontoSpesaEsami);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            // The user is signed in

            // Get the user's UUID
            String patientUUID = currentUser.getUid();

            // Create an instance of DatabaseAdapterPatient
            DatabaseAdapterPatient adapter = new DatabaseAdapterPatient(getContext());

            // Call the getSumExpenses method
            adapter.getExpensesList(patientUUID,  new OnExpensesListCallback() {
                @Override
                public void onCallback(List<Expenses> expensesList) {
                    // This method is called when the total expenses are successfully retrieved.
                    // You can update your UI here.

                    //controllo che la lista non sia vuota:
                    if (!expensesList.isEmpty()) {


                        double sumFarmaci = getExpensesByCategory(expensesList, Expenses.Category.FARMACI);
                        double sumTerapie = getExpensesByCategory(expensesList, Expenses.Category.TERAPIE);
                        double sumAltro= getExpensesByCategory(expensesList, Expenses.Category.ALTRO);
                        double sumEsami = getExpensesByCategory(expensesList, Expenses.Category.ESAMI);

                        double totalSum = sumFarmaci + sumTerapie + sumAltro + sumEsami;
                        //Creazione grafico a torta
                        List<PieEntry> entries = new ArrayList<>();
                        entries.add(new PieEntry((float) sumFarmaci, getString(R.string.expenses_medicines)));
                        entries.add(new PieEntry((float) sumTerapie, getString(R.string.expenses_therapies)));
                        entries.add(new PieEntry((float) sumAltro, getString(R.string.expenses_others)));
                        entries.add(new PieEntry((float) sumEsami, getString(R.string.expenses_examinations)));
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

                        //convert totalSum to string
                        String totalSumString = String.valueOf(totalSum);

                        pieChart.setCenterText(getResources().getString(R.string.total)+": " + totalSumString);



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
                                pieChart.setCenterText(getResources().getString(R.string.total)+": " + totalSum);
                            }

                        });

                    }else {
                        // La lista è vuota
                        Log.d("Expenses", "La lista è vuota");
                        pieChart.setNoDataText("Non hai nessuna spesa");

                    }

                    // Calcola le spese dell'ultimo mese
                    double lastMonthExpenses = sumLastMonthExpenses(expensesList);
                    Log.d("Expenses", "Last month expenses: " + lastMonthExpenses);
                    // textViewBalanceMonth.setText(String.valueOf(lastMonthExpenses) + " €");

                    //Calcola le spese dell'ultima settimana
                    double lastWeekExpenses = sumLastWeekExpenses(expensesList);
                    Log.d("Expenses", "Last week expenses: " + lastWeekExpenses);
                    //  textViewBalanceWeek.setText(String.valueOf(lastWeekExpenses) + " €");

                    ArcGauge arcGaugeMonth = view.findViewById(R.id.arcGaugeMonth);
                    ArcGauge arcGaugeWeek = view.findViewById(R.id.arcGaugeWeek);

                    // Crea i range di colori
                    Range range = new Range();
                    range.setColor(Color.parseColor("#ce0000"));
                    range.setFrom(40.0);
                    range.setTo(60.0);

                    Range range_1 = new Range();
                    range_1.setColor(Color.parseColor("#ce0000"));
                    range_1.setFrom(200.0);
                    range_1.setTo(400.0);

                    Range range2 = new Range();
                    range2.setColor(Color.parseColor("#E3E500"));
                    range2.setFrom(60.0);
                    range2.setTo(100.0);

                    Range range2_1 = new Range();
                    range2_1.setColor(Color.parseColor("#E3E500"));
                    range2_1.setFrom(100.0);
                    range2_1.setTo(150.0);

                    Range range3 = new Range();
                    range3.setColor(Color.parseColor("#00b20b"));
                    range3.setFrom(150.0);
                    range3.setTo(200.0);

                    Range range4 = new Range();
                    range4.setColor(Color.parseColor("#00b20b"));
                    range4.setFrom(0.0);
                    range4.setTo(100.0);

                    // Aggiungi i range di colori agli ArcGauge
                    arcGaugeMonth.addRange(range3);
                    arcGaugeMonth.addRange(range2);
                    arcGaugeMonth.addRange(range);

                    arcGaugeWeek.addRange(range4);
                    arcGaugeWeek.addRange(range2_1);
                    arcGaugeWeek.addRange(range_1);


                    // Imposta il valore minimo e massimo dell'ArcGauge
                    arcGaugeMonth.setMinValue(0);
                    arcGaugeMonth.setMaxValue(400);

                    // Imposta il valore corrente dell'ArcGauge
                    arcGaugeMonth.setValue(lastMonthExpenses);

                    // Imposta il valore minimo e massimo dell'ArcGauge
                    arcGaugeWeek.setMinValue(0);
                    arcGaugeWeek.setMaxValue(200);

                    // Imposta il valore corrente dell'ArcGauge
                    arcGaugeWeek.setValue(lastWeekExpenses);




                }

                @Override
                public void onCallbackError(Exception e) {
                    // This method is called when there is an error retrieving the total expenses.
                    // You can handle the error here.
                    Log.e("Expenses", "Error getting total expenses", e);
                }
            });
        } else {
            // No user is signed in
            Log.d("Expenses", "No user is signed in");
        }

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

    }

    private String formatEntry(PieEntry entry) {
        return String.format(getString(R.string.entry_format), entry.getLabel(), entry.getValue());
    }

    public double getExpensesByCategory(List<Expenses> expensesList, Expenses.Category category) {
        double sum = 0;
        for (Expenses expense : expensesList) {
            if (expense.getCategory() == category) {
                sum += expense.getAmount(); // assuming there's a method getAmount() in Expenses class
            }
        }
        return sum;
    }
    public double sumLastMonthExpenses(List<Expenses> expensesList) {
        // Get the current date
        LocalDate now = LocalDate.now();
        // Get the start date of the last month
        LocalDate lastMonth = now.minusMonths(1);

        // Convert LocalDate to Date
        Date lastMonthDate = Date.from(lastMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Filter the expenses of the last month and sum them
        double totalExpenses = expensesList.stream()
                .filter(expense -> expense.getDate().after(lastMonthDate))
                .mapToDouble(Expenses::getAmount)
                .sum();

        return totalExpenses;
    }

    public double sumLastWeekExpenses(List<Expenses> expensesList) {
        // Get the current date
        LocalDate now = LocalDate.now();
        // Get the start date of the last week
        LocalDate lastWeek = now.minus(1, ChronoUnit.WEEKS);

        // Convert LocalDate to Date
        Date lastWeekDate = Date.from(lastWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Filter the expenses of the last week and sum them
        double totalExpenses = expensesList.stream()
                .filter(expense -> expense.getDate().after(lastWeekDate))
                .mapToDouble(Expenses::getAmount)
                .sum();

        return totalExpenses;
    }

}



