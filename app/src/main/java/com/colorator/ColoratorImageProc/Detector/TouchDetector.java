package com.colorator.ColoratorImageProc.Detector;

import android.view.MotionEvent;

import com.colorator.ColoratorImageProc.ColoratorMatManager;
import com.colorator.ColoratorImageProc.Detector.DetectorHelpers.MainColorsFinder;
import com.colorator.utils.ColorRange;
import com.colorator.utils.CommonScalars;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

public class TouchDetector extends RelativeDetector {
    private Mat mCircleMask, mOutput;
    private boolean mIsTouched = false;
    private Point mLastTouchPoint = new Point();
    static private final int FINGER_RADIUS = 50;

    public TouchDetector(ColoratorMatManager coloratorMatManager) {
        super(coloratorMatManager);
    }

    @Override
    public void onTouch(MotionEvent event, Point actualTouchPoint) {
        synchronized (this) {
            mLastTouchPoint = actualTouchPoint;
            setDetectedColor();
            setIsTouched(event);
        }
    }

    private void setDetectedColor() {
        if (mCircleMask.height() != mBlurredImage.height() || mCircleMask.width() != mBlurredImage.width()) {
            mCircleMask.release();
            mCircleMask = mColoratorMatManager.allocateNewMat(CvType.CV_8UC1);
        }
        mCircleMask.setTo(CommonScalars.Zeros);
        Imgproc.circle(mCircleMask, mLastTouchPoint, FINGER_RADIUS, CommonScalars.Ones, -1);
        ColorRange mainColor = MainColorsFinder.find(mBlurredImage,
                mCircleMask,
                MainColorsFinder.METHOD.TOP_SINGLE_COLOR,
                0).getColorRanges().get(0);
        super.setDetectedColor(mainColor.getCenterColor());
    }

    private void setIsTouched(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mIsTouched = true;
                break;
            case MotionEvent.ACTION_UP:
                mIsTouched = false;
            default:
                break;
        }
    }

    @Override
    public Mat detect(Mat inputImage) {
        synchronized (this) {
            mOutput = super.detect(inputImage);
            if (mIsTouched) {
                Core.bitwise_or(mOutput, mCircleMask, mOutput);
            }
        }
        return mOutput;
    }

    @Override
    void allocationsOfFirstDetection() {
        super.allocationsOfFirstDetection();
        mCircleMask = mColoratorMatManager.allocateNewMat(CvType.CV_8UC1);
    }
}