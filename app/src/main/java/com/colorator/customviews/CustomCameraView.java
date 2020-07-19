package com.colorator.customviews;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.colorator.utils.OpenCVHelpers;

import org.opencv.android.JavaCameraView;

import java.util.ArrayList;
import java.util.List;


public class CustomCameraView extends JavaCameraView {
    private static final String TAG = "ZoomCameraView";

    public CustomCameraView(Context context, int cameraId) {
        super(context, cameraId);
    }

    public CustomCameraView(Context context, AttributeSet attrs) {
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
                });
    }

    protected boolean initializeCamera(int width, int height) {
        boolean ret = super.initializeCamera(width, height);
        Camera.Parameters params = mCamera.getParameters();
        if (params.isZoomSupported())
            enableZoomControls(params);
        mCamera.setParameters(params);
        return ret;
    }


    public void freezePicture() {
        mCamera.setPreviewCallback(null);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);
    }
}