package it.uniba.dib.sms232417.asilapp.patientsFragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import it.uniba.dib.sms232417.asilapp.patientsFragments.ProductFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.TreatmentFragment;


public class ExpensesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Toolbar toolbar;

    public ExpensesFragment() {
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // This is called when the fragment is associated with its context.
        // You can use this method to initialize essential components of the fragment that rely on the context.
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        toolbar = requireActivity().findViewById(R.id.toolbar);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        // Show home button
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        BadgeDrawable badgeDrawable = BadgeDrawable.create(requireContext());
        badgeDrawable.setNumber(10);


        // Set toolbar title
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Spese");
        // Change toolbar title text color
        toolbar.setTitleTextColor(getResources().getColor(R.color.md_theme_light_surface));

        MaterialCardView cardViewProducts = view.findViewById(R.id.cardViewProducts);
        cardViewProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductFragment productFragment = new ProductFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, productFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        MaterialCardView cardViewOperations = view.findViewById(R.id.cardViewOperations);
        cardViewOperations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LastOperationItemFragment lastOperationFragment = new LastOperationItemFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, lastOperationFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        MaterialCardView cardViewAnalyses = view.findViewById(R.id.cardViewAnalyses);
        cardViewAnalyses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnalysesExpensesFragment analysesExpensesFragment = new AnalysesExpensesFragment();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, analysesExpensesFragment);
                transaction.addToBackStack(null);
                transaction.commit();


            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ottieni il riferimento a textViewBalance
        TextView textViewBalance = view.findViewById(R.id.textViewBalance);

        // Imposta il valore di textViewBalance a 500 €
        textViewBalance.setText("500.00 € ");

        //otteniamo il riferimento alla progress bar
        ProgressBar progressBar = view.findViewById(R.id.progressBar);
        // Imposta il valore iniziale della ProgressBar a 0
        progressBar.setProgress(0);

        /* Non funziona animazione perchè
        arriva fino alla fine e non si riesce ad impostare
        valore finale
        Animator animator = AnimatorInflater.loadAnimator(getContext(), R.animator.progress_anim);
        animator.setTarget(progressBar);
        // Avvia l'animazione
        animator.start();

         */
        // Imposta il valore della ProgressBar qui.


        progressBar.setProgress(75); // Imposta il progresso al 75%

    }


}