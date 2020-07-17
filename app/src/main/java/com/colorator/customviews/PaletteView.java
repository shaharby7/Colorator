package com.colorator.customviews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.colorator.R;

public class PaletteView extends View {
    private int mViewWidth, mViewHeight, mSquareSize;
    private float mHue, mMinSat, mMaxSat, mMinVal, mMaxVal, mTouchedX, mTouchedY;
    private double mHPortion = 0.5;
    private double mSPortion = 255;
    private double mVPortion = 255;
    private Paint mGradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
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

    @SuppressLint("ResourceAsColor")
    private void init(Context context, AttributeSet attrs) {
        super.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mCirclePaint.setColor(R.color.colorPrimary);
        mCirclePaint.setStrokeWidth(3);
        mCirclePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mViewWidth = w;
        mViewHeight = h;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setInitialParams(int hue, int minSat, int maxSat, int minVal, int maxVal) {
        mHue = (float) (hue / mHPortion);
        mMinSat = (float) (minSat / mSPortion);
        mMaxSat = (float) (maxSat / mSPortion);
        mMinVal = (float) (minVal / mVPortion);
        mMaxVal = (float) (maxVal / mVPortion);
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
        mHue = (float) (hue / mHPortion);
        if (!hasNeverBeenDrawned) {
            refreshSatGradient();
        }
    }

    public void setSatBounds(int minSat, int maxSat) {
        mMinSat = (float) minSat / 100;
        mMaxSat = (float) maxSat / 100;
        if (!hasNeverBeenDrawned) {
            refreshSatGradient();
        }
    }

    public void setValBounds(int minVal, int maxVal) {
        mMinVal = (float) minVal / 100;
        mMaxVal = (float) maxVal / 100;
        if (!hasNeverBeenDrawned) {
            refreshValGradient();
        }
    }

    public float getSelectedSat() {
        return (float) (((mTouchedX / getWidth()) * (mMaxSat - mMinSat) + mMinSat) * mSPortion);
    }

    public float getSelectedVal() {
        return (float) (((1 - (mTouchedY / getHeight())) * (mMaxVal - mMinVal) + mMinVal) * mVPortion);
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
        if (mTouchedY != 0 && mTouchedX != 0) {
            canvas.drawCircle(mTouchedX, mTouchedY, 50, mCirclePaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mSquareSize = Math.min(widthMeasureSpec, heightMeasureSpec);
        mTouchedX = (float) (mSquareSize / 2);
        mTouchedY = (float) (mSquareSize / 2);
        super.onMeasure(mSquareSize, mSquareSize);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        mTouchedX = event.getX();
        mTouchedY = event.getY();
        invalidate();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            performClick();
        }
        return true;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }
}
