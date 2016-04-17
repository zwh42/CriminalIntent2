package com.example.zhaowenhao.criminalintent2;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.Date;
import java.util.List;
import java.util.UUID;


public class CrimeFragment extends Fragment {

    public static final String EXTRA_CRIME_ID = "CRIME_ID";
    public static final String TAG = "CrimeFragment";

    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final String  DIALOG_IMAGE = "image";

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private ImageButton mPhotoButton;
    private Button mSuspectButton;
    private Button mReportButton;
    private ImageView mPhotoView;


    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        UUID crimeID = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){


        View v = inflater.inflate(R.layout.fragment_crime, parent, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) v.findViewById(R.id.crime_date);


        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);

            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_imageButton);
        mPhotoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
                        startActivityForResult(i, REQUEST_PHOTO);
                    }
                }
        );

        mPhotoView = (ImageView) v.findViewById(R.id.crime_imageView);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo p = mCrime.getPhoto();
                if (p == null) return;

                FragmentManager fm = getActivity().getSupportFragmentManager();
                String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
                ImageFragment imageFragment = ImageFragment.newInstance(path);
                imageFragment.show(fm, DIALOG_IMAGE);
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_reportButton);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        mSuspectButton = (Button) v.findViewById(R.id.crime_suspectButton);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                PackageManager pm = getActivity().getPackageManager();
                List<ResolveInfo> activities = pm.queryIntentActivities(i, 0);
                if (activities.size() <= 0){
                    return;
                }
                Log.d(TAG, "select contact intent started");
                startActivityForResult(i, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null){ 
            mSuspectButton.setText(mCrime.getSuspect());
        }

        return v;
    }


    @Override
    public void onStart(){
        super.onStart();
        showPhoto();
    }

    @Override
    public void onStop(){
        super.onStop();
        PictureUtils.cleanImageView(mPhotoView);
    }

    @Override
    public void onPause(){
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode == REQUEST_DATE){
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        } else  if (requestCode == REQUEST_PHOTO){
            String filename = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            if (filename != null){
                Log.i(TAG, "pic filename: " + filename + " loaded");
                Photo p = new Photo(filename);
                mCrime.setPhoto(p);
                Log.i(TAG, "Crime: " + mCrime.getTitle() + " has a photo.");
                showPhoto();
            }
        }else if (requestCode == REQUEST_CONTACT){
            Uri contactUri = data.getData();
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };

            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            if (c.getCount() == 0){
                c.close();
                return;
            }

            c.moveToFirst();
            String suspect =c.getString(0);
            Log.d(TAG, "suspect name: " + suspect);
            mCrime.setSuspect(suspect);
            Log.d(TAG, "get suspect name" + mCrime.getSuspect());
            mSuspectButton.setText(suspect);
            c.close();
        }
    }

    public void updateDate(){
        mDateButton.setText(DateFormat.format("yyyy/MM/dd E hh:mm", mCrime.getDate()));
    }

    private void showPhoto(){
        Photo p = mCrime.getPhoto();
        BitmapDrawable b = null;
        if (p != null){
            String path = getActivity().getFileStreamPath(p.getFilename()).getAbsolutePath();
            Log.i(TAG, "photo file absoulute path: " + path);
            b = PictureUtils.getScaleDrawable(getActivity(), path);
        }

        mPhotoView.setImageDrawable(b);
    }

    private String getCrimeReport(){
        String solvedString = null;
        if (mCrime.isSolved()){
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null){
            suspect = getString(R.string.crime_report_no_suspect);
        }else{
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }


}
