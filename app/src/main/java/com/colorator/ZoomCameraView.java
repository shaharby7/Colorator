package com.colorator;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import org.opencv.android.JavaCameraView;

import java.util.ArrayList;
import java.util.List;


public class ZoomCameraView extends JavaCameraView {
    private static final String TAG = "ZoomCameraView";

    public ZoomCameraView(Context context, int cameraId) {
        super(context, cameraId);
    }

    public ZoomCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected SeekBar mSeekBar;

    public void setZoomControl(SeekBar _seekBar) {
        mSeekBar = _seekBar;
    }

    protected void enableZoomControls(Camera.Parameters params) {

        final int maxZoom = params.getMaxZoom();
        mSeekBar.setMax(maxZoom);
        mSeekBar.setOnSeekBarChangeListener(
                new OnSeekBarChangeListener() {
                    int progressvalue = 0;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress,
                                                  boolean fromUser) {
                        // TODO Auto-generated method stub
                        progressvalue = progress;
                        Camera.Parameters params = mCamera.getParameters();
                        params.setZoom(progress);
                        mCamera.setParameters(params);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub
                    }


                }

        );

    }

    protected boolean initializeCamera(int width, int height) {

        boolean ret = super.initializeCamera(width, height);


        Camera.Parameters params = mCamera.getParameters();

        if (params.isZoomSupported())
            enableZoomControls(params);

        mCamera.setParameters(params);

        return ret;
    }

    Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean arg0, Camera arg1) {
            if (arg0) {
                mCamera.cancelAutoFocus();
            }
        }
    };

    public void setFocus(double[] touchedCoordinates) {
        final Rect tfocusRect = calculateTapArea(touchedCoordinates);
        try {
            List<Camera.Area> focusList = new ArrayList<Area>();
            Camera.Area focusArea = new Camera.Area(tfocusRect, 1000);
            focusList.add(focusArea);

            Camera.Parameters param = mCamera.getParameters();
            param.setFocusAreas(focusList);
            param.setMeteringAreas(focusList);
            mCamera.setParameters(param);

            mCamera.autoFocus(mAutoFocusCallback);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "Unable to autofocus");
        }
    }

    private Rect calculateTapArea(double[] touchedCoordinates) {
        Rect touchRect = new Rect(
                (int) (touchedCoordinates[0] - 1),
                (int) (touchedCoordinates[1] - 1),
                (int) (touchedCoordinates[0] + 1),
                (int) (touchedCoordinates[1] + 1));
        final Rect tapArea = new Rect(
                touchRect.left * 2000 / this.getWidth() - 1000,
                touchRect.top * 2000 / this.getHeight() - 1000,
                touchRect.right * 2000 / this.getWidth() - 1000,
                touchRect.bottom * 2000 / this.getHeight() - 1000);
        return tapArea;
    }
}