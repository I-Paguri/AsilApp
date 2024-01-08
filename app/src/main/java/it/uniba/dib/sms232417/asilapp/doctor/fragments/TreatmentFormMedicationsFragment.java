package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import static com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.touchboarder.weekdaysbuttons.WeekdaysDataItem;
import com.touchboarder.weekdaysbuttons.WeekdaysDataSource;

import java.util.ArrayList;
import java.util.Calendar;

import it.uniba.dib.sms232417.asilapp.R;


public class TreatmentFormMedicationsFragment extends Fragment implements WeekdaysDataSource.Callback {

    private View linearLayoutInterval;
    private TextView subtitleInterval;
    private View linearLayoutWeekdays;
    private TextView subtitleWeekdays;
    private String[] descriptionData;
    private MaterialDatePicker.Builder<Long> builderIntakeTime;
    private Button btnIntakeTime;
    private int intakeCount;
    private Button btnContinue;
    private AutoCompleteTextView intervalSelection, howRegularly, howToTakeMedicine, medicinesList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_treatment_form_medications, container, false);

        descriptionData = new String[]{getResources().getString(R.string.planning), getResources().getString(R.string.medications), getResources().getString(R.string.notes)};

        intakeCount = 1;

        StateProgressBar stateProgressBar = view.findViewById(R.id.state_progress_bar);
        stateProgressBar.setStateDescriptionData(descriptionData);

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Find the AutoCompleteTextView in the layout
        medicinesList = view.findViewById(R.id.medicines_list);
        howToTakeMedicine = view.findViewById(R.id.how_to_take_medicine);
        howRegularly = view.findViewById(R.id.how_regularly);
        intervalSelection = view.findViewById(R.id.intervalSelection);

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
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Hide the keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }

                    // Create the dialog
                    showDialog();
                }
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

        TextView intakeLabel = view.findViewById(R.id.intakeLabel);
        intakeLabel.setText(getResources().getString(R.string.intake) + " " + intakeCount);

        Button btnAddIntake = view.findViewById(R.id.btnAddIntake);
        btnAddIntake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewIntakeLayout();
                updateIntakeLabels();
            }
        });

        btnIntakeTime = view.findViewById(R.id.intakeTime);

        btnIntakeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                // Create a MaterialTimePicker
                MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                        .setInputMode(INPUT_MODE_CLOCK)
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(12)
                        .setMinute(0)
                        .setTitleText(getResources().getString(R.string.select_time))
                        .build();

                // Show the MaterialTimePicker
                materialTimePicker.show(getChildFragmentManager(), "time_picker");

                materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int hour = materialTimePicker.getHour();
                        int minute = materialTimePicker.getMinute();
                        String formattedTime = String.format("%02d:%02d", hour, minute);
                        btnIntakeTime.setText(formattedTime);
                    }
                });
            }
        });

        btnContinue = getView().findViewById(R.id.goNext);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreatmentFormNotesFragment treatmentFormNotesFragment = new TreatmentFormNotesFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, treatmentFormNotesFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    private void addNewIntakeLayout() {
        // Inflate the layout from XML file
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View intakeLayout = inflater.inflate(R.layout.add_intake_layout, null);

        // Get the parent layout
        LinearLayout parentLayout = getView().findViewById(R.id.parentLinearLayout);

        // Get the index of the second last view in parentLayout
        int index = parentLayout.getChildCount() - 1;

        // Add the new layout to the parent layout at the index of the second last view
        parentLayout.addView(intakeLayout, index);

        TextView intakeLabel = intakeLayout.findViewById(R.id.intakeLabel);
        intakeLabel.setText(getResources().getString(R.string.intake) + " " + intakeCount);
        // Find the close button in the layout
        Button closeButton = intakeLayout.findViewById(R.id.closeButton);

        // Set a click listener on the close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the intakeLayout from the parent layout
                parentLayout.removeView(intakeLayout);
                updateIntakeLabels();
            }
        });

        // Find the intakeTime button in the layout
        Button btnIntakeTime = intakeLayout.findViewById(R.id.intakeTime);

        btnIntakeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                // Create a MaterialTimePicker
                MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                        .setInputMode(INPUT_MODE_CLOCK)
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(12)
                        .setMinute(0)
                        .setTitleText(getResources().getString(R.string.select_time))
                        .build();

                // Show the MaterialTimePicker
                materialTimePicker.show(getChildFragmentManager(), "time_picker");

                materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int hour = materialTimePicker.getHour();
                        int minute = materialTimePicker.getMinute();
                        String formattedTime = String.format("%02d:%02d", hour, minute);
                        btnIntakeTime.setText(formattedTime);
                    }
                });
            }
        });
    }

    private void updateIntakeLabels() {
        // Get the parent layout
        LinearLayout parentLayout = getView().findViewById(R.id.parentLinearLayout);

        // Initialize intakeCount to 1
        intakeCount = 1;

        // Iterate over all the child views of the parent layout
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View childView = parentLayout.getChildAt(i);

            // Check if the child view is an intake layout
            if (childView.getId() == R.id.linearLayoutIntake) {
                // Find the intakeLabel in the child view
                TextView intakeLabel = childView.findViewById(R.id.intakeLabel);

                // Update the text of the intakeLabel
                intakeLabel.setText(getResources().getString(R.string.intake) + " " + intakeCount);

                // Increment intakeCount
                intakeCount++;
            }
        }
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
                    // User clicked OK, retrieve the selected values
                    int selectedNumber = numberPicker1.getValue();
                    String selectedInterval = displayedValues[numberPicker2.getValue()];

                    String formattedSelectedInterval = selectedInterval;

                    if (selectedInterval.equals(getResources().getString(R.string.day))) {
                        // days
                        formattedSelectedInterval = getResources().getQuantityString(R.plurals.days, selectedNumber, selectedNumber);
                    } else {
                        if (selectedInterval.equals(getResources().getString(R.string.week))) {
                            // weeks
                            formattedSelectedInterval = getResources().getQuantityString(R.plurals.weeks, selectedNumber, selectedNumber);
                        } else {
                            // months
                            formattedSelectedInterval = getResources().getQuantityString(R.plurals.months, selectedNumber, selectedNumber);
                        }
                    }


                    if (selectedNumber == 1) {
                        if (selectedInterval.equals(getResources().getString(R.string.day))) {
                            subtitleInterval.setVisibility(View.GONE);
                            linearLayoutInterval.setVisibility(View.GONE);

                            howRegularly.setText(getResources().getStringArray(R.array.how_regularly_list)[0]);

                        } else {
                            intervalSelection.setText(getResources().getString(R.string.every) + " " + formattedSelectedInterval);
                        }
                    } else {
                        intervalSelection.setText(getResources().getString(R.string.every) + " " + selectedNumber + " " + formattedSelectedInterval);
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    // User cancelled the dialog, do something if necessary
                });

        // Create and show the dialog
        Dialog dialog = builder.create();
        dialog.show();
    }
}