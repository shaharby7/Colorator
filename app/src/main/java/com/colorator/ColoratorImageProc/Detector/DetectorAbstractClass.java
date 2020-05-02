package com.colorator.ColoratorImageProc.Detector;

import org.opencv.core.Mat;

import java.util.Map;

public abstract class DetectorAbstractClass {
    Map<String, Object> mDetectorArgs;

    public DetectorAbstractClass(Map detectorArgs) {
        mDetectorArgs = detectorArgs;
    }

    abstract public Mat detect(Mat inputImage);
}
