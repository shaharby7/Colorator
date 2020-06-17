package com.colorator.utils;

import android.graphics.Bitmap;


import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class LogoCreator {
    private static Scalar red = new Scalar(255, 0, 0);
    private static Scalar green = new Scalar(0, 255, 0);
    private static Scalar blue = new Scalar(0, 0, 255);

    public static Bitmap create(int size) {
        Mat mat = createShape(size);
        Mat effectMasc = createEffectMask(size);
        applyColorBlindnessEffect(mat, effectMasc, 0.5);
        Bitmap bpm = OpenCVHelpers.mat2Bitmap(mat, Imgproc.COLOR_RGB2RGBA);
        return bpm;
    }

    private static void applyColorBlindnessEffect(Mat mat, Mat effectMasc, double effectPercent) {
        Mat afterEffect = new Mat();
        mat.copyTo(afterEffect);
        List<Mat> channels = new ArrayList(3);
        Core.split(afterEffect, channels);
        Core.multiply(channels.get(0), new Scalar(1. - effectPercent), channels.get(0));
        Mat additionalRed = new Mat();
        Core.multiply(channels.get(1), new Scalar(effectPercent), additionalRed);
        Core.add(channels.get(0), additionalRed, channels.get(0));
        Core.merge(channels, afterEffect);
        afterEffect.copyTo(mat, effectMasc);
    }

    private static Mat createEffectMask(int size) {
        int halfSize = (int) (size / 2);
        Mat mask = new Mat(size, size, CvType.CV_8UC1, CommonScalars.Ones);
        Imgproc.circle(mask, new Point(halfSize, halfSize), size / 8 * 3, CommonScalars.Zeros, -1);
        return mask;
    }

    private static Mat createShape(int size) {
        int halfSize = (int) size / 2;
        Mat mat = new Mat(size, size, CvType.CV_8UC3);
        Imgproc.rectangle(mat, new Point(0, 0), new Point(halfSize, halfSize), green, -1);
        Imgproc.rectangle(mat, new Point(halfSize, 0), new Point(size, halfSize), red, -1);
        Imgproc.rectangle(mat, new Point(0, halfSize), new Point(halfSize, size), blue, -1);
        Imgproc.rectangle(mat, new Point(halfSize, halfSize), new Point(size, size), green, -1);
        return mat;
    }
}
