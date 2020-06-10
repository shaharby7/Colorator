package com.colorator.ColoratorImageProc.Emphasizer;

import com.colorator.ColoratorImageProc.ColoratorMatManager;

import org.opencv.core.Mat;

public abstract class EmphasizerAbstractClass {
    ColoratorMatManager mColoratorMatManager;

    EmphasizerAbstractClass(ColoratorMatManager coloratorMatManager) {
        mColoratorMatManager = coloratorMatManager;
    }

    abstract void emphasize(Mat inputImage, Mat mask);
}
