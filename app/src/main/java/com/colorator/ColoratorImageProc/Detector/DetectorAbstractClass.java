package com.colorator.ColoratorImageProc.Detector;

import org.json.JSONObject;
import org.opencv.core.Mat;

import java.util.Map;

public abstract class DetectorAbstractClass {
    JSONObject mDetectorArgs;

    public DetectorAbstractClass(JSONObject detectorArgs) {
        mDetectorArgs = detectorArgs;
    }

    public DetectorAbstractClass(){}

    abstract public Mat detect(Mat inputImage);
}
