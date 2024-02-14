package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import it.uniba.dib.sms232417.asilapp.R;
import it.uniba.dib.sms232417.asilapp.adapters.DatabaseAdapterPatient;
import it.uniba.dib.sms232417.asilapp.adapters.ViewPagerAdapter;
import it.uniba.dib.sms232417.asilapp.interfaces.OnProfileImageCallback;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class PatientFragment extends Fragment {
    private Toolbar toolbar;
    private String patientUUID, patientName, patientAge, user;
    private ViewPagerAdapter adapter;

    private DatabaseAdapterPatient dbAdapterPatient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patient, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        patientName = "Patient Name";
        patientAge = "Patient Age";

        String patientUUID = getArguments().getString("patientUUID"); // Ottieni l'UUID del paziente

        // Inizializza dbAdapterPatient
        dbAdapterPatient = new DatabaseAdapterPatient(getContext());

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
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.my_patients));
        // Change toolbar title text color
        toolbar.setTitleTextColor(getResources().getColor(R.color.md_theme_light_surface));

        // Set navigation click listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to previous fragment
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        TextView textName = view.findViewById(R.id.txtName_inputlayout);
        if (this.getArguments() != null) {
            patientUUID = this.getArguments().getString("patientUUID");
            patientName = this.getArguments().getString("patientName");
            patientAge = this.getArguments().getString("patientAge");
            user = this.getArguments().getString("user");
        }

        textName.setText(patientName);

        TextView textAge = view.findViewById(R.id.txtAge);
        textAge.setText(patientAge);

        ImageView imagePatient = view.findViewById(R.id.imgProfile);
        imagePatient.setImageResource(R.drawable.my_account);

        // Find the ViewPager2 in your layout
        ViewPager2 viewPager = view.findViewById(R.id.viewPager);

        // Create a bundle and put patientUUID into it
        Bundle bundle = new Bundle();
        bundle.putString("patientUUID", patientUUID);
        bundle.putString("patientName", patientName);
        bundle.putString("patientAge", patientAge);
        bundle.putString("user", user);

        // Create an adapter that knows which fragment should be shown on each page
        adapter = new ViewPagerAdapter(this, bundle);
        viewPager.setAdapter(adapter);

        // Find the TabLayout in your layout
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);

        // Connect the TabLayout with the ViewPager2
        // This will update the TabLayout when the ViewPager2 is swiped
        // Connect the TabLayout with the ViewPager2
        // This will update the TabLayout when the ViewPager2 is swiped
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText(R.string.measurements);
                            break;
                        case 1:
                            tab.setText(R.string.treatment);
                            break;
                    }
                }
        ).attach();

        // Check if there are arguments and if "selectedTab" is present
        if (getArguments() != null && getArguments().containsKey("selectedTab")) {
            int selectedTab = getArguments().getInt("selectedTab");
            TabLayout.Tab tab = tabLayout.getTabAt(selectedTab);

            if (tab != null) {
                tab.select();
                viewPager.setCurrentItem(selectedTab);
                adapter = new ViewPagerAdapter(this, getArguments());
                viewPager.setAdapter(adapter);

            }
        }


        // Check if there are arguments and if "selectedTab" is present
        if (getArguments() != null && getArguments().containsKey("selectedTab")) {
            int selectedTab = getArguments().getInt("selectedTab");
            TabLayout.Tab tab = tabLayout.getTabAt(selectedTab);
            if (tab != null) {
                tab.select();
                viewPager.post(() -> {
                    viewPager.setCurrentItem(selectedTab);

                });
            }
        }

        // Ottieni l'URL dell'immagine del profilo dal database
        dbAdapterPatient.getProfileImage(patientUUID, new OnProfileImageCallback() {
            @Override
            public void onCallback(String imageUrl) {
                // Check if the profile image URL exists and is not empty before loading it
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(getContext())
                            .load(imageUrl)
                            .circleCrop()
                            .into((ImageView) getView().findViewById(R.id.imgProfile));
                } else {
                    // If the profile image URL does not exist or is empty, load the default profile image
                    Glide.with(getContext())
                            .load(R.drawable.default_profile_image)
                            .circleCrop()
                            .into((ImageView) getView().findViewById(R.id.imgProfile));
                }
            }

            @Override
            public void onCallbackError(Exception e) {
                // If there is an error getting the profile image URL, load the default profile image
                Glide.with(getContext())
                        .load(R.drawable.default_profile_image)
                        .circleCrop()
                        .into((ImageView) getView().findViewById(R.id.imgProfile));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

}