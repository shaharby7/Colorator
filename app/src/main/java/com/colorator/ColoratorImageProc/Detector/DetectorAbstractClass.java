package com.colorator.ColoratorImageProc.Detector;

import android.view.MotionEvent;
import android.widget.ImageView;

import com.colorator.ColoratorImageProc.ColoratorMatManager;

import org.json.JSONObject;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public abstract class DetectorAbstractClass {
    private JSONObject mDetectorArgs;
    ColoratorMatManager mColoratorMatManager;
    private boolean hasNotRanYet = true;
    Mat mBlurredImage;
    Size mBlurSize;

    public DetectorAbstractClass(ColoratorMatManager coloratorMatManager, JSONObject detectorArgs) {
        mDetectorArgs = detectorArgs;
        mColoratorMatManager = coloratorMatManager;
    }

    public DetectorAbstractClass(ColoratorMatManager coloratorMatManager) {
        mColoratorMatManager = coloratorMatManager;
    }

    public Mat detect(Mat inputImage) {
        allocationsOfFirstDetectionWrapper();
        if (generateBlurredImage()) {
            Imgproc.GaussianBlur(inputImage, mBlurredImage, mBlurSize, 0);
        }
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

    void allocationsOfFirstDetection() {
        mBlurredImage = mColoratorMatManager.allocateNewMat(CvType.CV_8UC3);
        mBlurSize = new Size(5, 5);
    }

    abstract boolean generateBlurredImage();
}