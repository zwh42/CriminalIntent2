package com.example.zhaowenhao.criminalintent2;


import android.support.v4.app.Fragment;

/**
 * Created by zhaowenhao on 16/3/16.
 */
public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new CrimeListFragment();
    };

}
