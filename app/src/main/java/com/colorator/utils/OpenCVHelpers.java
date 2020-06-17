package com.colorator.utils;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class OpenCVHelpers {
    public static Bitmap mat2Bitmap(Mat srcMat, int conversion2Rgba) {
        Mat convertedMat = new Mat();
        Bitmap bmp = null;
        Imgproc.cvtColor(srcMat, convertedMat, conversion2Rgba, 4);
        bmp = Bitmap.createBitmap(srcMat.cols(), srcMat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(convertedMat, bmp);
        return bmp;
    }

    public static Bitmap mat2Bitmap(Mat srcMat) {
        return mat2Bitmap(srcMat, Imgproc.COLOR_HSV2RGB);
    }
}
