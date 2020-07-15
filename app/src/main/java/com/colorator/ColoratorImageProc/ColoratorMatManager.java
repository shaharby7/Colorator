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

    Mat allocateNewMat() {
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
            Size newSize = new Size(mWidth, mHeight);
            for (Mat mat : mAllMats) {
                synchronized (this) {
                    if (mat.size().empty()) {
                        mat = new Mat(newSize, mat.type());
                    } else {
                        Imgproc.resize(mat, mat, newSize);
                    }
                }
            }
        }
    }

    void resizeAllMats(int height, int width) {
        resizeAllMats(height, width, false);
    }

    void releaseAllMats() {
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
