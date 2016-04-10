package com.example.zhaowenhao.criminalintent2;

import android.app.DialogFragment;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by zhaowenhao on 16/4/11.
 */
public class ImageFragment extends android.support.v4.app.DialogFragment {
    public static final String EXTRA_IMAGE_PATH = "criminal_intent_image_path";

    private ImageView mImageView;

    public static ImageFragment newInstance(String imagePath){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_IMAGE_PATH, imagePath);

        ImageFragment f = new ImageFragment();
        f.setArguments(args);
        f.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        mImageView = new ImageView(getActivity());
        String path = (String) getArguments().getSerializable(EXTRA_IMAGE_PATH);
        BitmapDrawable image = PictureUtils.getScaleDrawable(getActivity(), path);
        mImageView.setImageDrawable(image);
        return mImageView;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        PictureUtils.cleanImageView(mImageView);
    }


}
