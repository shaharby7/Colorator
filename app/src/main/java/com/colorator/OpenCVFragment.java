package com.colorator;

import com.colorator.ColoratorImageProc.ColoratorImageProc;
import com.colorator.customviews.CustomCameraView;
import com.colorator.utils.OpenCVHelpers;

import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.widget.Toast;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;

public class OpenCVFragment extends Fragment implements CvCameraViewListener2, OnTouchListener {

    private static final String TAG = "OCVSample::Fragment";
    private View mView;
    private ColoratorImageProc mColoratorImageProc;
    private CustomCameraView mOpenCvCameraView;
    private Switch mCommitProcessSwitch;
    private Context mAppContext;
    private double[] mTouchedCoordinates = new double[2];
    private double[] mCameraOffset = new double[2];
    private Point mTouchedPoint = new Point();

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(mAppContext) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public OpenCVFragment(ColoratorImageProc mainActivityImageProc) {
        super();
        Log.i(TAG, "Instantiated new " + this.getClass());
        mColoratorImageProc = mainActivityImageProc;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        mAppContext = Objects.requireNonNull(getActivity()).getApplicationContext();
        super.onCreate(savedInstanceState);
        mView = inflater.inflate(R.layout.camera_fragment, container, false);
        mOpenCvCameraView = mView.findViewById(R.id.show_camera_activity_java_surface_view);
        mOpenCvCameraView.setZoomControl((SeekBar) mView.findViewById(R.id.zoom_seekbar));
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setOnTouchListener(this);
        mCommitProcessSwitch = mView.findViewById(R.id.commit_process_switch);
        mCommitProcessSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mColoratorImageProc.setCommitProcess(isChecked);
            }
        });
        final Button takePictureButton = mView.findViewById(R.id.take_picture_button);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
        return mView;
    }


    @Override
    public void onPause() {
        super.onPause();
        Objects.requireNonNull(getActivity()).getWindow()
                .clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getActivity()).getWindow()
                .addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, mAppContext, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mColoratorImageProc.allocateMatsBeforeRunning();
        mColoratorImageProc.setCommitProcess(mCommitProcessSwitch.isChecked());
    }

    public void onCameraViewStopped() {
        //TODO: fix "Resize Mat" of coloratorMatManager, doesn't really resizing anything so the app crashes anytime
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        return mColoratorImageProc.pipeline(inputFrame);
    }

    @SuppressLint("SimpleDateFormat")
    private void takePicture() {
        mOpenCvCameraView.freezePicture();
        ((MainActivity) Objects.requireNonNull(getActivity()))
                .checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Mat lastResult = mColoratorImageProc.getLastResult();
        Bitmap image = OpenCVHelpers.mat2Bitmap(lastResult);
        String imageName = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        try {
            OpenCVHelpers.saveImage(image, imageName, mAppContext.getContentResolver());
        } catch (IOException e) {
            Toast.makeText(mAppContext,
                    "For some reason image could not be written",
                    Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mCameraOffset[0] = (mOpenCvCameraView.getWidth() - mColoratorImageProc.getFrameWidth()) / 2.;
        mCameraOffset[1] = (mOpenCvCameraView.getHeight() - mColoratorImageProc.getFrameHeight()) / 2.;
        mTouchedCoordinates[0] = event.getX() - mCameraOffset[0];
        mTouchedCoordinates[1] = event.getY() - mCameraOffset[1];
        mTouchedPoint.set(mTouchedCoordinates);
        mColoratorImageProc.onTouch(event, mTouchedPoint);
        return true;
    }
}