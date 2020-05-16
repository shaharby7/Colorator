package com.colorator.ColoratorImageProc.Detector;

import com.colorator.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Core;


public class RangesDetector extends DetectorAbstractClass {
    private static JSONObject mSavedRangesConfiguration = MainActivity.readConfiguration("saved_color_ranges");
    private int mHeight, mWidth;
    private Mat mOutput, mRangeMask;

    public RangesDetector(JSONObject detectorArgs) {
        super(detectorArgs);
        convertHsvScalesToOpenCV();
    }


    public RangesDetector() {
        JSONObject detectorArgs = new JSONObject();
        try {
            detectorArgs.put("Ranges", mSavedRangesConfiguration.getJSONArray("default"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mDetectorArgs = detectorArgs;
        convertHsvScalesToOpenCV();
    }

    private void allocateMats(int height, int width) {
        mHeight = height;
        mWidth = width;
        mOutput = new Mat(mHeight, mWidth, 0);
        mRangeMask = new Mat(mHeight, mWidth, 0);
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
        mRangeMask.setTo(new Scalar(0));
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
        if (mHeight!=inputImage.height()||mWidth!=inputImage.width()){
            allocateMats(inputImage.height(),inputImage.width());
        }
        mOutput.setTo(new Scalar(0));
        try {
            JSONArray allRanges = mDetectorArgs.getJSONArray("Ranges");
            for (int i = 0; i < allRanges.length(); i++) {
                JSONObject typedRange = (JSONObject) allRanges.getJSONObject(i);
                Core.bitwise_or(mOutput, getMaskOfRange(inputImage, typedRange), mOutput);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mOutput;
    }
}
