package com.colorator.ColoratorImageProc.Emphasizer;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class IntegratedEmphasizers {
    int color = 0;

    public void emphasize(Mat inputImage, Mat mask) {
        inputImage.setTo(new Scalar(color, 255, 255), mask);
        color = (color + 1) % 255;
    }
}
