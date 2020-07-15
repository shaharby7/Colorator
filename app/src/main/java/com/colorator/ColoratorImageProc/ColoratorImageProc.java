package com.colorator.ColoratorImageProc;

import android.graphics.ImageFormat;
import android.util.Log;
import android.view.MotionEvent;

import com.colorator.ColoratorImageProc.Detector.DetectorAbstractClass;
import com.colorator.ColoratorImageProc.Detector.RangesDetector;
import com.colorator.ColoratorImageProc.Detector.TouchDetector;
import com.colorator.ColoratorImageProc.Detector.RelativeDetector;
import com.colorator.ColoratorImageProc.Emphasizer.RainbowEmphasizer;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ColoratorImageProc {
    public String TAG = "ColoratorImageProc";
    private ColoratorMatManager mColoratorMatManager = new ColoratorMatManager();
    private DetectorAbstractClass mDetector = new RelativeDetector(mColoratorMatManager, new Scalar(120, 255, 255));
    private RainbowEmphasizer mEmphasizer = new RainbowEmphasizer(mColoratorMatManager);
    private MaskDenoiser mMaskDenoiser = new MaskDenoiser(mColoratorMatManager);
    private Mat mFrameInProcess;
    private boolean mCommitProcess;
    private int mPreviewFormat;


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
            mMaskDenoiser.denoise(mask, mFrameInProcess);
            mEmphasizer.emphasize(mFrameInProcess, mask);
        }
        standardizeImageToPreview();
        return mFrameInProcess;
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
    }

    public int getFrameWidth() {
        return mColoratorMatManager.getWidth();
    }

    public int getFrameHeight() {
        return mColoratorMatManager.getHeight();
    }

    public void forceMatResizing(int height, int width) {
        mColoratorMatManager.resizeAllMats(height, width, true);
    }
}
