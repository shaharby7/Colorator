package com.colorator.ColoratorImageProc;

import android.widget.ImageView;

import com.colorator.ColoratorImageProc.Emphasizer.GeneralEmphasizer;
import com.colorator.ColoratorImageProc.Detector.GeneralDetector;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ColoratorImageProc {

    private GeneralDetector detector = new GeneralDetector();
    private GeneralEmphasizer emphasizer = new GeneralEmphasizer();

    private Mat mRgba;
    private Mat mRgbaF;
    private Mat mRgbaT;
    private Mat standardizedHsv;
    private Mat finalImage;

    public ColoratorImageProc(int height, int width) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(width, width, CvType.CV_8UC4);
        standardizedHsv = new Mat(height, width, CvType.CV_8UC4);
        finalImage = new Mat(height, width, CvType.CV_8UC4);
    }

    public Mat pipeline(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        standardizeCameraImage(inputFrame);
        Mat mask = detector.detect(standardizedHsv);
        emphasizer.emphasize(standardizedHsv, mask);
        prepareFinalImage();
        return finalImage;
    }

    private void standardizeCameraImage(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        Core.transpose(mRgba, mRgbaT);
        Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0, 0, 0);
        Core.flip(mRgbaF, mRgba, 1);
        Imgproc.cvtColor(mRgba, standardizedHsv, Imgproc.COLOR_RGB2HSV);
    }

    private void prepareFinalImage() {
        Imgproc.cvtColor(standardizedHsv, finalImage, Imgproc.COLOR_HSV2BGR);
    }

    public void releaseResources() {
        mRgba.release();
        mRgbaF.release();
        mRgbaT.release();
        standardizedHsv.release();
        finalImage.release();
    }
}
