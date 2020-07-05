package com.colorator.ColoratorImageProc.Detector;

import com.colorator.ColoratorImageProc.ColoratorMatManager;
import com.colorator.MainActivity;
import com.colorator.utils.ColorRange;
import com.colorator.utils.CommonScalars;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Core;

import java.util.ArrayList;
import java.util.List;


public class RangesDetector extends DetectorAbstractClass {
    private static JSONObject mSavedRangesConfiguration = MainActivity.readConfiguration("saved_color_ranges");
    Mat mOutput, mRangeMask;
    List<ColorRange> mColorRanges = new ArrayList<>();

    public RangesDetector(ColoratorMatManager coloratorMatManager, ColorRange colorRange) {
        super(coloratorMatManager);
        mColorRanges.add(colorRange);
    }

    public RangesDetector(ColoratorMatManager coloratorMatManager, ArrayList<ColorRange> colorRanges) {
        super(coloratorMatManager);
        mColorRanges = colorRanges;
    }


    public RangesDetector(ColoratorMatManager coloratorMatManager) {
        super(coloratorMatManager);
        try {

            ColorRange defaultColorRange = new ColorRange(
                    (JSONObject) mSavedRangesConfiguration.getJSONArray("saved_colors").get(0)
            );
            mColorRanges.add(defaultColorRange);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Mat getMaskOfRange(Mat inputImage, ColorRange colorRange) {
        mRangeMask.setTo(CommonScalars.Zeros);
        colorRange.filterBy(inputImage, mRangeMask);
        return mRangeMask;
    }

    @Override
    public Mat detect(Mat inputImage) {
        super.detect(inputImage);
        mOutput.setTo(CommonScalars.Zeros);
        for (ColorRange colorRange : mColorRanges) {
            Core.bitwise_or(mOutput, getMaskOfRange(mBlurredImage, colorRange), mOutput);
        }
        return mOutput;
    }

    @Override
    void allocationsOfFirstDetection() {
        super.allocationsOfFirstDetection();
        mOutput = mColoratorMatManager.allocateNewMat(CvType.CV_8UC1);
        mRangeMask = mColoratorMatManager.allocateNewMat(CvType.CV_8UC1);
    }

    @Override
    boolean generateBlurredImage(){
        return true;
    }
}