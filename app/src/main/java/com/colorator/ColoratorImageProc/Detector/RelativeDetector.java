package com.colorator.ColoratorImageProc.Detector;

import com.colorator.ColoratorImageProc.ColoratorMatManager;
import com.colorator.utils.CommonScalars;
import com.colorator.utils.OpenCVHelpers;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

public class RelativeDetector extends DetectorAbstractClass {
    private Scalar mDetectedColor, mImageSize, mDetectedColorChannel, m179, mHistMultiply, mHueExtraWeightScalar, mMin, mMax;
    private Mat mDistances, mChannelRef, mSingleChannel, mOutput, mEmptyMask, mHist, mHueAbove90, mNewHueVals, mInRange, mGray,mRgb;
    private double[] mDetectedColorChannelValue = new double[1];
    private double[] mMinVal = new double[1];
    private double[] mMaxVal = new double[1];
    private List<Mat> mHistInput = new ArrayList<>();
    private static MatOfInt mHistChannels;
    private static int mHistBins = 1000;
    private static MatOfInt mHistSize;
    private static MatOfFloat mHistRanges;
    private OpenCVHelpers.LocalMinMaxResults mHistMinMax;
    float mDistanceThreshold = 0;
    private static int mMaxBinCounted = (int) mHistBins / 4;
    private static double mHueExtraWeight = 4;
    private static double mMaxColorDistance = Math.sqrt(Math.pow((90 * mHueExtraWeight), 2) + Math.pow(255, 2) + Math.pow(255, 2));
    private static double mHistStep = mMaxColorDistance / mHistBins;

    @Override
    void allocationsOfFirstDetection() {
        super.allocationsOfFirstDetection();
        mDistances = mColoratorMatManager.allocateNewMat(CvType.CV_32F);
        mSingleChannel = mColoratorMatManager.allocateNewMat(CvType.CV_32F);
        mHueAbove90 = mColoratorMatManager.allocateNewMat(CvType.CV_8UC1);
        mInRange = mColoratorMatManager.allocateNewMat(CvType.CV_8UC1);
        mNewHueVals = mColoratorMatManager.allocateNewMat(CvType.CV_32F);
        mOutput = mColoratorMatManager.allocateNewMat(CvType.CV_8SC1);
        mGray = mColoratorMatManager.allocateNewMat(CvType.CV_8SC3);
        mRgb = mColoratorMatManager.allocateNewMat(CvType.CV_8UC3);
        mOutput = mColoratorMatManager.allocateNewMat(CvType.CV_8SC1);
        mChannelRef = new Mat();
        mEmptyMask = new Mat();
        mHist = new Mat();
        mHistSize = new MatOfInt(mHistBins);
        mHistRanges = new MatOfFloat(0, (float) mMaxColorDistance);
        mHistChannels = new MatOfInt(0);
        m179 = new Scalar(179);
        mDetectedColorChannel = new Scalar(0);
        mMin = new Scalar(0);
        mMax = new Scalar(0);
        mImageSize = new Scalar((int) (mColoratorMatManager.getWidth() * mColoratorMatManager.getHeight()));
        mHueExtraWeightScalar = new Scalar(mHueExtraWeight);
        mHistMultiply = new Scalar(10000);
        mHistInput.clear();
        mHistInput.add(mDistances);
    }

    public RelativeDetector(ColoratorMatManager coloratorMatManager, Scalar hsvColor) {
        super(coloratorMatManager);
        mDetectedColor = hsvColor;
    }

    public RelativeDetector(ColoratorMatManager coloratorMatManager) {
        super(coloratorMatManager);
        mDetectedColor = null;
    }

    @Override
    public Mat detect(Mat inputImage) {
        super.detect(inputImage);
        if (mDetectedColor != null) {
            calcDistanceMat();
            calcDistanceHist();
            watershedByDistances();
            mOutput.convertTo(mOutput, CvType.CV_8UC1);
        } else {
            mOutput.setTo(CommonScalars.Zeros);
        }
        return mOutput;
    }

    private void watershedByDistances() {
        mHistMinMax = OpenCVHelpers.localMinMax(mHist);
        mOutput.convertTo(mOutput, CvType.CV_32SC1);
        mOutput.setTo(CommonScalars.Zeros);
        applyInRange(mHistMinMax.maxLocs.get(0));
        mOutput.setTo(CommonScalars.Twos, mInRange);
        for (int i = 1; i < mHistMinMax.maxLocs.size(); i++) {
            applyInRange(mHistMinMax.maxLocs.get(i));
            mOutput.setTo(CommonScalars.Ones, mInRange);
        }
        Imgproc.cvtColor(mBlurredImage, mRgb, Imgproc.COLOR_HSV2RGB);
        Imgproc.cvtColor(mRgb, mGray,Imgproc.COLOR_RGB2GRAY);
        Imgproc.watershed(mGray, mOutput);
        mOutput.convertTo(mOutput, CvType.CV_8UC1);
        Imgproc.threshold(mOutput, mOutput, 1, 1, Imgproc.THRESH_BINARY);
    }

    private void applyInRange(int bin) {
        mInRange.setTo(CommonScalars.Zeros);
        mMinVal[0] = bin * mHistStep;
        mMaxVal[0] = (bin + 1) * mHistStep;
        mMin.set(mMinVal);
        mMax.set(mMaxVal);
        Core.inRange(mDistances, mMin, mMax, mInRange);
    }

    private void calcDistanceMat() {
        mDistances.setTo(CommonScalars.Zeros);
        for (int channelIdx = 0; channelIdx < 3; channelIdx++) {
            Core.extractChannel(mBlurredImage, mChannelRef, channelIdx);
            mChannelRef.convertTo(mSingleChannel, CvType.CV_32F);
            mDetectedColorChannelValue[0] = mDetectedColor.val[channelIdx];
            mDetectedColorChannel.set(mDetectedColorChannelValue);
            Core.absdiff(mSingleChannel, mDetectedColorChannel, mSingleChannel);
            if (channelIdx == 0) {
                normalizeHueChannel();
            }
            Core.pow(mSingleChannel, 2, mSingleChannel);
            Core.add(mDistances, mSingleChannel, mDistances, mEmptyMask, CvType.CV_32F);
        }
        Core.sqrt(mDistances, mDistances);
    }

    private void normalizeHueChannel() {
        mHueAbove90.setTo(CommonScalars.Zeros);
        Imgproc.threshold(mSingleChannel, mHueAbove90, 90, 1, Imgproc.THRESH_BINARY);
        mHueAbove90.convertTo(mHueAbove90, CvType.CV_8UC1);
        Core.absdiff(mSingleChannel, m179, mNewHueVals);
        mNewHueVals.copyTo(mSingleChannel, mHueAbove90);
        Core.multiply(mSingleChannel, mHueExtraWeightScalar, mSingleChannel);
    }

    private void calcDistanceHist() {
        Imgproc.calcHist(mHistInput, mHistChannels, mEmptyMask, mHist, mHistSize, mHistRanges);
        Core.divide(mHist, mImageSize, mHist);
        Core.multiply(mHist, mHistMultiply, mHist);
        mHist.convertTo(mHist, CvType.CV_32F);
        Imgproc.medianBlur(mHist, mHist, 5);
    }

    private void calcDistanceThreshold() {
        mHistMinMax = OpenCVHelpers.localMinMax(mHist);
        if (mHistMinMax.minLocs.size() == 0 || mHistMinMax.maxLocs.size() == 0) {
            mDistanceThreshold = 0;
            return;
        }
        Integer firstMinLoc = mHistMinMax.minLocs.get(0);
        Integer firstMaxLoc = mHistMinMax.maxLocs.get(0);
        if (firstMaxLoc > mMaxBinCounted) {
            mDistanceThreshold = 0;
        } else if (firstMinLoc > firstMaxLoc) {
            mDistanceThreshold = (int) ((firstMinLoc + 1) * mHistStep);
        } else {
            mDistanceThreshold = (int) ((mHistMinMax.minLocs.get(1) + 1) * mHistStep);
        }
    }

    @Override
    boolean generateBlurredImage() {
        return true;
    }

    void setDetectedColor(Scalar newColor) {
        mDetectedColor = newColor;
    }
}
