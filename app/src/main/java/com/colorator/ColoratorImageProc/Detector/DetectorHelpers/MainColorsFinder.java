package com.colorator.ColoratorImageProc.Detector.DetectorHelpers;

import com.colorator.utils.ColorRange;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class MainColorsFinder {
    private static List<Mat> listOfMat = new ArrayList<>();
    private static MatOfInt channels = new MatOfInt(0, 1);
    private static MatOfInt histSize = new MatOfInt(18, 18);
    private static MatOfFloat histRange = new MatOfFloat(0f, 180f, 0f, 256f);

    public static class MainColorFinderResults {
        private float coverPercent;
        private int originalMatPixels, meanBrightness;
        private ArrayList<ColorRange> colorRanges = new ArrayList<>();

        MainColorFinderResults(int totalAmountOfPixels, int meanBrightness) {
            coverPercent = 0f;
            originalMatPixels = totalAmountOfPixels;
            this.meanBrightness = meanBrightness;
        }

        MainColorFinderResults() {
            coverPercent = -1;
            originalMatPixels = -1;
            this.meanBrightness = -1;
        }

        void addColorRange(ColorRange colorRange, int pixelsAmount) {
            colorRanges.add(colorRange);
            coverPercent += (float) pixelsAmount / originalMatPixels * 100;
        }

        public ArrayList<ColorRange> getColorRanges() {
            return colorRanges;
        }

        float getCoverPercent() {
            return coverPercent;
        }

    }

    private static void calcHist(Mat inputImage, Mat hist, Mat mask) {
        listOfMat.add(inputImage);
        Imgproc.calcHist(listOfMat, channels, mask, hist, histSize, histRange);
        listOfMat.clear();
    }

    private static MainColorFinderResults findRelevantColorRanges(Mat hist,
                                                                  MainColorFinderResults previousResults,
                                                                  int minimalRequiredCoverPercent) {
        Core.MinMaxLocResult minMaxLoc = Core.minMaxLoc(hist);
        ColorRange maxBinColorRange = getColorRangeOfBin(minMaxLoc.maxLoc, previousResults.meanBrightness);
        previousResults.addColorRange(maxBinColorRange, (int) minMaxLoc.maxVal);
        if (previousResults.getCoverPercent() > minimalRequiredCoverPercent) {
            return previousResults;
        }
        hist.put((int) minMaxLoc.maxLoc.y, (int) minMaxLoc.maxLoc.x, 0);
        return findRelevantColorRanges(hist, previousResults, minimalRequiredCoverPercent);
    }

    private static ColorRange getColorRangeOfBin(Point binPoint, int meanBrightness) {
        int[] minMaxHue = getBoundariesForOneDimension(0, (int) binPoint.y);
        int[] minMaxSat = getBoundariesForOneDimension(1, (int) binPoint.x);
        return new ColorRange(minMaxHue[0], minMaxHue[1], minMaxSat[0], minMaxSat[1], meanBrightness - 30, meanBrightness + 30);
    }

    private static int[] getBoundariesForOneDimension(int dimension, int binLoc) {
        int[] minMax = {0, 0};
        double dimMinRange = histRange.get(dimension * 2, 0)[0];
        double dimMaxRange = histRange.get((dimension * 2) + 1, 0)[0];
        double dimBinsAmount = histSize.get(dimension, 0)[0];
        double dimSteps = (dimMaxRange - dimMinRange) / dimBinsAmount;
        minMax[0] = (int) (dimMinRange + (binLoc * dimSteps));
        minMax[1] = (int) (dimMinRange + ((binLoc + 1) * dimSteps));
        return minMax;
    }

    private static int calcMeanBrightness(Mat inputImage, Mat mask) {
        Mat valChannel = new Mat(mask.height(), mask.width(), CvType.CV_8UC1);
        Core.extractChannel(inputImage, valChannel, 2);
        int meanBrightness = (int) Core.mean(valChannel, mask).val[0];
        valChannel.release();
        return meanBrightness;
    }

    public static MainColorFinderResults find(Mat inputImage, Mat mask) {
        Mat hist = new Mat();
        calcHist(inputImage, hist, mask);
        int totalPixelAmount = (int) Core.sumElems(hist).val[0];
        int meanBrightness = calcMeanBrightness(inputImage, mask);
        if (totalPixelAmount == 0) {
            return new MainColorFinderResults();
        }
        MainColorFinderResults results = new MainColorFinderResults(totalPixelAmount, meanBrightness);
        results = findRelevantColorRanges(hist, results, 80);
        hist.release();
        return results;
    }
}