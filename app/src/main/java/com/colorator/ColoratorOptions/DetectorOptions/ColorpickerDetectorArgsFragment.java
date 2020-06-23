package com.colorator.ColoratorOptions.DetectorOptions;

import com.colorator.R;
import com.colorator.customviews.MyRangeSeekbar;
import com.colorator.customviews.PaletteView;
import com.colorator.utils.ColorRange;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;


public class ColorpickerDetectorArgsFragment extends DetectorArgsAbstractClass {
    public static final String TAG = "ColorPickerFragment";
    private View mRootView;
    private MyRangeSeekbar mHRangeSeekbar, mSRangeSeekbar, mVRangeSeekbar;
    private CrystalSeekbar mCurrentHueSeekbar;
    private int mMinH, mMaxH, mCurrentH, mMinS, mMaxS, mMinV, mMaxV;
    private PaletteView mPaletteView;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "called onCreateView");
        super.onCreate(savedInstanceState);
        mRootView = inflater.inflate(R.layout.colorpicker_detector_options, container, false);
        setRageSeekbars();
        mPaletteView = mRootView.findViewById(R.id.hsv_preview);
        mCurrentHueSeekbar = mRootView.findViewById(R.id.current_hue_seekbar);
        mCurrentH = (int) ((mHRangeSeekbar.mStartMax + mHRangeSeekbar.mStartMin) / 2);
        mPaletteView.setInitialParams(mCurrentH, mSRangeSeekbar.mStartMin, mSRangeSeekbar.mStartMax,
                mVRangeSeekbar.mStartMin, mVRangeSeekbar.mStartMax);
        setCurrentHueSeekbar();
        return mRootView;
    }

    private void setRageSeekbars() {
        setHueRangeSeekbar();
        setSRangeSeekbar();
        setVRangeSeekbar();
    }

    private void setVRangeSeekbar() {
        mVRangeSeekbar = mRootView.findViewById(R.id.VValuesSeekbar);
        mMaxV = mVRangeSeekbar.mStartMax;
        mMinV = mVRangeSeekbar.mStartMin;
        mVRangeSeekbar.mRangeSeekbar.setBarGradientStart(Color.BLACK).setBarGradientEnd(Color.WHITE)
                .setBarColorMode(CrystalSeekbar.ColorMode.GRADIENT).setBarHighlightColor(R.color.veryTransparent)
                .apply();
        mVRangeSeekbar.setOnChangeValueListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                if (mPaletteView != null) {
                    mMaxV = maxValue.intValue();
                    mMinV = minValue.intValue();
                    mPaletteView.setVal(mMinV, mMaxV);
                }
            }
        });
    }

    private void setSRangeSeekbar() {
        mSRangeSeekbar = mRootView.findViewById(R.id.SValuesSeekbar);
        mSRangeSeekbar.mRangeSeekbar.setBarHighlightColor(R.color.veryTransparent)
                .setBarColorMode(CrystalSeekbar.ColorMode.GRADIENT).setBarGradientStart(Color.WHITE)
                .setBarGradientEnd(Color.HSVToColor(new float[]{mCurrentH, 1, 1})).apply();
        mMaxS = mSRangeSeekbar.mStartMax;
        mMinS = mSRangeSeekbar.mStartMin;
        mSRangeSeekbar.setOnChangeValueListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                if (mPaletteView != null) {
                    mMaxS = maxValue.intValue();
                    mMinS = minValue.intValue();
                    mPaletteView.setSat(mMinS, mMaxS);
                }
            }
        });
    }

    private void setHueRangeSeekbar() {
        mHRangeSeekbar = mRootView.findViewById(R.id.HValuesSeekbar);
        mMaxH = mHRangeSeekbar.mStartMax;
        mMinH = mHRangeSeekbar.mStartMin;
        mHRangeSeekbar.setOnChangeValueListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                mMaxH = maxValue.intValue();
                mMinH = minValue.intValue();
                if (mCurrentHueSeekbar != null) {
                    mCurrentHueSeekbar.setMinValue(mMinH).setMaxValue(mMaxH).apply();
                }
            }
        });
    }

    private void setCurrentHueSeekbar() {
        mCurrentHueSeekbar.setMinValue(mHRangeSeekbar.mStartMin)
                .setMaxValue(mHRangeSeekbar.mStartMax).setMinStartValue(mCurrentH).apply();
        mCurrentHueSeekbar.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number value) {
                setCurrentH(value.intValue());
            }
        });
    }

    private void setCurrentH(int value) {
        mCurrentH = value;
        mPaletteView.setHue(mCurrentH);
        mSRangeSeekbar.mRangeSeekbar.setMinStartValue(mMinS).setMaxStartValue(mMaxS)
                .setBarGradientEnd(Color.HSVToColor(new float[]{mCurrentH, 1, 1})).apply();
    }

    @Override
    public Object getDetectorsArgs() {
        return new ColorRange((int) (mMinH / 360. * 179.),
                (int) (mMaxH / 360. * 179.),
                (int) (mMinS / 100. * 255.),
                (int) (mMaxS / 100. * 255.),
                (int) (mMinV / 100. * 255.),
                (int) (mMaxV / 100. * 255.));
    }
}
