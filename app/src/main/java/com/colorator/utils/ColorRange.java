package com.colorator.utils;


import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class ColorRange {
    private final static String TAG = "ColorRange";
    private String mColorName, mHex;
    private Scalar mMinColor = new Scalar(0, 0, 0);
    private Scalar mMaxColor = new Scalar(0, 0, 0);
    private Scalar mCenterColor = new Scalar(0, 0, 0);

    public ColorRange(String name, int minH, int maxH, int minS, int maxS, int minV, int maxV) {
        mColorName = name;
        mMinColor.set(new double[]{minH, minS, minV});
        mMaxColor.set(new double[]{maxH, maxS, maxV});
        mCenterColor.set(new double[]{
                (maxH + minH) / 2.,
                (maxS + minS) / 2.,
                (maxV + minV) / 2.
        });
    }

    public ColorRange(int minH, int maxH, int minS, int maxS, int minV, int maxV) {
        this("NoName", minH, maxH, minS, maxS, minV, maxV);
    }

    public ColorRange(String name, int minH, int maxH, int minS, int maxS, int minV, int maxV, String hex) {
        this(name, minH, maxH, minS, maxS, minV, maxV);
        mHex = hex;
    }

    public ColorRange(JSONObject colorRangeJson) throws JSONException {
        this(colorRangeJson.getString("Name"),
                colorRangeJson.getInt("minH"),
                colorRangeJson.getInt("maxH"),
                colorRangeJson.getInt("minS"),
                colorRangeJson.getInt("maxS"),
                colorRangeJson.getInt("minV"),
                colorRangeJson.getInt("maxV"));
        try {
            mHex = colorRangeJson.getString("Hex");
        } catch (JSONException ignored) {
        }
    }

    public void filterBy(Mat src, Mat dst) {
        Core.inRange(src, mMinColor, mMaxColor, dst);
    }

    public String getColorName() {
        return mColorName;
    }

    public String getHex() {
        return mHex;
    }

    public Scalar getCenterColor() {
        return mCenterColor;
    }
}