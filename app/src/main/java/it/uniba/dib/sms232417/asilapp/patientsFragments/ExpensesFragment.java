package it.uniba.dib.sms232417.asilapp.patientsFragments;

import android.content.Context;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

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
                LastOperationFragment lastOperationFragment = new LastOperationFragment();
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


}