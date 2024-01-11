package it.uniba.dib.sms232417.asilapp.patientsFragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.uniba.dib.sms232417.asilapp.adapters.OperationItemRecyclerViewAdapter;
import it.uniba.dib.sms232417.asilapp.adapters.ProductAdapter;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.patientsFragments.placeholder.OperationItem;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProductFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductFragment newInstance(String param1, String param2) {
        ProductFragment fragment = new ProductFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        List<OperationItem> productItem = new ArrayList<>();
        productItem.add(new OperationItem("Probiotico per la gola", 56, "04/01/2024", "1", "Farmaci"));
        productItem.add(new OperationItem("Preservativi durex comfort", 89, "04/01/2024",   "1", "Farmaci"));
        productItem.add(new OperationItem("Flomax", 23, "04/01/2024", "1", "Farmaci"));
        productItem.add(new OperationItem("Oki Task", 89, "04/01/2024", "1", "Farmaci"));
        productItem.add(new OperationItem("Puntulil", 89, "04/01/2024", "1", "Farmaci"));
        productItem.add(new OperationItem("Citromax", 89, "04/01/2024", "1", "Farmaci"));
        productItem.add(new OperationItem("Spokkimax", 89, "04/01/2024",  "1", "Farmaci"));
        productItem.add(new OperationItem("Spider-man", 89, "04/01/2024", "1", "Farmaci"));


        // Set up the RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(new ProductAdapter(productItem));


        // Set up the RecyclerView


        return view;

    }
}