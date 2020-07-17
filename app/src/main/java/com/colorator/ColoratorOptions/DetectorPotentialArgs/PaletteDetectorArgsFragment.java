package com.colorator.ColoratorOptions.DetectorPotentialArgs;

import com.colorator.R;
import com.colorator.customviews.PaletteView;
import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import org.opencv.core.Scalar;


public class PaletteDetectorArgsFragment extends DetectorArgsAbstractClass {
    public static final String TAG = "PaletteDetectorArgsFragment";
    private CrystalSeekbar mHueSeekbar;
    private int mH;
    private PaletteView mPaletteView;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "called onCreateView");
        super.onCreate(savedInstanceState);
        View mRootView = inflater.inflate(R.layout.palette_detector_options, container, false);
        mPaletteView = mRootView.findViewById(R.id.hsv_preview);
        mHueSeekbar = mRootView.findViewById(R.id.hue_seekbar);
        mH = 0;
        mPaletteView.setInitialParams(mH, 0, 255, 0, 255);
        setHueSeekbar();
        return mRootView;
    }


    private void setHueSeekbar() {
        mHueSeekbar.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number value) {
                mH = value.intValue();
                mPaletteView.setHue(mH);
            }
        });
    }

    @Override
    public Object getDetectorsArgs() {
        return new Scalar(mH, mPaletteView.getSelectedSat(), mPaletteView.getSelectedVal());
    }
}
