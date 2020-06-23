package com.colorator.ColoratorImageProc.Detector;

import android.view.MotionEvent;

import com.colorator.ColoratorImageProc.ColoratorMatManager;

import org.json.JSONObject;
import org.opencv.core.Mat;
import org.opencv.core.Point;

public abstract class DetectorAbstractClass {
    private JSONObject mDetectorArgs;
    ColoratorMatManager mColoratorMatManager;
    private boolean hasNotRanYet = true;

    public DetectorAbstractClass(ColoratorMatManager coloratorMatManager, JSONObject detectorArgs) {
        mDetectorArgs = detectorArgs;
        mColoratorMatManager = coloratorMatManager;
    }

    public DetectorAbstractClass(ColoratorMatManager coloratorMatManager) {
        mColoratorMatManager = coloratorMatManager;
    }

    public Mat detect(Mat inputImage) {
        allocationsOfFirstDetectionWrapper();
        return null;
    }

    public void onTouch(MotionEvent event, Point touchedPoint) {
    }

    protected void allocationsOfFirstDetectionWrapper() {
        if (hasNotRanYet) {
            allocationsOfFirstDetection();
        }
        hasNotRanYet = false;
    }

    abstract void allocationsOfFirstDetection();
}