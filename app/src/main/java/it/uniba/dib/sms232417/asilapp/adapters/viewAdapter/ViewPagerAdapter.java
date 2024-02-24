package it.uniba.dib.sms232417.asilapp.adapters.viewAdapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import it.uniba.dib.sms232417.asilapp.fragments.MeasurementsFragment;
import it.uniba.dib.sms232417.asilapp.fragments.TreatmentFragment;
import it.uniba.dib.sms232417.asilapp.thread_connection.NoConnectionFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private Bundle bundle;

    public ViewPagerAdapter(@NonNull Fragment fragment, Bundle bundle) {
        super(fragment);
        this.bundle = bundle;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                Fragment selectedFragment;
                boolean noConnection = bundle.getBoolean("noConnection");
                if(noConnection){
                    selectedFragment = new NoConnectionFragment();
                }else {
                    selectedFragment = new MeasurementsFragment();
                    selectedFragment.setArguments(bundle);
                }

                return selectedFragment;
            case 1:
                TreatmentFragment treatmentFragment = new TreatmentFragment();
                treatmentFragment.setArguments(bundle);
                return treatmentFragment;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}