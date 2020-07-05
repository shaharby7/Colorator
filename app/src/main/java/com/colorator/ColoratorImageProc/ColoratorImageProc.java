package com.colorator.ColoratorImageProc;

import android.graphics.ImageFormat;
import android.util.Log;
import android.view.MotionEvent;

import com.colorator.ColoratorImageProc.Detector.DetectorAbstractClass;
import com.colorator.ColoratorImageProc.Detector.RangesDetector;
import com.colorator.ColoratorImageProc.Detector.TouchDetector;
import com.colorator.ColoratorImageProc.Emphasizer.RainbowEmphasizer;
import com.colorator.utils.CommonScalars;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class ColoratorImageProc {
    public String TAG = "ColoratorImageProc";
    private ColoratorMatManager mColoratorMatManager = new ColoratorMatManager();
    private DetectorAbstractClass mDetector = new TouchDetector(mColoratorMatManager);
    private RainbowEmphasizer mEmphasizer = new RainbowEmphasizer(mColoratorMatManager);
    private Mat mFrameInProcess, mHierarchy;
    private boolean mCommitProcess;
    private int mPreviewFormat;
    List<MatOfPoint> mContours;


    public void setDetector(String detectorClassName, Object detectorArgs) {
        try {
            Class<?> detectorClass = Class.forName(detectorClassName);
            Constructor<?> detectorConstructor = detectorClass.getConstructor(ColoratorMatManager.class, detectorArgs.getClass());
            mDetector = (DetectorAbstractClass) detectorConstructor.newInstance(mColoratorMatManager, detectorArgs);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException ex) {
            Log.e(TAG, "Unknown Detector class " + detectorClassName);
            ex.printStackTrace();
        }
    }

    public Mat pipeline(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mPreviewFormat = inputFrame.getPreviewFormat();
        standardizeCameraImage(inputFrame);
        mColoratorMatManager.resizeAllMats(mFrameInProcess.height(), mFrameInProcess.width());
        if (mCommitProcess) {
            Mat mask = mDetector.detect(mFrameInProcess);
//            denoise(mask, mask);
            mEmphasizer.emphasize(mFrameInProcess, mask);
        }
        standardizeImageToPreview();
        return mFrameInProcess;
    }

    private void denoise(Mat src, Mat dst) {
//        Imgproc.Canny(src, dst, 250, 256);
//        Imgproc.findContours(src, mContours, mHierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
//        mHierarchy.release();
//        Imgproc.drawContours(dst, mContours, -1, CommonScalars.Ones, -1);
//        MatOfInt hull = new MatOfInt();
//        for (int i = 0; i < mContours.size(); i++) {
//            Imgproc.convexHull(mContours.get(i),hull);
//        }
    }

    public void setCommitProcess(boolean commitProcess) {
        mCommitProcess = commitProcess;
    }

    private void standardizeCameraImage(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mFrameInProcess = inputFrame.rgba();
        Imgproc.cvtColor(mFrameInProcess, mFrameInProcess, Imgproc.COLOR_RGB2HSV, 3);
    }

    private void standardizeImageToPreview() {
        if (mPreviewFormat == ImageFormat.NV21)
            Imgproc.cvtColor(mFrameInProcess, mFrameInProcess, Imgproc.COLOR_HSV2RGB, 3);
        else if (mPreviewFormat == ImageFormat.YV12)
            Imgproc.cvtColor(mFrameInProcess, mFrameInProcess, Imgproc.COLOR_HSV2BGR, 3);
    }

    public void releaseResources() {
        mColoratorMatManager.releaseAllMats();
        mFrameInProcess.release();
    }

    public void onTouch(MotionEvent event, Point touchedPoint) {
        mDetector.onTouch(event, touchedPoint);
    }

    public void allocateFrameImProcess() {
        mFrameInProcess = mColoratorMatManager.allocateNewMat();
        mHierarchy = new Mat();
        mContours = new ArrayList<MatOfPoint>();
    }

    public int getFrameWidth() {
        return mColoratorMatManager.getWidth();
    }

    public int getFrameHeight() {
        return mColoratorMatManager.getHeight();
    }

    public void forceMatResizing(int height, int width){
        mColoratorMatManager.resizeAllMats(height, width, true);
    }
}
