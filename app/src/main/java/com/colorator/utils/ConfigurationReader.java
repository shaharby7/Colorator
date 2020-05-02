package com.colorator.utils;


import android.content.Context;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class ConfigurationReader {
    private Context mContext;

    public ConfigurationReader(Context context) {
        mContext = context;
    }

    public JSONObject getConfigJson(String configName) {
        String jsonString;
        try {
            InputStream is = mContext.getAssets().open(configName + ".json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
            return new JSONObject(jsonString);
        } catch (IOException | org.json.JSONException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}