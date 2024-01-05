package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.touchboarder.weekdaysbuttons.WeekdaysDataItem;
import com.touchboarder.weekdaysbuttons.WeekdaysDataSource;

import java.util.ArrayList;
import java.util.Calendar;

import it.uniba.dib.sms232417.asilapp.R;


public class TreatmentFormMedicinesFragment extends Fragment implements WeekdaysDataSource.Callback {

    private View linearLayoutInterval;
    private TextView subtitleInterval;
    private View linearLayoutWeekdays;
    private TextView subtitleWeekdays;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_treatment_form_medicines, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Find the AutoCompleteTextView in the layout
        AutoCompleteTextView medicinesList = view.findViewById(R.id.medicines_list);
        AutoCompleteTextView howToTakeMedicine = view.findViewById(R.id.how_to_take_medicine);
        AutoCompleteTextView howRegularly = view.findViewById(R.id.how_regularly);
        AutoCompleteTextView intervalSelection = view.findViewById(R.id.intervalSelection);

        // Get the string array from the resources
        String[] medicines = getResources().getStringArray(R.array.medicines_list);
        String[] howToTake = getResources().getStringArray(R.array.how_to_take_medicine_list);
        String[] howRegularlyList = getResources().getStringArray(R.array.how_regularly_list);

        // Create an ArrayAdapter using the string array and a default layout
        ArrayAdapter<String> adapterMedicines = new ArrayAdapter<>(getActivity(), android.R.layout.simple_expandable_list_item_1, medicines);
        ArrayAdapter<String> adapterHowToTake = new ArrayAdapter<>(getActivity(), android.R.layout.simple_expandable_list_item_1, howToTake);
        ArrayAdapter<String> adapterHowRegularly = new ArrayAdapter<>(getActivity(), android.R.layout.simple_expandable_list_item_1, howRegularlyList);

        // Set the ArrayAdapter to the AutoCompleteTextView
        medicinesList.setAdapter(adapterMedicines);
        howToTakeMedicine.setAdapter(adapterHowToTake);
        howRegularly.setAdapter(adapterHowRegularly);

        // Find the linearLayoutWeekdays in the layout
        linearLayoutInterval = view.findViewById(R.id.linearLayoutInterval);
        subtitleInterval = view.findViewById(R.id.subtitleInterval);

        linearLayoutWeekdays = view.findViewById(R.id.linearLayoutWeekdays);
        subtitleWeekdays = view.findViewById(R.id.subtitleWeekdays);



        howToTakeMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });


        howRegularly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        howRegularly.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Weekdays")) {
                    linearLayoutWeekdays.setVisibility(View.VISIBLE);
                    subtitleWeekdays.setVisibility(View.VISIBLE);
                    linearLayoutInterval.setVisibility(View.GONE);
                    subtitleInterval.setVisibility(View.GONE);
                } else {
                    linearLayoutWeekdays.setVisibility(View.GONE);
                    subtitleWeekdays.setVisibility(View.GONE);

                    if (selectedItem.equals("Interval")) {
                        linearLayoutInterval.setVisibility(View.VISIBLE);
                        subtitleInterval.setVisibility(View.VISIBLE);
                    } else {
                        linearLayoutInterval.setVisibility(View.GONE);
                        subtitleInterval.setVisibility(View.GONE);
                    }
                }
            }
        });

        intervalSelection.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                // Create the dialog
                showDialog();

                return false;
            }
        });



        WeekdaysDataSource wds = new WeekdaysDataSource((AppCompatActivity) getActivity(), R.id.weekdays_stub)
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setUnselectedColorRes(R.color.bottom_nav_bar_background)
                .setTextColorUnselectedRes(R.color.md_theme_light_primary)
                .setFontTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                .start(this);

        new WeekdaysDataSource.Callback() {
            @Override
            public void onWeekdaysItemClicked(int attachId, WeekdaysDataItem item) {
                // Do something if today is selected?
                Calendar calendar = Calendar.getInstance();
                if (item.getCalendarDayId() == calendar.get(Calendar.DAY_OF_WEEK) && item.isSelected()) {

                }
            }

            @Override
            public void onWeekdaysSelected(int attachId, ArrayList<WeekdaysDataItem> items) {
                //Filter on the attached id if there is multiple weekdays data sources
                if (attachId == R.id.weekdays_stub) {
                    // Do something on week 4?
                }
            }

        };

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onWeekdaysItemClicked(int i, WeekdaysDataItem weekdaysDataItem) {

    }

    @Override
    public void onWeekdaysSelected(int i, ArrayList<WeekdaysDataItem> arrayList) {

    }

    private void showDialog() {
        // Create a dialog builder
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity(), R.style.CustomMaterialDialog);

        // Create a LinearLayout
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER);

        // Create the NumberPickers
        NumberPicker numberPicker1 = new NumberPicker(getActivity());
        NumberPicker numberPicker2 = new NumberPicker(getActivity());

        // Set the min and max values for numberPicker1
        numberPicker1.setMinValue(1);
        numberPicker1.setMaxValue(99);

        // Set the min and max values for numberPicker2
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(2);

        // Set the displayed values for numberPicker2
        String[] displayedValues = new String[]{getResources().getString(R.string.day), getResources().getString(R.string.week), getResources().getString(R.string.month)};
        numberPicker2.setDisplayedValues(displayedValues);

        // Create layout parameters with margins
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, (int) (16 * getResources().getDisplayMetrics().density), 0); // 8dp to px

        // Add the NumberPickers to the LinearLayout
        layout.addView(numberPicker1, layoutParams); // Add layout params to numberPicker1
        layout.addView(numberPicker2);

        // Set the LinearLayout as the dialog view
        builder.setView(layout);

        // Set the dialog title and buttons
        builder.setTitle(getResources().getString(R.string.what_interval)).setMessage(getResources().getString(R.string.what_interval_message))
                .setPositiveButton("OK", (dialog, id) -> {
                    // User clicked OK, do something
                    int num1 = numberPicker1.getValue();
                    String str2 = displayedValues[numberPicker2.getValue()];
                    // ...
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    // User cancelled the dialog, do something if necessary
                });

        // Create and show the dialog
        Dialog dialog = builder.create();
        dialog.show();
    }
}