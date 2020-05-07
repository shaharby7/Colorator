package com.colorator.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class PaletteView extends View {
    private int mViewWidth, mViewHeight;
    public float mHue, mMinSat, mMaxSat, mMinVal, mMaxVal;
    private Paint mGradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private LinearGradient mSatGradient, mValGradient;
    private ComposeShader mFinalGradient;
    private Boolean hasNeverBeenDrawned = true;

    public PaletteView(Context context) {
        super(context);
        init(context, null);
    }

    public PaletteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public PaletteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        super.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mViewWidth = w;
        mViewHeight = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setInitialParams(int hue, int minSat, int maxSat, int minVal, int maxVal) {
        mHue = (float) hue;
        mMinSat = (float) minSat / 100;
        mMaxSat = (float) maxSat / 100;
        mMinVal = (float) minVal / 100;
        mMaxVal = (float) maxVal / 100;
    }

    private void refreshSatGradient() {
        int satFromColor = Color.HSVToColor(new float[]{mHue, mMinSat, 1});
        int satToColor = Color.HSVToColor(new float[]{mHue, mMaxSat, 1});
        mSatGradient = new LinearGradient(0, 0, mViewWidth, 0, satFromColor, satToColor, Shader.TileMode.CLAMP);
        mFinalGradient = new ComposeShader(mSatGradient, mValGradient, PorterDuff.Mode.MULTIPLY);
        mGradientPaint.setShader(mFinalGradient);
        invalidate();
    }

    private void refreshValGradient() {
        int valFromColor = Color.HSVToColor(new float[]{0, 0, mMaxVal});
        int valToColor = Color.HSVToColor(new float[]{0, 0, mMinVal});
        mValGradient = new LinearGradient(0, 0, 0, mViewHeight, valFromColor, valToColor, Shader.TileMode.CLAMP);
        mFinalGradient = new ComposeShader(mSatGradient, mValGradient, PorterDuff.Mode.MULTIPLY);
        mGradientPaint.setShader(mFinalGradient);
        invalidate();
    }

    public void setHue(int hue) {
        mHue = (float) hue;
        if (!hasNeverBeenDrawned) {
            refreshSatGradient();
        }
    }

    public void setSat(int minSat, int maxSat) {
        mMinSat = (float) minSat / 100;
        mMaxSat = (float) maxSat / 100;
        if (!hasNeverBeenDrawned) {
            refreshSatGradient();
        }
    }

    public void setVal(int minVal, int maxVal) {
        mMinVal = (float) minVal / 100;
        mMaxVal = (float) maxVal / 100;
        if (!hasNeverBeenDrawned) {
            refreshValGradient();
        }
    }

    private void createFirstTimeGradients() {
        int satFromColor = Color.HSVToColor(new float[]{mHue, mMinSat, 1});
        int satToColor = Color.HSVToColor(new float[]{mHue, mMaxSat, 1});
        mSatGradient = new LinearGradient(0, 0, mViewWidth, 0, satFromColor, satToColor, Shader.TileMode.CLAMP);
        refreshValGradient();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (hasNeverBeenDrawned) {
            createFirstTimeGradients();
            hasNeverBeenDrawned = false;
        }
        super.onDraw(canvas);
        canvas.drawRect(0, 0, mViewWidth, mViewHeight, mGradientPaint);
    }
}
