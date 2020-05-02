package com.colorator.ColoratorOptions;

import com.colorator.R;
import com.colorator.customviews.MyRangeSeekbar;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ColorpickerDetectorArgsFragment extends DetectorArgsAbstractClass {
    // Todo: create ColorViewer - https://stackoverflow.com/questions/15878769/android-how-to-draw-2-directional-gradient
    // https://stackoverflow.com/questions/15720681/cannot-get-color-hsvtocolor-to-work-on-android
    public static final String TAG = "ColorPickerFragment";
    private View mRootView;
    private MyRangeSeekbar mHRangeSeekbar, mSRangeSeekbar, mVRangeSeekbar;
    private int mMinH, mMaxH, mMinS, mMaxS, mMinV, mMaxV;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "called onCreateView");
        super.onCreate(savedInstanceState);
        mRootView = inflater.inflate(R.layout.colorpicker_detector_options, container, false);
        setRageSeekbars();
        return mRootView;
    }

    private void setRageSeekbars() {
        mHRangeSeekbar = mRootView.findViewById(R.id.HValuesSeekbar);
        mMaxH = mHRangeSeekbar.mStartMax;
        mMinH = mHRangeSeekbar.mStartMin;
        mHRangeSeekbar.setFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                mMaxH = maxValue.intValue();
                mMinH = minValue.intValue();
            }
        });

        mSRangeSeekbar = mRootView.findViewById(R.id.SValuesSeekbar);
        mMaxS = mSRangeSeekbar.mStartMax;
        mMinS = mSRangeSeekbar.mStartMin;
        mSRangeSeekbar.setFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                mMaxS = maxValue.intValue();
                mMinS = minValue.intValue();
            }
        });

        mVRangeSeekbar = mRootView.findViewById(R.id.VValuesSeekbar);
        mMaxV = mVRangeSeekbar.mStartMax;
        mMinV = mVRangeSeekbar.mStartMin;
        mVRangeSeekbar.setFinalValueListener(new OnRangeSeekbarFinalValueListener() {
            @Override
            public void finalValue(Number minValue, Number maxValue) {
                mMaxV = maxValue.intValue();
                mMinV = minValue.intValue();
            }
        });
    }

    @Override
    public Map getDetectorsArgs() {
        HashMap<String, Integer> range = new HashMap<String, Integer>();
        range.put("minS", mMinS);
        range.put("maxS", mMaxS);
        range.put("minH", mMinH);
        range.put("maxH", mMaxH);
        range.put("minV", mMinV);
        range.put("maxV", mMaxV);
        List<HashMap<String, Integer>> allRanges = new ArrayList<HashMap<String, Integer>>();
        allRanges.add(range);
        HashMap<String, Object> detectorArgs = new HashMap<String, Object>();
        detectorArgs.put("Ranges", allRanges);
        return (Map) detectorArgs;
    }
}
