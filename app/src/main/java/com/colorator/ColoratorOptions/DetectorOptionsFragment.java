package com.colorator.ColoratorOptions;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.colorator.ColoratorImageProc.ColoratorImageProc;
import com.colorator.ColoratorOptions.DetectorOptions.DetectorArgsAbstractClass;
import com.colorator.MainActivity;
import com.colorator.OpenCVFragment;
import com.colorator.R;
import com.colorator.utils.FragmentWithFragments;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;


public class DetectorOptionsFragment extends FragmentWithFragments {
    public ColoratorImageProc mColoratorImageProc;
    private static final String TAG = "DetectorOptions::Fragment";
    private Button mSubmitButton;
    private static JSONObject mDetectorsConfiguration = MainActivity.readConfiguration("detectors_config");
    private final String mDetectorName;
    private DetectorArgsAbstractClass mDetectorArgsFragment;

    public DetectorOptionsFragment(ColoratorImageProc mainActivityImageProc, String detectorName) {
        mColoratorImageProc = mainActivityImageProc;
        mDetectorName = detectorName;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "called onCreateView");
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.detector_options_layout, container, false);
        mSubmitButton = view.findViewById(R.id.button_submit_detectors_options);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDetector();
                hideKeyboardFrom(Objects.requireNonNull(getContext()), view);
                ((MainActivity) getActivity()).loadFragment(new OpenCVFragment(mColoratorImageProc));
            }
        });
        setDetectorsArgsView();
        return view;
    }

    private void updateDetector() {
        try {
            String actualDetectorClass = mDetectorsConfiguration
                    .getJSONObject(mDetectorName)
                    .getString("ActualDetectorClass");
            Object detectorArgs = mDetectorArgsFragment.getDetectorsArgs();
            mColoratorImageProc.setDetector(actualDetectorClass, detectorArgs);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }


    private void setDetectorsArgsView() {
        try {
            String fragmentName = mDetectorsConfiguration
                    .getJSONObject(mDetectorName)
                    .getString("OptionsFragmentClass");
            Class<?> detectorArgsFragment = Class.forName(fragmentName);
            mDetectorArgsFragment = (DetectorArgsAbstractClass) detectorArgsFragment.newInstance();
            loadFragment((Fragment) mDetectorArgsFragment, R.id.detectors_args_container);
        } catch (ClassNotFoundException | IllegalAccessException | java.lang.InstantiationException | JSONException ex) {
            Log.e(TAG, "Unknown DetectorArgs class");
            ex.printStackTrace();
        }
    }
}
