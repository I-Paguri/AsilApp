package it.uniba.dib.sms232417.asilapp.doctor.fragments;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import it.uniba.dib.sms232417.asilapp.MainActivity;
import it.uniba.dib.sms232417.asilapp.R;

public class MyAccountFragment extends Fragment {
    Toolbar toolbar;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    BottomNavigationView bottomNavigationView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_my_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomNavigationView = requireActivity().findViewById(R.id.nav_view);

        toolbar = requireActivity().findViewById(R.id.toolbar);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        // Show home button
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set home icon as back button
        Drawable homeIcon = getResources().getDrawable(R.drawable.home, null);
        // Set color filter
        homeIcon.setColorFilter(getResources().getColor(R.color.md_theme_light_surface), PorterDuff.Mode.SRC_ATOP);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeAsUpIndicator(homeIcon);

        // Set toolbar title
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.my_account));
        // Change toolbar title text color
        toolbar.setTitleTextColor(getResources().getColor(R.color.md_theme_light_surface));

        // Set navigation click listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Navigate to HomeFragment
                bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.nav_host_fragment_activity_main, new HomeFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser()!=null) {
            TextView name = getView().findViewById(R.id.txt_my_account);
            db.collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                            .get();
            name.setText(mAuth.getCurrentUser().getEmail());
        } else {
             RelativeLayout relativeLayout = getView().findViewById(R.id.txt_notLoggedUser);
             relativeLayout.setVisibility(View.VISIBLE);
        }

    }
}