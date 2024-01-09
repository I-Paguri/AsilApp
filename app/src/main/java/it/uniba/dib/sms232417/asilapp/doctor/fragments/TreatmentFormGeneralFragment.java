package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.support.v4.media.MediaDescriptionCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kofigyan.stateprogressbar.StateProgressBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.entity.Treatment;

public class TreatmentFormGeneralFragment extends Fragment {

    private ConstraintLayout constraintLayout;
    private MaterialButton btnStartDate, btnEndDate;
    private SimpleDateFormat dateFormat;
    private Date startDate, endDate;
    private Button btnBack, btnNext;
    private TextInputEditText treatmentTarget;
    private MaterialDatePicker.Builder<Long> builderStartDate, builderEndDate;
    private MaterialSwitch endDateSwitch;
    private Toolbar toolbar;
    private String[] descriptionData;

    private Treatment treatment;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        toolbar = requireActivity().findViewById(R.id.toolbar);


        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        // Show home button
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set home icon as back button
        Drawable homeIcon = getResources().getDrawable(R.drawable.arrow_back, null);
        // Set color filter
        homeIcon.setColorFilter(getResources().getColor(R.color.md_theme_light_surface), PorterDuff.Mode.SRC_ATOP);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeAsUpIndicator(homeIcon);

        // Set toolbar title
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.new_treatment));
        // Change toolbar title text color
        toolbar.setTitleTextColor(getResources().getColor(R.color.md_theme_light_surface));

        // Set navigation click listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to previous fragment
                new MaterialAlertDialogBuilder(requireContext(), R.style.CustomMaterialDialog)
                        .setTitle(getResources().getString(R.string.going_back))
                        .setMessage(getResources().getString(R.string.unsaved_changes))
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Respond to negative button press
                            }
                        })
                        .setPositiveButton(getResources().getString(R.string.go_back), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Respond to positive button press
                                // Navigate back
                                FragmentManager fragmentManager = getFragmentManager();
                                if (fragmentManager != null) {
                                    fragmentManager.popBackStack();
                                }
                            }
                        })
                        .create()
                        .show();
            }
        });

        constraintLayout = (ConstraintLayout) inflater.inflate(R.layout.fragment_treatment_form_general, container, false);

        Context context = getContext();

        if (context != null) {
            MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                    .setTitle(context.getString(R.string.app_name))
                    .build();
        }


        btnStartDate = constraintLayout.findViewById(R.id.startDate);
        btnEndDate = constraintLayout.findViewById(R.id.endDate);

        TextInputLayout textInputLayout = constraintLayout.findViewById(R.id.treatmentTargetInputLayout);

        btnBack = constraintLayout.findViewById(R.id.goBack);
        btnNext = constraintLayout.findViewById(R.id.goNext);

        btnStartDate.setStrokeColor(ColorStateList.valueOf(textInputLayout.getBoxStrokeColor()));
        btnStartDate.setStrokeWidth(textInputLayout.getBoxStrokeWidth());
        btnEndDate.setStrokeColor(ColorStateList.valueOf(textInputLayout.getBoxStrokeColor()));
        btnEndDate.setStrokeWidth(textInputLayout.getBoxStrokeWidth());

        descriptionData = new String[]{getResources().getString(R.string.planning), getResources().getString(R.string.medications), getResources().getString(R.string.notes)};

        StateProgressBar stateProgressBar = (StateProgressBar) constraintLayout.findViewById(R.id.state_progress_bar);
        stateProgressBar.setStateDescriptionData(descriptionData);


        return constraintLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        builderStartDate = MaterialDatePicker.Builder.datePicker();
        builderEndDate = MaterialDatePicker.Builder.datePicker();

        if (startDate != null) {
            btnStartDate.setText(dateFormat.format(startDate));
        }

        if (endDate != null) {
            btnEndDate.setText(dateFormat.format(endDate));
        }

        treatmentTarget = constraintLayout.findViewById(R.id.treatmentTargetEditText);

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
                    ((TextInputLayout) treatmentTarget.getParent().getParent()).setError(null);
                }
            }
        };

        // Add the TextWatcher to your treatmentTarget
        treatmentTarget.addTextChangedListener(textWatcher);

        // Get today date
        Date today = new Date();
        dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                builderStartDate = MaterialDatePicker.Builder.datePicker();
                builderStartDate.setTheme(R.style.CustomMaterialDatePicker);
                MaterialDatePicker<Long> datePicker = builderStartDate.build();

                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        // Format the selected date and set it as the button text
                        startDate = new Date(selection);
                        String formattedDate = dateFormat.format(startDate);

                        btnStartDate.setText(formattedDate);


                    }
                });

                datePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startDate = null;
                        endDate = null;
                        btnStartDate.setText(getResources().getString(R.string.insert_start_date));
                        btnEndDate.setText(getResources().getString(R.string.insert_end_date));
                    }
                });


                datePicker.show(getChildFragmentManager(), "date_picker");
            }
        });


        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                if (startDate != null) {
                    builderEndDate = MaterialDatePicker.Builder.datePicker().setSelection(builderStartDate.build().getSelection());
                } else {
                    builderEndDate = MaterialDatePicker.Builder.datePicker().setSelection(today.getTime());
                }

                builderEndDate.setTheme(R.style.CustomMaterialDatePicker);

                MaterialDatePicker<Long> datePicker = builderEndDate.build();

                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
                        // Format the selected date
                        Date selectedDate = new Date(selection);


                        endDate = new Date(selection);

                        if (startDate != null) {
                            String formattedDate = dateFormat.format(startDate);

                            btnEndDate.setText(formattedDate);
                        }


                        // Check if the selected date is before the start date
                        if (startDate != null && selectedDate.compareTo(startDate) < 0) {
                            Toast.makeText(getContext(), getResources().getString(R.string.end_date_before_start_date), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Set the selected date as the button text
                        btnEndDate.setText(dateFormat.format(selectedDate));
                    }
                });

                datePicker.addOnNegativeButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        endDate = null;
                        btnEndDate.setText(getResources().getString(R.string.insert_end_date));
                    }
                });


                datePicker.show(getChildFragmentManager(), "date_picker");
            }
        });


        endDateSwitch = constraintLayout.findViewById(R.id.noEndDateSwitch); // replace with your switch id
        LinearLayout endDateLinearLayout = constraintLayout.findViewById(R.id.endDateLinearLayout);

        endDateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(buttonView.getWindowToken(), 0);
                }

                if (isChecked) {
                    endDateLinearLayout.setVisibility(View.GONE);
                } else {
                    endDateLinearLayout.setVisibility(View.VISIBLE);
                }
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(requireContext(), R.style.CustomMaterialDialog)
                        .setTitle(getResources().getString(R.string.going_back))
                        .setMessage(getResources().getString(R.string.unsaved_changes))
                        .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Respond to negative button press
                            }
                        })
                        .setPositiveButton(getResources().getString(R.string.go_back), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Respond to positive button press
                                // Navigate back
                                FragmentManager fragmentManager = getFragmentManager();
                                if (fragmentManager != null) {
                                    fragmentManager.popBackStack();
                                }
                            }
                        })
                        .create()
                        .show();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if the input is valid
                if (validateInput()) {
                    // Create a Bundle to hold the data
                    Bundle bundle = setBundle();
                    // Navigate to the next fragment
                    TreatmentFormMedicationsFragment treatmentFormMedicationsFragment = new TreatmentFormMedicationsFragment();
                    treatmentFormMedicationsFragment.setArguments(bundle);
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.nav_host_fragment_activity_main, treatmentFormMedicationsFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });

    }

    private boolean validateInput() {
        // Check if treatmentTarget is empty
        if (treatmentTarget.getText().toString().isEmpty()) {
            // Display error message and return false
            TextInputLayout treatmentTargetInputLayout = constraintLayout.findViewById(R.id.treatmentTargetInputLayout);
            treatmentTargetInputLayout.setError(getResources().getString(R.string.required_field));
            return false;
        }

        // Check if startDate is null
        if (startDate == null) {
            // Display error message and return false
            Toast.makeText(getContext(), getResources().getString(R.string.required_field), Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if endDateSwitch is checked
        if (!endDateSwitch.isChecked()) {
            // Check if endDate is null
            if (endDate == null) {
                // Display error message and return false
                Toast.makeText(getContext(), getResources().getString(R.string.required_field), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // If all checks pass, return true
        return true;
    }

    private Bundle setBundle() {
        Bundle bundle = new Bundle();

        String treatmentTargetString = treatmentTarget.getText().toString();

        treatment = new Treatment(treatmentTargetString, startDate, endDate);

        bundle.putParcelable("treatment", treatment);

        return bundle;
    }


}