package com.example.zhaowenhao.criminalintent2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.UUID;

public class CrimePagerActivity extends FragmentActivity{
    private ViewPager mViewPager;
    private ArrayList<Crime> mCrimes;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);
        setContentView(mViewPager);

        mCrimes = CrimeLab.get(getApplicationContext()).getCrimes();

        FragmentManager fm = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentPagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getID());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        mViewPager.setOnPageChangeListener(
                new ViewPager.OnPageChangeListener(){
                    public void onPageScrollStateChanged(int state){}
                    public void onPageScrolled(int pos, float posOffset, int posOffsetPixels){}
                    public void onPageSelected(int pos){
                        Crime crime = mCrimes.get(pos);

                        if(crime.getTitle() != null){
                            setTitle(crime.getTitle());
                        }
                    }
                }
        );


        UUID crimeId = (UUID) getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
        for(int i = 0; i < mCrimes.size(); i++){
            if(mCrimes.get(i).getID().equals(crimeId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }


}