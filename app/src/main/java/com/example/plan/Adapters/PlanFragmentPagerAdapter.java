package com.example.plan.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.plan.Fragments.Days.FridayFragment;
import com.example.plan.Fragments.Days.MondayFragment;
import com.example.plan.Fragments.Days.ThursdayFragment;
import com.example.plan.Fragments.Days.TuesdayFragment;
import com.example.plan.Fragments.Days.WednesdayFragment;
import com.example.plan.R;

public class PlanFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private String tabTitles[];

    public PlanFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        tabTitles  = new String[] {context.getResources().getString(R.string.day_monday) ,
                context.getResources().getString(R.string.day_tuesday),
                context.getResources().getString(R.string.day_wednesday),
                context.getResources().getString(R.string.day_thursday),
                context.getResources().getString(R.string.day_friday)};
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new MondayFragment();
        } else if (position == 1){
            return new TuesdayFragment();
        } else if (position == 2){
            return new WednesdayFragment();
        } else if (position == 3){
            return new ThursdayFragment();
        } else {
            return new FridayFragment();
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
