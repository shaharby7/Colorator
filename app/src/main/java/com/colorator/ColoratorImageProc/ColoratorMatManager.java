package com.colorator.ColoratorImageProc;

import android.widget.ImageView;

import com.colorator.utils.CommonScalars;

import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

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
        switch (matType) {
            case 0:
                newMat.setTo(CommonScalars.Zeros);
                break;
            case 16:
                newMat.setTo(CommonScalars.Zeros3C);
                break;
            default:
                break;
        }
        mAllMats.add(newMat);
        return newMat;
    }

    public void resizeAllMats(int height, int width, boolean forced) {
        if ((mWidth != width || mHeight != height) || forced) {
            mHeight = height;
            mWidth = width;
            for (Mat mat : mAllMats) {
                synchronized (this) {
//                    Imgproc.resize(mat, mat, new Size(mWidth, mHeight), 0,0, Imgproc.INTER_CUBIC);
                }
            }
        }
    }

    public void resizeAllMats(int height, int width) {
        resizeAllMats(height, width, false);
    }

    public void releaseAllMats() {
        for (Mat mat : mAllMats) {
            mat.release();
        }
    }

    public int getHeight() {
        return mHeight;
    }

    public int getWidth() {
        return mWidth;
    }
}
