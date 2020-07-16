package com.colorator;

import com.colorator.ColoratorImageProc.ColoratorImageProc;
import com.colorator.ColoratorOptions.DetectorOptionsFragment;
import com.colorator.utils.ConfigurationReader;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static ConfigurationReader appConfigurationReader;
    public ColoratorImageProc mColoratorImageProc;
    private JSONObject mDetectorConfig;
    private String mDetectorName = "touch samples";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appConfigurationReader = new ConfigurationReader(getApplicationContext());
        mColoratorImageProc = new ColoratorImageProc();
        mDetectorConfig = readConfiguration("detectors_config");
        setContentView(R.layout.activity_main);
        loadFragment(new OpenCVFragment(mColoratorImageProc));
        setDetectorsMenu();
        markSelectedButton();
    }

    private void setDetectorsMenu() {
        ViewGroup menu = findViewById(R.id.detectors_menu);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Iterator<String> keys = mDetectorConfig.keys();
        while (keys.hasNext()) {
            final String detectorName = keys.next();
            View itemHolder = newDetectorMenuItem(inflater, detectorName);
            menu.addView(itemHolder);
        }
    }

    private View newDetectorMenuItem(LayoutInflater inflater, final String detectorName) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, 100);
        layoutParams.weight = 1;
        View itemHolder = inflater.inflate(R.layout.detector_menu_item, null);
        Button item = itemHolder.findViewById(R.id.detectors_menu_item);
        item.setText(detectorName);
        item.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDetectorMenuItemSelected(detectorName);
            }
        });
        itemHolder.setLayoutParams(layoutParams);
        return itemHolder;
    }

    private void onDetectorMenuItemSelected(String detectorName) {
        if (mDetectorName.equals(detectorName)) {
            return;
        } else {
            try {
                mDetectorName = detectorName;
                markSelectedButton();
                if (!mDetectorConfig.getJSONObject(detectorName).getString("OptionsFragmentClass").equals("null")) {
                    loadFragment(new DetectorOptionsFragment(mColoratorImageProc, detectorName));
                } else {
                    mColoratorImageProc.setDetector(mDetectorConfig.getJSONObject(detectorName).getString("ActualDetectorClass"));
                    loadFragment(new OpenCVFragment(mColoratorImageProc));
                }
            } catch (JSONException ex) {
                Log.e(TAG, "Detector config is invalid");
            }
        }
    }

    private void markSelectedButton() {
        ViewGroup menu = findViewById(R.id.detectors_menu);
        for (int i = 0; i < menu.getChildCount(); i++) {
            Button item = menu.getChildAt(i).findViewById(R.id.detectors_menu_item);
            String name = item.getText().toString();
            if (name.equals(mDetectorName)) {
                item.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.halfTransparent));
            } else {
                item.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }


    public void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragments_container, fragment);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();
    }

    public static JSONObject readConfiguration(String configName) {
        return appConfigurationReader.getConfigJson(configName);
    }
}