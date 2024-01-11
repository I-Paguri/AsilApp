package it.uniba.dib.sms232417.asilapp.patientsFragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.OperationItemRecyclerViewAdapter;
import it.uniba.dib.sms232417.asilapp.patientsFragments.placeholder.OperationItem;

/**
 * A fragment representing a list of Items.
 */
public class LastOperationItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LastOperationItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static LastOperationItemFragment newInstance(int columnCount) {
        LastOperationItemFragment fragment = new LastOperationItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_last_operation_item_list, container, false);

        // Initialize the RecyclerView
       RecyclerView recyclerView = view.findViewById(R.id.list);

        // Create a list of OperationItem

        //Data 05/05/2020
        List<OperationItem> operationItems = new ArrayList<>();
        operationItems.add(new OperationItem("Probiotico per la gola", 56, "04/01/2024", "1", "Farmaci"));
        operationItems.add(new OperationItem("Preservativi durex comfort", 89, "04/01/2024",   "1", "Farmaci"));
        operationItems.add(new OperationItem("Flomax", 23, "04/01/2024", "1", "Farmaci"));
        operationItems.add(new OperationItem("Oki Task", 89, "04/01/2024", "1", "Farmaci"));
        operationItems.add(new OperationItem("Puntulil", 89, "04/01/2024", "1", "Farmaci"));
        operationItems.add(new OperationItem("Citromax", 89, "04/01/2024", "1", "Farmaci"));
        operationItems.add(new OperationItem("Spokkimax", 89, "04/01/2024",  "1", "Farmaci"));
        operationItems.add(new OperationItem("Spider-man", 89, "04/01/2024", "1", "Farmaci"));


        // Set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new OperationItemRecyclerViewAdapter(operationItems));

        return view;
    }

}


