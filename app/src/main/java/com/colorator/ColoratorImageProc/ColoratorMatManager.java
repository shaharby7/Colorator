package com.colorator.ColoratorImageProc;

import org.opencv.core.Mat;
import org.opencv.core.CvType;

import java.util.ArrayList;

public class ColoratorMatManager {
    private int mHeight, mWidth = 0;
    private ArrayList<Mat> mAllMats = new ArrayList<Mat>();

    ColoratorMatManager() {
        mWidth = 0;
        mHeight = 0;
    }

    public ColoratorMatManager(int height, int width) {
        mHeight = height;
        mWidth = width;
    }

    public Mat allocateNewMat() {
        return allocateNewMat(CvType.CV_8UC3);
    }

    public Mat allocateNewMat(int matType) {
        Mat newMat = new Mat(mHeight, mWidth, matType);
        mAllMats.add(newMat);
        return newMat;
    }

    public void resizeAllMats(int height, int width) {
        if (mWidth != width || mHeight != height) {
            mHeight = height;
            mWidth = width;
            for (Mat mat : mAllMats) {
                mat = new Mat(height, width, mat.type());
            }
        }
    }

    public void releaseAllMats() {
        for (Mat mat : mAllMats) {
            mat.release();
        }
    }

    public int getHeight(){
        return mHeight;
    }

    public int getWidth() {
        return mWidth;
    }
}
