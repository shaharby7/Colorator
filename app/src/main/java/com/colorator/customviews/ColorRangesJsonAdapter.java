package com.colorator.customviews;

import com.colorator.MainActivity;
import com.colorator.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

public class ColorRangesJsonAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    private JSONObject mColorRanges;
    private ArrayList<String> mColorNames = new ArrayList<String>();
    private ArrayList<String> mFilteredColorNames = new ArrayList<String>();
    private ArrayList<String> mSelectedColorNames = new ArrayList<String>();

    public ColorRangesJsonAdapter(Context context, String colorRangesAssetName) {
        mContext = context;
        mColorRanges = MainActivity.readConfiguration(colorRangesAssetName);
        Iterator<String> keysItr = mColorRanges.keys();
        while (keysItr.hasNext()) {
            String colorName = keysItr.next();
            mColorNames.add(colorName);
            mFilteredColorNames.add(colorName);
        }
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mFilteredColorNames.size();
    }

    @Override
    public JSONObject getItem(int position) {
        try {
            String colorName = mFilteredColorNames.get(position);
            return mColorRanges.getJSONObject(colorName);
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.color_names_searchbox_item, null);
        }
        TextView ColorName = (TextView) view.findViewById(R.id.color_name_label);
        View ColorPresentation = (View) view.findViewById(R.id.color_name_representation);
        String colorName = mFilteredColorNames.get(position);
        try {
            ColorName.setText(colorName);
            ColorPresentation.setBackgroundColor(Color.parseColor(getItem(position).getString("Hex")));
        } catch (JSONException js) {
            js.printStackTrace();
        }
        if (mSelectedColorNames.contains(colorName)) {
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.halfTransparent));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.defaultBackground));
        }
        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        if (charText.length() == 0) {
            mFilteredColorNames.addAll(mColorNames);
        } else {
            mFilteredColorNames.clear();
            Iterator<String> iter = mColorNames.iterator();
            while (iter.hasNext()) {
                String colorName = iter.next();
                if (colorName.toLowerCase(Locale.getDefault()).contains(charText)) {
                    mFilteredColorNames.add(colorName);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void changeColorState(int position) {
        String colorName = mFilteredColorNames.get(position);
        if (mSelectedColorNames.contains(colorName)) {
            mSelectedColorNames.remove(colorName);
        } else {
            mSelectedColorNames.add(colorName);
        }
        notifyDataSetChanged();
    }

    public void selectAllShownColors() {
        for (String colorName : mFilteredColorNames) {
            if (!mSelectedColorNames.contains(colorName)) {
                mSelectedColorNames.add(colorName);
            }
        }
        notifyDataSetChanged();
    }

    public void unSelectAllShownColors() {
        for (String colorName : mFilteredColorNames) {
            mSelectedColorNames.remove(colorName);
        }
        notifyDataSetChanged();
    }

    public JSONArray getSelectedRanges() {
        JSONArray results = new JSONArray();
        for (String selectedColor : mSelectedColorNames) {
            try {
                results.put(mColorRanges.getJSONObject(selectedColor));
            } catch (JSONException js) {
                js.printStackTrace();
            }
        }
        return results;
    }

}