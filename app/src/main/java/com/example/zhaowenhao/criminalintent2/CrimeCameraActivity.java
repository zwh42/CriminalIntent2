package com.example.zhaowenhao.criminalintent2;

import android.support.v4.app.Fragment;

/**
 * Created by zhaowenhao on 16/4/10.
 */
public class CrimeCameraActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment(){
        return new CrimeCameraFragment();
    }
}
