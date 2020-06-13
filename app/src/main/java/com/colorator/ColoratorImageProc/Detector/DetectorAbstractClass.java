package com.colorator.ColoratorImageProc.Detector;

import android.view.MotionEvent;

import com.colorator.ColoratorImageProc.ColoratorMatManager;

import org.json.JSONObject;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.core.Point;

public abstract class DetectorAbstractClass {
    JSONObject mDetectorArgs;
    ColoratorMatManager mColoratorMatManager;

    public DetectorAbstractClass(ColoratorMatManager coloratorMatManager, JSONObject detectorArgs) {
        mDetectorArgs = detectorArgs;
        mColoratorMatManager = coloratorMatManager;
    }

    public DetectorAbstractClass(ColoratorMatManager coloratorMatManager) {
        mColoratorMatManager = coloratorMatManager;
    }

    abstract public Mat detect(Mat inputImage);

    public void onTouch(MotionEvent event, Point touchedPoint) {
    }
}