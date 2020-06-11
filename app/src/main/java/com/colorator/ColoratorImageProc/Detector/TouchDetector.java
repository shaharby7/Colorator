package com.colorator.ColoratorImageProc.Detector;

import android.view.MotionEvent;

import com.colorator.ColoratorImageProc.ColoratorMatManager;
import com.colorator.utils.CommonScalars;

import org.json.JSONObject;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

public class TouchDetector extends DetectorAbstractClass {
    private Mat mOutput, mCircleMask;
    private boolean isFirstRun = true;

    public TouchDetector(ColoratorMatManager coloratorMatManager, JSONObject detectorArgs) {
        super(coloratorMatManager, detectorArgs);
    }

    public TouchDetector(ColoratorMatManager coloratorMatManager) {
        super(coloratorMatManager);
    }

    @Override
    public void onTouch(MotionEvent event, Point actualTouchPoint) {
        synchronized (this) {
            Imgproc.circle(mOutput,actualTouchPoint,50, CommonScalars.Ones, -1);
        }
    }

    @Override
    public Mat detect(Mat inputImage) {
        allocateAtFirstRun();
        return mOutput;
    }

    private void allocateAtFirstRun() {
        if (isFirstRun) {
            mOutput = mColoratorMatManager.allocateNewMat(CvType.CV_8UC1);
            mCircleMask = mColoratorMatManager.allocateNewMat(CvType.CV_8UC1);
            isFirstRun = false;
        }
    }
}
