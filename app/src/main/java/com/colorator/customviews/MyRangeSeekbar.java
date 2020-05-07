package com.colorator.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.colorator.R;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarFinalValueListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar;

public class MyRangeSeekbar extends LinearLayout {
    LayoutInflater mInflater;
    public CrystalRangeSeekbar mRangeSeekbar;
    public int mStartMin, mStartMax;
    public OnRangeSeekbarChangeListener mExternalOnChangeListener;

    public MyRangeSeekbar(Context context) {
        super(context);
        mInflater = LayoutInflater.from(context);
        init(context, null);
    }

    public MyRangeSeekbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mInflater = LayoutInflater.from(context);
        init(context, attrs);
    }

    public MyRangeSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = LayoutInflater.from(context);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View rootView = mInflater.inflate(R.layout.my_range_seekbar, this, true);
        mRangeSeekbar = (CrystalRangeSeekbar) rootView.findViewById(R.id.basic_seekbar);
        final TextView tvMin = (TextView) rootView.findViewById(R.id.textMin);
        final TextView tvMax = (TextView) rootView.findViewById(R.id.textMax);
        setChangeListener(tvMin, tvMax);
        setAttrs(context, attrs, rootView);
    }

    public void setFinalValueListener(OnRangeSeekbarFinalValueListener finalValueListener) {
        mRangeSeekbar.setOnRangeSeekbarFinalValueListener(finalValueListener);
    }

    public void setOnChangeValueListener(OnRangeSeekbarChangeListener onRangeSeekbarChangeListener) {
        mExternalOnChangeListener = onRangeSeekbarChangeListener;
    }

    private void setAttrs(Context context, AttributeSet attrs, View rootView) {
        CrystalRangeSeekbar rangeSeekbar = rootView.findViewById(R.id.basic_seekbar);
        TextView titleTextView = rootView.findViewById(R.id.titleTextView);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MyRangeSeekbar,
                0, 0);
        final int min = typedArray.getInteger(R.styleable.MyRangeSeekbar_min, 0);
        final int max = typedArray.getInteger(R.styleable.MyRangeSeekbar_max, 0);
        final int startMin = typedArray.getInteger(R.styleable.MyRangeSeekbar_startMin, 0);
        final int startMax = typedArray.getInteger(R.styleable.MyRangeSeekbar_startMax, 0);
        mStartMin = startMin;
        mStartMax = startMax;
        final String titleText = typedArray.getString(R.styleable.MyRangeSeekbar_titleText);
        rangeSeekbar.setMinValue(min).setMaxValue(max)
                .setMinStartValue(startMin).setMaxStartValue(startMax).apply();
        titleTextView.setText(titleText);
        typedArray.recycle();
    }


    private void setChangeListener(final TextView tvMin, final TextView tvMax) {
        mRangeSeekbar.setOnRangeSeekbarChangeListener(new OnRangeSeekbarChangeListener() {
            @Override
            public void valueChanged(Number minValue, Number maxValue) {
                tvMin.setText(String.valueOf(minValue));
                tvMax.setText(String.valueOf(maxValue));
                if (mExternalOnChangeListener != null) {
                    mExternalOnChangeListener.valueChanged(minValue, maxValue);
                }
            }
        });
    }
}
