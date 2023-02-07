package com.example.customer_dinein;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.jetbrains.annotations.NotNull;

public class TabAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;

    public TabAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        return DynamicFragment.addfrag(position);
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
