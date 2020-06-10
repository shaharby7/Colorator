package com.colorator.ColoratorImageProc.Detector;

import com.colorator.ColoratorImageProc.ColoratorMatManager;

import org.json.JSONObject;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.CvType;
import org.opencv.imgproc.Imgproc;

public class TestDetector extends DetectorAbstractClass {
    private Mat mOutput;
    private boolean isFirstRun = true;
    private Point mPoint = new Point(200, 200);
    private Scalar mOneScalar = new Scalar(1);

    public TestDetector(ColoratorMatManager coloratorMatManager, JSONObject detectorArgs) {
        super(coloratorMatManager, detectorArgs);
    }

    public TestDetector(ColoratorMatManager coloratorMatManager) {
        super(coloratorMatManager);
    }

    @Override
    public Mat detect(Mat inputImage) {
        if (isFirstRun) {
            mOutput = super.mColoratorMatManager.allocateNewMat(CvType.CV_8UC1);
            isFirstRun = false;
        }
        mOutput.setTo(new Scalar(0));
        Imgproc.circle(mOutput, mPoint, 50, mOneScalar, -1);
        return mOutput;
    }
}
