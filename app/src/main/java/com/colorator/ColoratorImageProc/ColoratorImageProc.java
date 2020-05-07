package com.colorator.ColoratorImageProc;

import android.util.Log;

import com.colorator.ColoratorImageProc.Detector.DetectorAbstractClass;
import com.colorator.ColoratorImageProc.Detector.RangesDetector;
import com.colorator.ColoratorImageProc.Emphasizer.IntegratedEmphasizers;

import org.json.JSONObject;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class ColoratorImageProc {
    public String TAG = "ColoratorImageProc";
    private DetectorAbstractClass mDetector = new RangesDetector();
    private IntegratedEmphasizers mEmphasizer = new IntegratedEmphasizers();
    private Mat mFrameInProcess;
    private boolean mCommitProcess;

    public void allocateImageSize(int height, int width) {
        mFrameInProcess = new Mat(height, width, CvType.CV_8UC4);
    }

    public void setDetector(String detectorClassName, JSONObject detectorArgs) {
        try {
            Class<?> detectorClass = Class.forName(detectorClassName);
            Constructor<?> detectorConstructor = detectorClass.getConstructor(JSONObject.class);
            mDetector = (DetectorAbstractClass) detectorConstructor.newInstance(detectorArgs);

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException ex) {
            Log.e(TAG, "Unknown Detector class " + detectorClassName);
            ex.printStackTrace();
        }
    }

    public Mat pipeline(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        standardizeCameraImage(inputFrame);
        if (mCommitProcess) {
            Mat mask = mDetector.detect(mFrameInProcess);
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
        Mat RgbaT = new Mat(mFrameInProcess.width(), mFrameInProcess.width(), CvType.CV_8UC4);
        Core.transpose(mFrameInProcess, RgbaT);
        Imgproc.resize(RgbaT, mFrameInProcess, mFrameInProcess.size(), 0, 0, 0);
        Core.flip(mFrameInProcess, mFrameInProcess, 1);
        Imgproc.cvtColor(mFrameInProcess, mFrameInProcess, Imgproc.COLOR_RGB2HSV);


    }

    private void standardizeImageToPreview() {
        Imgproc.cvtColor(mFrameInProcess, mFrameInProcess, Imgproc.COLOR_HSV2BGR);
    }

    public void releaseResources() {
        mFrameInProcess.release();
    }
}
