package com.colorator.ColoratorImageProc.Detector;

import android.view.MotionEvent;

import com.colorator.ColoratorImageProc.ColoratorMatManager;
import com.colorator.utils.CommonScalars;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Core.MinMaxLocResult;

import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;


public class __TouchDetector extends DetectorAbstractClass {
    private Mat mOutput, mRangeMask, mTouchMask, mLastInputFrame, mCircleMask;
    private List<Mat> mRGBChannels = new ArrayList(3);
    private JSONArray mSampledRanges = new JSONArray();
    private boolean isFirstRun = true;
    private double[] mTouchCoordinates = new double[2];
    private Point mTouchedPoint = new Point();
    private Scalar mMinColorScalar = CommonScalars.Zeros3C;
    private Scalar mMaxColorScalar = CommonScalars.Zeros3C;
    private double[] mMaxColorArray = new double[3];
    private double[] mMinColorArray = new double[3];

    public __TouchDetector(ColoratorMatManager coloratorMatManager, JSONObject detectorArgs) {
        super(coloratorMatManager, detectorArgs);
    }

    public __TouchDetector(ColoratorMatManager coloratorMatManager) {
        super(coloratorMatManager);
    }

    private enum AdditionStates {
        ADD, REPLACE_LAST_ONE, CLEAR_ALL_AND_ADD
    }

//    @Override
//    public void onTouch(MotionEvent event, int cameraViewHeight, int cameraViewWidth) {
//        mOutput.setTo(CommonScalars.Zeros);
//        setTouchedPoint(event, cameraViewHeight, cameraViewWidth);
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//            case MotionEvent.ACTION_MOVE:
//            case MotionEvent.ACTION_UP:
//                sampleRanges(event, mTouchedPoint, AdditionStates.CLEAR_ALL_AND_ADD);
//        }
//    }

    private void sampleRanges(MotionEvent event, Point touchedPoint, AdditionStates state) {
        mTouchMask.setTo(CommonScalars.Zeros);
        mCircleMask.setTo(CommonScalars.Zeros);
        Imgproc.circle(mCircleMask, touchedPoint, 50, CommonScalars.Ones, -1);
        mLastInputFrame.copyTo(mTouchMask, mCircleMask);
        Core.split(mTouchMask, mRGBChannels);
        MinMaxLocResult mMinMaxLacResultForChannel = Core.minMaxLoc(mRGBChannels.get(0));
        int minH = (int) mMinMaxLacResultForChannel.minVal;
        int maxH = (int) mMinMaxLacResultForChannel.maxVal;
        mMinMaxLacResultForChannel = Core.minMaxLoc(mRGBChannels.get(1));
        int minS = (int) mMinMaxLacResultForChannel.minVal;
        int maxS = (int) mMinMaxLacResultForChannel.maxVal;
        mMinMaxLacResultForChannel = Core.minMaxLoc(mRGBChannels.get(2));
        int minV = (int) mMinMaxLacResultForChannel.minVal;
        int maxV = (int) mMinMaxLacResultForChannel.maxVal;
        JSONObject rangeJson = createRangeJson(minH, minS, minV, maxH, maxS, maxV);
        switch (state) {
            case ADD:
                mSampledRanges.put(rangeJson);
                break;
            case REPLACE_LAST_ONE:
                mSampledRanges.remove(mSampledRanges.length() - 1);
                mSampledRanges.put(rangeJson);
                break;
            case CLEAR_ALL_AND_ADD:
                mSampledRanges = new JSONArray();
                mSampledRanges.put(rangeJson);
        }
        Imgproc.circle(mOutput, mTouchedPoint, 50, CommonScalars.Ones, -1);
    }


    @Override
    public Mat detect(Mat inputImage) {
        allocationsOfFirstRun(inputImage);
        mOutput.setTo(CommonScalars.Zeros);
        try {
            for (int i = 0; i < mSampledRanges.length(); i++) {
                JSONObject typedRange = mSampledRanges.getJSONObject(i);
                Core.bitwise_or(mOutput, getMaskOfRange(inputImage, typedRange), mOutput);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mOutput;
    }

    private void allocationsOfFirstRun(Mat inputImage) {
        mLastInputFrame = inputImage;
        if (isFirstRun) {
            mOutput = mColoratorMatManager.allocateNewMat(CvType.CV_8UC1);
            mCircleMask = mColoratorMatManager.allocateNewMat(CvType.CV_8UC1);
            mRangeMask = mColoratorMatManager.allocateNewMat(CvType.CV_8UC1);
            mTouchMask = mColoratorMatManager.allocateNewMat(CvType.CV_8UC3);
            isFirstRun = false;
        }
    }

    private Mat getMaskOfRange(Mat inputImage, JSONObject rangeDescription) {
        mRangeMask.setTo(CommonScalars.Zeros);
        try {
            mMinColorArray[0] = rangeDescription.getInt("minH");
            mMinColorArray[1] = rangeDescription.getInt("minS");
            mMinColorArray[2] = rangeDescription.getInt("minV");
            mMaxColorArray[0] = rangeDescription.getInt("maxH");
            mMaxColorArray[1] = rangeDescription.getInt("maxS");
            mMaxColorArray[2] = rangeDescription.getInt("maxV");
            mMinColorScalar.set(mMinColorArray);
            mMaxColorScalar.set(mMaxColorArray);
            Core.inRange(inputImage, mMinColorScalar, mMaxColorScalar, mRangeMask);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mRangeMask;
    }

    private void setTouchedPoint(MotionEvent event, int cameraViewHeight, int cameraViewWidth) {
        int xOffset = (cameraViewWidth - mColoratorMatManager.getWidth()) / 2;
        int yOffset = (cameraViewHeight - mColoratorMatManager.getHeight()) / 2;
        mTouchCoordinates[0] = (int) (event).getX() - xOffset;
        mTouchCoordinates[1] = (int) (event).getY() - yOffset;
        mTouchedPoint.set(mTouchCoordinates);
    }

    private static JSONObject createRangeJson(int minH, int minS, int minV, int maxH, int maxS, int maxV) {
        try {
            JSONObject rangeJson = new JSONObject();
            rangeJson.put("minH", minH);
            rangeJson.put("minS", minS);
            rangeJson.put("minV", minV);
            rangeJson.put("maxH", maxH);
            rangeJson.put("maxS", maxS);
            rangeJson.put("maxV", maxV);
            return rangeJson;
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
