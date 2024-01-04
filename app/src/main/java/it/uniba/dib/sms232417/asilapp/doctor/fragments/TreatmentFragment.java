package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import it.uniba.dib.sms232417.asilapp.R;


public class TreatmentFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_treatment, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Get the parent layout
        LinearLayout parentLayout = view.findViewById(R.id.linearLayoutCardView);

        // Convert dp to pixels
        int dp85 = (int) (85 * Resources.getSystem().getDisplayMetrics().density);
        int dp16 = (int) (16 * Resources.getSystem().getDisplayMetrics().density);
        int dp8 = (int) (8 * Resources.getSystem().getDisplayMetrics().density);
        int dp2 = (int) (2 * Resources.getSystem().getDisplayMetrics().density);

        int numberOfCards = 6;

        for (int i = 0; i < numberOfCards; i++) {
            // Create a new instance of MaterialCardView
            MaterialCardView cardView = new MaterialCardView(getContext());


            // Set the layout parameters for the MaterialCardView
            LinearLayout.LayoutParams cardViewParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            // Set the margins for the MaterialCardView
            if (i == numberOfCards - 1) {
                // last card has bigger bottom margin
                cardViewParams.setMargins(dp16, dp8, dp16, dp85);
            } else {
                if (i == 0) {
                    // first card has bigger top margin
                    cardViewParams.setMargins(dp16, dp16, dp16, dp8);
                } else {
                    cardViewParams.setMargins(dp16, dp8, dp16, dp8);
                }
            }
            cardView.setLayoutParams(cardViewParams);

            // Create a new LinearLayout
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            // Create TextViews and apply the same styles as in the XML file
            TextView titleText = new TextView(getContext());
            titleText.setText(getResources().getString(R.string.treatment_goal));
            titleText.setTextAppearance(getContext(), R.style.CardViewTitleText);


            TextView dateText = new TextView(getContext());
            dateText.setText("01/01/2024 - 01/02/2024");
            dateText.setTextAppearance(getContext(), R.style.CardViewDates);

            TextView subtitleText = new TextView(getContext());
            subtitleText.setText(getResources().getString(R.string.medicines));
            subtitleText.setTextAppearance(getContext(), R.style.CardViewSubtitleText);

            TextView regularText = new TextView(getContext());
            regularText.setText("I miei farmaci");
            regularText.setTextAppearance(getContext(), R.style.RegularText);

            TextView noteText = new TextView(getContext());
            noteText.setText(getResources().getString(R.string.notes));
            noteText.setTextAppearance(getContext(), R.style.CardViewSubtitleText);

            TextView noteContentText = new TextView(getContext());
            noteContentText.setText("Misurare la pressione arteriosa 2 volte al giorno");
            noteContentText.setTextAppearance(getContext(), R.style.RegularText);

            // Create a ViewGroup.MarginLayoutParams object for the TextViews
            ViewGroup.MarginLayoutParams textParamsTitle = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            textParamsTitle.setMargins(dp16, dp8, dp16, 0);

            // Set the layout parameters for the TextViews
            titleText.setLayoutParams(textParamsTitle);

            ViewGroup.MarginLayoutParams textParamsRegularText = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            textParamsRegularText.setMargins(dp16, dp2, dp16, dp16);

            dateText.setLayoutParams(textParamsRegularText);

            ViewGroup.MarginLayoutParams textParamsSubtitle = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            textParamsSubtitle.setMargins(dp16, dp2, dp16, 0);

            subtitleText.setLayoutParams(textParamsSubtitle);

            regularText.setLayoutParams(textParamsRegularText);

            noteText.setLayoutParams(textParamsSubtitle);

            noteContentText.setLayoutParams(textParamsRegularText);

            // Add the TextViews to the LinearLayout
            linearLayout.addView(titleText);
            linearLayout.addView(dateText);
            linearLayout.addView(subtitleText);
            linearLayout.addView(regularText);
            linearLayout.addView(noteText);
            linearLayout.addView(noteContentText);

            // Add the LinearLayout to the MaterialCardView
            cardView.addView(linearLayout);

            // Add the MaterialCardView to the parent layout
            parentLayout.addView(cardView);


            final ExtendedFloatingActionButton fab = view.findViewById(R.id.fab);

            // register the nestedScrollView from the main layout
            NestedScrollView nestedScrollView = view.findViewById(R.id.nestedScrollView);

            // handle the nestedScrollView behaviour with OnScrollChangeListener
            // to extend or shrink the Extended Floating Action Button
            nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    // the delay of the extension of the FAB is set for 12 items
                    if (scrollY > oldScrollY + 12 && fab.isExtended()) {
                        fab.shrink();
                    }

                    // the delay of the extension of the FAB is set for 12 items
                    if (scrollY < oldScrollY - 12 && !fab.isExtended()) {
                        fab.extend();
                    }

                    // if the nestedScrollView is at the first item of the list then the
                    // extended floating action should be in extended state
                    if (scrollY == 0) {
                        fab.extend();
                    }
                }
            });

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TreatmentFormGeneralFragment treatmentFormGeneralFragment = new TreatmentFormGeneralFragment();
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.nav_host_fragment_activity_main, treatmentFormGeneralFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }



    }
}
