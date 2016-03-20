package com.example.zhaowenhao.criminalintent2;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by zhaowenhao on 16/3/11.
 */
public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mAppContext;

    private ArrayList<Crime> mCrimes;

    private CrimeLab(Context appContext){
        mAppContext = appContext;
        mCrimes = new ArrayList<Crime>();
        for (int i = 0 ; i < 100; i++){
            Crime c  = new Crime();
            c.setSolved(i%2 == 0);
            c.setTitle("Crime #" + i);
            mCrimes.add(c);
        }
    }

    public static CrimeLab get (Context context){
        if(sCrimeLab == null){
            sCrimeLab = new CrimeLab(context.getApplicationContext());
        }
        return  sCrimeLab;
    }

    public ArrayList<Crime> getCrimes() {
        return mCrimes;
    }

    public Crime getCrime(UUID id){
        for(Crime c: mCrimes){
            if (c.getID() == id){
                return c;
            }
        }

        return null;
    }
}
