package com.colorator.ColoratorImageProc.Emphasizer;


import com.colorator.ColoratorImageProc.ColoratorMatManager;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class RainbowEmphasizer extends EmphasizerAbstractClass {
    private Scalar mColorScalar = new Scalar(0, 255, 255);
    private double[] mColorArray = new double[3];
    private int mHColor = 0;

    public RainbowEmphasizer(ColoratorMatManager coloratorMatManager) {
        super(coloratorMatManager);
        mColorArray[1] = 255;
        mColorArray[2] = 255;
    }

    public void emphasize(Mat inputImage, Mat mask) {
        mHColor = (mHColor + 1) % 179;
        mColorArray[0] = mHColor;
        mColorScalar.set(mColorArray);
        inputImage.setTo(mColorScalar, mask);
    }
}
