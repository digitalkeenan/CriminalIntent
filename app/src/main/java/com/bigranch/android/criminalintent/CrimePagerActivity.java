package com.bigranch.android.criminalintent;

import android.app.FragmentManager;
import android.app.Fragment;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private ArrayList<Crime> mCrimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        mCrimes = CrimeLab.get(this).getCrimes();
        FragmentManager fm = getFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public int getCount() {
                return mCrimes.size();
            }
            @Override
            public Fragment getItem(int pos) {
                Crime crime = mCrimes.get(pos);
                return CrimeFragment.newInstance(crime.getId());
            }
        });

        //ToDo: Do I need to implement removeOnPageChangeListener?
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) { }
            public void onPageScrolled(int pos, float posOffset, int posOffsetPixels) { }

            public void onPageSelected(int pos) {
                Crime crime = mCrimes.get(pos);
                if (crime.getTitle() != null) {
                    setTitle(crime.getTitle());
                }
            }
        });

        UUID crimeId = (UUID) getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
