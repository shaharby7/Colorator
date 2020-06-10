package com.colorator.ColoratorImageProc.Detector;

import android.graphics.Color;

import com.colorator.ColoratorImageProc.ColoratorMatManager;
import com.colorator.MainActivity;
import com.colorator.utils.CommonScalars;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Core;


public class RangesDetector extends DetectorAbstractClass {
    private static JSONObject mSavedRangesConfiguration = MainActivity.readConfiguration("saved_color_ranges");
    private Mat mOutput, mRangeMask;
    private boolean isFirstRun = true;

    public RangesDetector(ColoratorMatManager coloratorMatManager, JSONObject detectorArgs) {
        super(coloratorMatManager, detectorArgs);
        convertHsvScalesToOpenCV();
    }


    public RangesDetector(ColoratorMatManager coloratorMatManager) {
        super(coloratorMatManager);
        JSONObject detectorArgs = new JSONObject();
        try {
            detectorArgs.put("Ranges", mSavedRangesConfiguration.getJSONArray("default"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mDetectorArgs = detectorArgs;
        convertHsvScalesToOpenCV();
    }

    private void convertHsvScalesToOpenCV() {
        try {
            JSONArray convertedAllRanges = new JSONArray();
            JSONArray allRanges = mDetectorArgs.getJSONArray("Ranges");
            for (int i = 0; i < allRanges.length(); i++) {
                JSONObject convertedRAnge = new JSONObject();
                JSONObject oldRange = allRanges.getJSONObject(i);
                convertedRAnge.put("minH", (int) (oldRange.getDouble("minH") / 360 * 179));
                convertedRAnge.put("minS", (int) (oldRange.getDouble("minS") / 100 * 255));
                convertedRAnge.put("minV", (int) (oldRange.getDouble("minV") / 100 * 255));
                convertedRAnge.put("maxH", (int) (oldRange.getDouble("maxH") / 360 * 179));
                convertedRAnge.put("maxS", (int) (oldRange.getDouble("maxS") / 100 * 255));
                convertedRAnge.put("maxV", (int) (oldRange.getDouble("maxV") / 100 * 255));
                convertedAllRanges.put(convertedRAnge);
            }
            mDetectorArgs.put("Ranges", convertedAllRanges);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }


    private Mat getMaskOfRange(Mat inputImage, JSONObject rangeDescription) {
        mRangeMask.setTo(CommonScalars.Zeros);
        try {
            Core.inRange(inputImage,
                    new Scalar(rangeDescription.getInt("minH"),
                            rangeDescription.getInt("minS"),
                            rangeDescription.getInt("minV")),
                    new Scalar(rangeDescription.getInt("maxH"),
                            rangeDescription.getInt("maxS"),
                            rangeDescription.getInt("maxV")),
                    mRangeMask);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mRangeMask;
    }

    @Override
    public Mat detect(Mat inputImage) {
        if (isFirstRun){
            mOutput = mColoratorMatManager.allocateNewMat(CvType.CV_8UC1);
            mRangeMask = mColoratorMatManager.allocateNewMat(CvType.CV_8UC1);
            isFirstRun = false;
        }
        mOutput.setTo(CommonScalars.Zeros);
        try {
            JSONArray allRanges = mDetectorArgs.getJSONArray("Ranges");
            for (int i = 0; i < allRanges.length(); i++) {
                JSONObject typedRange = allRanges.getJSONObject(i);
                Core.bitwise_or(mOutput, getMaskOfRange(inputImage, typedRange), mOutput);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mOutput;
    }
}
