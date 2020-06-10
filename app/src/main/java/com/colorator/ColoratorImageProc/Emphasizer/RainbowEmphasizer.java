package com.colorator.ColoratorImageProc.Emphasizer;


import com.colorator.ColoratorImageProc.ColoratorMatManager;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

public class RainbowEmphasizer extends EmphasizerAbstractClass {
    Scalar mColorScalar = new Scalar(0, 255, 255);
    double[] mColorArray = new double[3];
    int mHColor = 0;
    Point mPoint = new Point(200, 200);

    public RainbowEmphasizer(ColoratorMatManager coloratorMatManager) {
        super(coloratorMatManager);
        mColorArray[1] = 255;
        mColorArray[2] = 255;
    }

    public void emphasize(Mat inputImage, Mat mask) {
        mHColor = (mHColor + 1) % 360;
        mColorArray[0] = mHColor;
        mColorScalar.set(mColorArray);
        inputImage.setTo(mColorScalar, mask);
    }
}
