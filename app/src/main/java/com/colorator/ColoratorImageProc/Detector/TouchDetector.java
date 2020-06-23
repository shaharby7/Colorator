package com.colorator.ColoratorImageProc.Detector;

import android.view.MotionEvent;

import com.colorator.ColoratorImageProc.ColoratorMatManager;
import com.colorator.utils.ColorRange;
import com.colorator.utils.CommonScalars;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

public class TouchDetector extends RangesDetector {
    private Mat mCircleMask, mTouchedSample, mLastFrame, mSingleChannel;
    private Core.MinMaxLocResult mMinMaxResultsForChannel;
    private boolean isFirstRun = true;
    private boolean mIsTouched = false;
    private Point mLastTouchPoint = new Point();
    private double[] mMinColor = new double[3];
    private double[] mMaxColor = new double[3];
    static private final int FINGER_RADIUS = 50;

    public TouchDetector(ColoratorMatManager coloratorMatManager) {
        super(coloratorMatManager);
    }

    @Override
    public void onTouch(MotionEvent event, Point actualTouchPoint) {
        synchronized (this) {
            mLastTouchPoint = actualTouchPoint;
            setColorRangesByActualPoint();
            setIsTouched(event);
        }
    }

    private void setColorRangesByActualPoint() {
        setTouchedSample();
        for (int channelIndex = 0; channelIndex < 3; channelIndex++) {
            setMinMaxForChannel(channelIndex);
        }
        mColorRanges.clear();
        mColorRanges.add(new ColorRange((int) mMinColor[0], (int) mMaxColor[0],
                (int) mMinColor[1], (int) mMaxColor[1],
                (int) mMinColor[2], (int) mMaxColor[2]));
    }

    private void setTouchedSample() {
        if (mCircleMask.height() != mLastFrame.height() || mCircleMask.width() != mLastFrame.width()) {
            mCircleMask.release();
            mCircleMask = mColoratorMatManager.allocateNewMat(CvType.CV_8UC1);
        } //todo: uderstand why this if is needed. without it for some reason the mCircleMask is 0X0 size after application is brought back from background
        mCircleMask.setTo(CommonScalars.Zeros);
        mTouchedSample.setTo(CommonScalars.Zeros3C);
        Imgproc.circle(mCircleMask, mLastTouchPoint, FINGER_RADIUS, CommonScalars.Ones, -1);
        mLastFrame.copyTo(mTouchedSample, mCircleMask);
    }

    private void setMinMaxForChannel(int channelIndex) {
        Core.extractChannel(mTouchedSample, mSingleChannel, channelIndex);
        mMinMaxResultsForChannel = Core.minMaxLoc(mSingleChannel, mSingleChannel);
        mMinColor[channelIndex] = (int) mMinMaxResultsForChannel.minVal;
        mMaxColor[channelIndex] = (int) mMinMaxResultsForChannel.maxVal;
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
            inputImage.copyTo(mLastFrame);
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
        mSingleChannel = mColoratorMatManager.allocateNewMat(CvType.CV_8UC1);
        mTouchedSample = mColoratorMatManager.allocateNewMat();
        mLastFrame = mColoratorMatManager.allocateNewMat();
    }
}
