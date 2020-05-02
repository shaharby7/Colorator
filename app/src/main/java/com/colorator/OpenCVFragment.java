package com.colorator;

import com.colorator.ColoratorImageProc.ColoratorImageProc;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.CompoundButton;

import org.opencv.android.JavaCameraView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.util.Objects;

public class OpenCVFragment extends Fragment implements CvCameraViewListener2 {

    private static final String TAG = "OCVSample::Fragment";
    private View mView;
    private ColoratorImageProc mColoratorImageProc;
    private CameraBridgeViewBase mOpenCvCameraView;
    private Switch mCommitProcessSwitch;
    private Context mAppContext;

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
        Log.i(TAG, "Instantiated new " + this.getClass());
        mColoratorImageProc = mainActivityImageProc;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        mAppContext = Objects.requireNonNull(getActivity()).getApplicationContext();
        super.onCreate(savedInstanceState);
        mView = inflater.inflate(R.layout.camera_fragment, container, false);
        mOpenCvCameraView = (JavaCameraView) mView.findViewById(R.id.show_camera_activity_java_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mCommitProcessSwitch = (Switch) mView.findViewById(R.id.commit_process_switch);
        mCommitProcessSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mColoratorImageProc.setCommitProcess(isChecked);
            }
        });
        return mView;
    }

    @Override
    public void onPause() {
        super.onPause();
//        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
//        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        mColoratorImageProc.allocateImageSize(height, width);
        mColoratorImageProc.setCommitProcess(mCommitProcessSwitch.isChecked());
    }

    public void onCameraViewStopped() {
        mColoratorImageProc.releaseResources();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        return mColoratorImageProc.pipeline(inputFrame);
    }
}