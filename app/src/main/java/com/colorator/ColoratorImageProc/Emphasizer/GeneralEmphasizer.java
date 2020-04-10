package com.colorator.ColoratorImageProc.Emphasizer;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class GeneralEmphasizer {
    public void emphasize(Mat inputImage, Mat mask) {
        inputImage.setTo(new Scalar(20, 255, 255), mask);
    }
}
