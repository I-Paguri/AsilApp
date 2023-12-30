package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BulletSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import it.uniba.dib.sms232417.asilapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TreatmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TreatmentFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TreatmentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TreatmentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TreatmentFragment newInstance(String param1, String param2) {
        TreatmentFragment fragment = new TreatmentFragment();
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
        return inflater.inflate(R.layout.fragment_treatment, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the parent layout
        LinearLayout parentLayout = view.findViewById(R.id.linearLayoutCardView);

        // Convert dp to pixels
        int dp16 = (int) (16 * Resources.getSystem().getDisplayMetrics().density);
        int dp8 = (int) (8 * Resources.getSystem().getDisplayMetrics().density);
        int dp2 = (int) (2 * Resources.getSystem().getDisplayMetrics().density);

        for (int i = 0; i < 3; i++) {
            // Create a new instance of MaterialCardView
            MaterialCardView cardView = new MaterialCardView(getContext());

            // Set the layout parameters for the MaterialCardView
            LinearLayout.LayoutParams cardViewParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cardViewParams.setMargins(dp16, dp8, dp16, dp8);
            cardView.setLayoutParams(cardViewParams);

            // Create a new LinearLayout
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            // Create TextViews and apply the same styles as in the XML file
            TextView titleText = new TextView(getContext());
            titleText.setText("Obiettivo della terapia");
            titleText.setTextAppearance(getContext(), R.style.CardViewTitleText);


            TextView dateText = new TextView(getContext());
            dateText.setText("01/01/2024 - 01/02/2024");
            dateText.setTextAppearance(getContext(), R.style.CardViewDates);

            TextView subtitleText = new TextView(getContext());
            subtitleText.setText("Farmaci");
            subtitleText.setTextAppearance(getContext(), R.style.CardViewSubtitleText);

            TextView regularText = new TextView(getContext());
            regularText.setText("I miei farmaci");
            regularText.setTextAppearance(getContext(), R.style.RegularText);

            TextView noteText = new TextView(getContext());
            noteText.setText("Note");
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
        }
    }

}