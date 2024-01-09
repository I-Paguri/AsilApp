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

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.touchboarder.weekdaysbuttons.WeekdaysDataItem;
import com.touchboarder.weekdaysbuttons.WeekdaysDataSource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.entity.Medication;
import it.uniba.dib.sms232417.asilapp.entity.Treatment;


public class TreatmentFormMedicationsFragment extends Fragment implements WeekdaysDataSource.Callback {

    private View linearLayoutInterval;
    private TextView subtitleInterval;
    private View linearLayoutWeekdays;
    private TextView subtitleWeekdays;
    private Button btnIntakeTime;
    private int intakeCount;
    private AutoCompleteTextView intervalSelection;
    private AutoCompleteTextView howRegularly;
    private AutoCompleteTextView howToTakeMedicine;
    private int intervalSelectedNumber;
    private String intervalSelected;
    private ArrayAdapter<String> adapterQuantity;
    // Declare an ArrayList to hold the selected weekdays
    private ArrayList<WeekdaysDataItem> selectedWeekdays = new ArrayList<>();
    private String[] quantityValues;
    private List<String> quantityValuesList;
    private boolean isMilliliters;

    // Received data from TreatmentFormGeneralFragment
    private String treatmentTarget;
    private long startDate, endDate;
    private boolean endDateSwitch;
    private Treatment treatment;

    private ArrayList<Medication> medications;

    private boolean validInput;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_treatment_form_medications, container, false);

        String[] descriptionData = new String[]{getResources().getString(R.string.planning), getResources().getString(R.string.medications), getResources().getString(R.string.notes)};

        intakeCount = 1;
        isMilliliters = false;

        StateProgressBar stateProgressBar = view.findViewById(R.id.state_progress_bar);
        stateProgressBar.setStateDescriptionData(descriptionData);

        return view;
    }



    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Get the bundle
        Bundle bundle = this.getArguments();

        validInput = false;
        treatment = null;
        medications = new ArrayList<>();
        if (bundle != null) {
            treatment = bundle.getParcelable("treatment");
        }

        // Find the AutoCompleteTextView in the layout
        AutoCompleteTextView medicinesList = view.findViewById(R.id.medicines_list);
        howToTakeMedicine = view.findViewById(R.id.how_to_take_medicine);
        howRegularly = view.findViewById(R.id.how_regularly);
        intervalSelection = view.findViewById(R.id.intervalSelection);
        AutoCompleteTextView quantity = view.findViewById(R.id.quantityString);

        // Get the string array from the resources
        String[] medicines = getResources().getStringArray(R.array.medicines_list);
        String[] howToTake = getResources().getStringArray(R.array.how_to_take_medicine_list);
        String[] howRegularlyList = getResources().getStringArray(R.array.how_regularly_list);

        // Create an ArrayAdapter using the string array and a default layout
        ArrayAdapter<String> adapterMedicines = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_expandable_list_item_1, medicines);
        ArrayAdapter<String> adapterHowToTake = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_expandable_list_item_1, howToTake);
        ArrayAdapter<String> adapterHowRegularly = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_expandable_list_item_1, howRegularlyList);

        // Set the ArrayAdapter to the AutoCompleteTextView
        medicinesList.setAdapter(adapterMedicines);
        howToTakeMedicine.setAdapter(adapterHowToTake);
        howRegularly.setAdapter(adapterHowRegularly);

        quantityValuesList = new ArrayList<>();

        // Find the linearLayoutWeekdays in the layout
        linearLayoutInterval = view.findViewById(R.id.linearLayoutInterval);
        subtitleInterval = view.findViewById(R.id.subtitleInterval);

        linearLayoutWeekdays = view.findViewById(R.id.linearLayoutWeekdays);
        subtitleWeekdays = view.findViewById(R.id.subtitleWeekdays);

        howToTakeMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        howToTakeMedicine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                quantityValuesList.clear();

                if (position == 4) {
                    // Milliliters input value
                    isMilliliters = true;
                    changeQuantityInputType(); // true = milliliters
                } else {
                    isMilliliters = false;
                    changeQuantityInputType();
                    if (position == 1) {
                        // drops from 1 to 30
                        // Add integers from 1 to 5
                        for (int i = 1; i <= 30; i++) {
                            quantityValuesList.add(String.valueOf(i));
                        }
                    } else {
                        // tablets, sachets, suppositories, syringes, inhalatin from 1/4 to 5
                        // Add fractional quantities
                        quantityValuesList.add("1/4");
                        quantityValuesList.add("1/2");
                        quantityValuesList.add("3/4");
                        // Add integers from 1 to 5
                        for (int i = 1; i <= 5; i++) {
                            quantityValuesList.add(String.valueOf(i));
                        }
                    }
                    // Convert the list to an array
                    quantityValues = quantityValuesList.toArray(new String[0]);

                    // Create an ArrayAdapter using the string array and a default layout
                    adapterQuantity = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_expandable_list_item_1, quantityValues);

                    setQuantityAdapter();
                }
            }
        });

        quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                // Check if howToTakeMedicine has been selected
                if (howToTakeMedicine.getText().toString().isEmpty()) {
                    // Set error message
                    howToTakeMedicine.setError(getResources().getString(R.string.select_how_to_take_first));
                    howToTakeMedicine.requestFocus();
                }
            }
        });

        howRegularly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        });

        howRegularly.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals(getResources().getStringArray(R.array.how_regularly_list)[2])) {
                    linearLayoutWeekdays.setVisibility(View.VISIBLE);
                    subtitleWeekdays.setVisibility(View.VISIBLE);
                    linearLayoutInterval.setVisibility(View.GONE);
                    subtitleInterval.setVisibility(View.GONE);
                } else {
                    linearLayoutWeekdays.setVisibility(View.GONE);
                    subtitleWeekdays.setVisibility(View.GONE);

                    if (selectedItem.equals(getResources().getStringArray(R.array.how_regularly_list)[1])) {
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
                    InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }

                    // Create the dialog
                    showDialog();
                }
                return false;
            }
        });


        WeekdaysDataSource wds = new WeekdaysDataSource((AppCompatActivity) requireActivity(), R.id.weekdays_stub)
                .setFirstDayOfWeek(Calendar.MONDAY)
                .setUnselectedColorRes(R.color.bottom_nav_bar_background)
                .setTextColorUnselectedRes(R.color.md_theme_light_primary)
                .setFontTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                .start(this);


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
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
                        @SuppressLint("DefaultLocale") String formattedTime = String.format("%02d:%02d", hour, minute);
                        btnIntakeTime.setText(formattedTime);
                    }
                });
            }
        });

        Button btnContinue = requireView().findViewById(R.id.goNext);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    // Get the bundle
                    Bundle bundle = setBundle();

                    TreatmentFormNotesFragment treatmentFormNotesFragment = new TreatmentFormNotesFragment();
                    treatmentFormNotesFragment.setArguments(bundle);
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.nav_host_fragment_activity_main, treatmentFormNotesFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    Toast.makeText(requireActivity(), getResources().getString(R.string.fill_inputs), Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button btnBack = requireView().findViewById(R.id.goBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    fragmentManager.popBackStack();
                }
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    private void addNewIntakeLayout() {
        // Increment intakeCount
        intakeCount++;
        // Inflate the layout from XML file
        LayoutInflater inflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View intakeLayout = inflater.inflate(R.layout.add_intake_layout, null);

        // Get the parent layout
        LinearLayout parentLayout = requireView().findViewById(R.id.parentLinearLayout);

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
                // Decrement intakeCount
                intakeCount--;
                updateIntakeLabels();
            }
        });

        // Find the intakeTime button in the layout
        Button btnIntakeTime = intakeLayout.findViewById(R.id.intakeTime);

        btnIntakeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
                        @SuppressLint("DefaultLocale") String formattedTime = String.format("%02d:%02d", hour, minute);
                        btnIntakeTime.setText(formattedTime);
                    }
                });
            }
        });

        AutoCompleteTextView quantity = intakeLayout.findViewById(R.id.quantityString);
        quantity.setFocusable(true);
        quantity.setFocusableInTouchMode(true);

        changeQuantityInputType();
        quantity.setAdapter(adapterQuantity);

    }

    @SuppressLint("SetTextI18n")
    private void updateIntakeLabels() {
        // Get the parent layout
        LinearLayout parentLayout = requireView().findViewById(R.id.parentLinearLayout);

        // Initialize intakeCount to 1
        int intakeCount = 1;

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

    private void setQuantityAdapter() {
        // Get the parent layout
        LinearLayout parentLayout = requireView().findViewById(R.id.parentLinearLayout);

        // Iterate over all the child views of the parent layout
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View childView = parentLayout.getChildAt(i);

            // Check if the child view is an intake layout
            if (childView.getId() == R.id.linearLayoutIntake) {
                // Find the linearLayoutQuantityString and linearLayoutQuantityNumber in the child view
                LinearLayout childLinearLayoutQuantityString = childView.findViewById(R.id.linearLayoutQuantityString);

                AutoCompleteTextView quantity = childLinearLayoutQuantityString.findViewById(R.id.quantityString);
                quantity.setAdapter(adapterQuantity);

            }
        }
    }

    private void changeQuantityInputType() {
        // Get the parent layout
        LinearLayout parentLayout = requireView().findViewById(R.id.parentLinearLayout);

        // Iterate over all the child views of the parent layout
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View childView = parentLayout.getChildAt(i);

            // Check if the child view is an intake layout
            if (childView.getId() == R.id.linearLayoutIntake) {
                // Find the linearLayoutQuantityString and linearLayoutQuantityNumber in the child view
                LinearLayout childLinearLayoutQuantityString = childView.findViewById(R.id.linearLayoutQuantityString);
                LinearLayout childLinearLayoutQuantityNumber = childView.findViewById(R.id.linearLayoutQuantityNumber);

                if (isMilliliters) {
                    // Milliliters input value
                    childLinearLayoutQuantityString.setVisibility(View.GONE);
                    childLinearLayoutQuantityNumber.setVisibility(View.VISIBLE);
                } else {
                    childLinearLayoutQuantityString.setVisibility(View.VISIBLE);
                    childLinearLayoutQuantityNumber.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onWeekdaysItemClicked(int attachId, WeekdaysDataItem item) {
        if (item.isSelected()) {
            // Add the selected item to the ArrayList
            selectedWeekdays.add(item);
            //Toast.makeText(getActivity(), item.getLabel() + " selected", Toast.LENGTH_SHORT).show();
        } else {
            // Remove the deselected item from the ArrayList
            selectedWeekdays.remove(item);
        }
    }

    @Override
    public void onWeekdaysSelected(int attachId, ArrayList<WeekdaysDataItem> items) {

    }

    @SuppressLint("SetTextI18n")
    private void showDialog() {
        // Create a dialog builder
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity(), R.style.CustomMaterialDialog);

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
                    intervalSelectedNumber = numberPicker1.getValue();
                    intervalSelected = displayedValues[numberPicker2.getValue()];

                    String formattedSelectedInterval = intervalSelected;

                    if (intervalSelected.equals(getResources().getString(R.string.day))) {
                        // days
                        formattedSelectedInterval = getResources().getQuantityString(R.plurals.days, intervalSelectedNumber, intervalSelectedNumber);
                    } else {
                        if (intervalSelected.equals(getResources().getString(R.string.week))) {
                            // weeks
                            formattedSelectedInterval = getResources().getQuantityString(R.plurals.weeks, intervalSelectedNumber, intervalSelectedNumber);
                        } else {
                            // months
                            formattedSelectedInterval = getResources().getQuantityString(R.plurals.months, intervalSelectedNumber, intervalSelectedNumber);
                        }
                    }


                    if (intervalSelectedNumber == 1) {
                        if (intervalSelected.equals(getResources().getString(R.string.day))) {
                            subtitleInterval.setVisibility(View.GONE);
                            linearLayoutInterval.setVisibility(View.GONE);

                            howRegularly.setText(getResources().getStringArray(R.array.how_regularly_list)[0], false);

                        } else {
                            intervalSelection.setText(getResources().getString(R.string.every) + " " + formattedSelectedInterval);
                        }
                    } else {
                        intervalSelection.setText(getResources().getString(R.string.every) + " " + intervalSelectedNumber + " " + formattedSelectedInterval);
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    // User cancelled the dialog, do something if necessary
                });

        // Create and show the dialog
        Dialog dialog = builder.create();
        dialog.show();
    }

    private boolean validateInput() {
        setupTextWatchers();

        AutoCompleteTextView medicinesList = getView().findViewById(R.id.medicines_list);
        AutoCompleteTextView howToTakeMedicine = getView().findViewById(R.id.how_to_take_medicine);
        AutoCompleteTextView howRegularly = getView().findViewById(R.id.how_regularly);
        AutoCompleteTextView intervalSelection = getView().findViewById(R.id.intervalSelection);

        validInput = true;

        if (medicinesList.getText().toString().isEmpty()) {
            TextInputLayout medicationsListLayout = getView().findViewById(R.id.medicationNameInputLayout);
            medicationsListLayout.setError(getResources().getString(R.string.required_field));
            validInput = false;
        }
        if (howToTakeMedicine.getText().toString().isEmpty()) {
            TextInputLayout howToTakeMedicineLayout = getView().findViewById(R.id.how_to_take_medicine_input_layout);
            howToTakeMedicineLayout.setError(getResources().getString(R.string.required_field));
            validInput = false;
        }
        if (howRegularly.getText().toString().isEmpty()) {
            TextInputLayout howRegularlyInputLayout = getView().findViewById(R.id.how_regularly_input_layout);
            howRegularlyInputLayout.setError(getResources().getString(R.string.required_field));
            validInput = false;
        } else {
            if (howRegularly.getText().toString().equals(getResources().getStringArray(R.array.how_regularly_list)[1])) {
                if (intervalSelection.getText().toString().isEmpty()) {
                    TextInputLayout intervalSelectionInputLayout = getView().findViewById(R.id.intervalSelectionInputLayout);
                    intervalSelectionInputLayout.setError(getResources().getString(R.string.required_field));
                    validInput = false;
                }
            } else {
                if (howRegularly.getText().toString().equals(getResources().getStringArray(R.array.how_regularly_list)[2])) {
                    if (selectedWeekdays.isEmpty()) {
                        // Assuming you have a TextView to display this error
                        Toast.makeText(requireActivity(), getResources().getString(R.string.select_weekdays), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        // Get the parent layout
        LinearLayout parentLayout = requireView().findViewById(R.id.parentLinearLayout);

        // Iterate over all the child views of the parent layout
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View childView = parentLayout.getChildAt(i);

            // Check if the child view is an intake layout
            if (childView.getId() == R.id.linearLayoutIntake) {
                // Find the intakeTime and quantity views in the child view
                Button intakeTime = childView.findViewById(R.id.intakeTime);
                TextInputLayout quantityStringInputLayout = childView.findViewById(R.id.quantityStringInputLayout);
                TextInputLayout quantityNumberInputLayout = childView.findViewById(R.id.quantityNumberInputLayout);
                AutoCompleteTextView quantityString = childView.findViewById(R.id.quantityString);
                AutoCompleteTextView quantityNumber = childView.findViewById(R.id.quantityNumber);

                // Check if intakeTime and quantity are filled
                if (intakeTime.getText().toString().isEmpty()) {
                    intakeTime.setError(getResources().getString(R.string.required_field));
                    validInput = false;
                }

                if (isMilliliters) {
                    if (quantityNumber.getText().toString().isEmpty()) {
                        quantityNumberInputLayout.setError(getResources().getString(R.string.required_field));
                        validInput = false;
                    }
                } else {
                    if (quantityString.getText().toString().isEmpty()) {
                        quantityStringInputLayout.setError(getResources().getString(R.string.required_field));
                        validInput = false;
                    }
                }
            }
        }

        return validInput;
    }

    private void setupTextWatchers() {
        AutoCompleteTextView medicinesList = getView().findViewById(R.id.medicines_list);
        AutoCompleteTextView howToTakeMedicine = getView().findViewById(R.id.how_to_take_medicine);
        AutoCompleteTextView howRegularly = getView().findViewById(R.id.how_regularly);
        AutoCompleteTextView intervalSelection = getView().findViewById(R.id.intervalSelection);

        // Create a TextWatcher
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No action needed here
            }

            @Override
            public void afterTextChanged(Editable s) {
                // After the text has changed, check if the field is now filled and if so, remove the error
                if (!s.toString().isEmpty()) {
                    View view = getActivity().getCurrentFocus();
                    if (view instanceof AutoCompleteTextView) {
                        ((TextInputLayout) view.getParent().getParent()).setError(null);
                    }
                }
            }
        };

        // Add the TextWatcher to your AutoCompleteTextViews
        medicinesList.addTextChangedListener(textWatcher);
        howToTakeMedicine.addTextChangedListener(textWatcher);
        howRegularly.addTextChangedListener(textWatcher);
        intervalSelection.addTextChangedListener(textWatcher);

        // Get the parent layout
        LinearLayout parentLayout = requireView().findViewById(R.id.parentLinearLayout);

        // Iterate over all the child views of the parent layout
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View childView = parentLayout.getChildAt(i);

            // Check if the child view is an intake layout
            if (childView.getId() == R.id.linearLayoutIntake) {
                // Find the intakeTime and quantity views in the child view
                Button intakeTime = childView.findViewById(R.id.intakeTime);
                AutoCompleteTextView quantityString = childView.findViewById(R.id.quantityString);
                AutoCompleteTextView quantityNumber = childView.findViewById(R.id.quantityNumber);

                intakeTime.addTextChangedListener(textWatcher);

                if (isMilliliters) {
                    quantityNumber.addTextChangedListener(textWatcher);
                } else {
                    quantityString.addTextChangedListener(textWatcher);
                }
            }
        }

    }

    private Bundle setBundle() {
        Bundle bundle = new Bundle();

        String medicationNameString, howToTakeString, howRegularlyString, intervalSelectedString;
        medicationNameString = "";
        howToTakeString = "";
        howRegularlyString = "";
        intervalSelectedString = "";

        AutoCompleteTextView medicinesList = requireView().findViewById(R.id.medicines_list);
        AutoCompleteTextView howToTakeMedicine = requireView().findViewById(R.id.how_to_take_medicine);
        AutoCompleteTextView howRegularly = requireView().findViewById(R.id.how_regularly);
        AutoCompleteTextView intervalSelection = requireView().findViewById(R.id.intervalSelection);

        if (!medicinesList.getText().toString().isEmpty()) {
            medicationNameString = medicinesList.getText().toString();
            //treatment.setMedication(medicinesList.getText().toString());
        }

        if (!howToTakeMedicine.getText().toString().isEmpty()) {
            howToTakeString = howToTakeMedicine.getText().toString();
            //treatment.setHowToTake(howToTakeMedicine.getText().toString());
        }

        if (!howRegularly.getText().toString().isEmpty()) {
            howRegularlyString = howRegularly.getText().toString();

            if (howRegularly.getText().toString().equals(getResources().getStringArray(R.array.how_regularly_list)[1])) {
                if (!intervalSelection.getText().toString().isEmpty()) {
                    intervalSelectedString = intervalSelection.getText().toString();
                    //treatment.setIntervalSelection(intervalSelection.getText().toString());
                }
            }
            //treatment.setHowRegularly(howRegularly.getText().toString());
        }

        Medication medication = new Medication(medicationNameString, howToTakeString, howRegularlyString, selectedWeekdays);

        medication.setIntervalSelected(intervalSelectedString);

        // Get the parent layout
        LinearLayout parentLayout = requireView().findViewById(R.id.parentLinearLayout);

        // Iterate over all the child views of the parent layout
        for (int i = 0; i < parentLayout.getChildCount(); i++) {
            View childView = parentLayout.getChildAt(i);

            // Check if the child view is an intake layout
            if (childView.getId() == R.id.linearLayoutIntake) {
                // Find the intakeTime and quantity views in the child view
                Button intakeTime = childView.findViewById(R.id.intakeTime);
                AutoCompleteTextView quantityString = childView.findViewById(R.id.quantityString);
                AutoCompleteTextView quantityNumber = childView.findViewById(R.id.quantityNumber);

                // Check if intakeTime and quantity are filled
                if (!intakeTime.getText().toString().isEmpty()) {
                    //treatment.addIntakeTime(intakeTime.getText().toString());
                    medication.addIntakeTime(intakeTime.getText().toString());
                }

                if (isMilliliters) {
                    if (!quantityNumber.getText().toString().isEmpty()) {
                        // treatment.addQuantity(quantityNumber.getText().toString());
                        medication.addQuantity(quantityNumber.getText().toString());
                    }
                } else {
                    if (!quantityString.getText().toString().isEmpty()) {
                        // treatment.addQuantity(quantityString.getText().toString());
                        medication.addQuantity(quantityString.getText().toString());
                    }
                }

            }
        }

        treatment.addMedication(medication);

        Log.d("Medication", medication.toString());
        bundle.putParcelable("treatment", treatment);

        return bundle;
    }


}