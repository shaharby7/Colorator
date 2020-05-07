package com.colorator.ColoratorOptions;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.colorator.ColoratorImageProc.ColoratorImageProc;
import com.colorator.MainActivity;
import com.colorator.OpenCVFragment;
import com.colorator.R;
import com.colorator.utils.FragmentWithFragments;


import org.json.JSONException;
import org.json.JSONObject;


public class DetectorOptionsFragment extends FragmentWithFragments {
    public ColoratorImageProc mColoratorImageProc;
    private static final String TAG = "DetectorOptions::Fragment";
    private Spinner mSpinner;
    private Button mSubmitButton;
    private ViewGroup mArgsContainer;
    private static JSONObject mDetectorsConfiguration = MainActivity.readConfiguration("detectors_config");
    private DetectorArgsAbstractClass mDetectorArgsFragment;

    public DetectorOptionsFragment(ColoratorImageProc mainActivityImageProc) {
        mColoratorImageProc = mainActivityImageProc;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "called onCreateView");
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.detector_options_layout, container, false);
        mSpinner = view.findViewById(R.id.detectors_spinner);
        mArgsContainer = view.findViewById(R.id.detectors_args_container);
        mSubmitButton = view.findViewById(R.id.button_submit_detectors_options);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDetector();
                ((MainActivity) getActivity()).loadFragment(new OpenCVFragment(mColoratorImageProc));
            }
        });
        setDetectorsArgsView();
        return view;
    }

    private void updateDetector() {
        try {
            String actualDetectorClass = mDetectorsConfiguration
                    .getJSONObject(mSpinner.getSelectedItem().toString())
                    .getString("ActualDetectorClass");
            JSONObject detectorArgs = mDetectorArgsFragment.getDetectorsArgs();
            mColoratorImageProc.setDetector(actualDetectorClass, detectorArgs);

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }


    private void setDetectorsArgsView() {
        try {
            String fragmentName = mDetectorsConfiguration
                    .getJSONObject(mSpinner.getSelectedItem().toString())
                    .getString("options_fragment_name");
//            Fragment argsFragment = (Fragment) Class.forName(fragmentName).newInstance();
            mDetectorArgsFragment = new ColorpickerDetectorArgsFragment();
            loadFragment(mDetectorArgsFragment, R.id.detectors_args_container);
        } catch (
                org.json.JSONException
//                        | java.lang.InstantiationException |java.lang.ClassNotFoundException | IllegalAccessException
                        ex
        ) {
            ex.printStackTrace();
        }
    }
}
