package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.RecyclerListViewAdapter;
import it.uniba.dib.sms232417.asilapp.utilities.listItem;

public class MyPatientsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerListViewAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_patients, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        List<listItem> list = new LinkedList<>();
        list.add(new listItem("Police","Don't stand so close to me"));
        list.add(new listItem("Rihanna","Love the way you lie"));
        list.add(new listItem("Marco Mengoni","L'essenziale"));

        // Set up the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RecyclerListViewAdapter(list);
        recyclerView.setAdapter(adapter);

        return view;
    }
}