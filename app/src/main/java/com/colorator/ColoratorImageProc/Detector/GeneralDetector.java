package com.colorator.ColoratorImageProc.Detector;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Core;
import org.opencv.imgproc.Imgproc;


public class GeneralDetector {
    public Mat detect(Mat inputImage) {
        Mat output = new Mat();
        Core.inRange(inputImage, new Scalar(0, 0, 200), new Scalar(255, 255, 255), output);
        return output;
    }
}
