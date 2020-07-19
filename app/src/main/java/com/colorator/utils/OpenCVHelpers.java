package com.colorator.utils;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static Mat simpleCalcHist(Mat input) {
        Mat hist = new Mat();
        List<Mat> listOfMat = new ArrayList<>();
        listOfMat.add(input);
        MatOfInt firstChannel = new MatOfInt(0);
        Core.MinMaxLocResult minMax = Core.minMaxLoc(input);
        MatOfInt histSize = new MatOfInt((int) (minMax.maxVal - minMax.minVal) + 1);
        float[] range = {(int) minMax.minVal, (int) minMax.maxVal + 1};
        MatOfFloat histRange = new MatOfFloat(range);
        Imgproc.calcHist(listOfMat, firstChannel, new Mat(), hist, histSize, histRange);
        return hist;
    }

    public static class LocalMinMaxResults {
        public List<Integer> maxLocs = new ArrayList<>();
        public List<Integer> minLocs = new ArrayList<>();
        public List<Double> maxVals = new ArrayList<>();
        public List<Double> minVals = new ArrayList<>();

        LocalMinMaxResults() {
        }

        void addLocalMin(int loc, double val) {
            minLocs.add(loc);
            minVals.add(val);
        }

        void addLocalMax(int loc, double val) {
            maxLocs.add(loc);
            maxVals.add(val);
        }
    }

    public static LocalMinMaxResults localMinMax(Mat oneDimMat) {
        double prev = oneDimMat.get(0, 0)[0];
        double current = oneDimMat.get(1, 0)[0];
        double next = oneDimMat.get(2, 0)[0];
        LocalMinMaxResults results = new LocalMinMaxResults();
        for (int i = 1; i < oneDimMat.rows() - 1; i++) {
            if (current >= prev && current > next) {
                results.addLocalMax(i, current);
            }
            if (current <= prev && current < next) {
                results.addLocalMin(i, current);
            }
            prev = current;
            current = next;
            next = oneDimMat.get(i + 1, 0)[0];
        }
        return results;
    }

    public static void saveImage(Bitmap image, String fileName, ContentResolver contentResolver) throws IOException {
        String path = Environment.getExternalStorageDirectory().toString();
        File file = new File(path, fileName + ".png");
        OutputStream fOut = new FileOutputStream(file);
        image.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
        fOut.close();
        MediaStore.Images.Media.insertImage(contentResolver, file.getAbsolutePath(), file.getName(), file.getName());
    }
}
