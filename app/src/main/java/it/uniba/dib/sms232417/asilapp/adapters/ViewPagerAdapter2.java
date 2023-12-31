package it.uniba.dib.sms232417.asilapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import it.uniba.dib.sms232417.asilapp.doctor.fragments.MeasurementsFragment;
import it.uniba.dib.sms232417.asilapp.doctor.fragments.TreatmentFragment;
import it.uniba.dib.sms232417.asilapp.patinentsFragments.ExpensesFragment;

public class ViewPagerAdapter2 extends FragmentStateAdapter {
    public ViewPagerAdapter2(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MeasurementsFragment();
            case 1:
                return new TreatmentFragment();
            case 2:
                return new ExpensesFragment();
            default:
                return new MeasurementsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}