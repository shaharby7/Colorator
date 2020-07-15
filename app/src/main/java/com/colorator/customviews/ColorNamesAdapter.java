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
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ColorNamesAdapter extends BaseAdapter {
    private static class ColorDescription {
        public String hex, name;
        public Integer h, s, v;

        ColorDescription(JSONObject json) {
            try {
                name = json.getString("Name");
                hex = json.getString("Hex");
                h = json.getInt("H");
                s = json.getInt("S");
                v = json.getInt("V");
            } catch (JSONException ignored) {
            }
        }
    }

    Context mContext;
    LayoutInflater mInflater;
    private List<ColorDescription> mAllColorRanges = new ArrayList<>();
    private List<ColorDescription> mFilteredColorRanges = new ArrayList<>();
    private ColorDescription mSelectedColor;

    public ColorNamesAdapter(Context context, String colorRangesAssetName) {
        mContext = context;
        try {
            JSONArray colorNamesConfig = MainActivity.readConfiguration(colorRangesAssetName).getJSONArray("colors");
            for (int i = 0; i < colorNamesConfig.length(); i++) {
                mAllColorRanges.add(new ColorDescription((JSONObject) colorNamesConfig.get(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mFilteredColorRanges.addAll(mAllColorRanges);
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mFilteredColorRanges.size();
    }

    @Override
    public ColorDescription getItem(int position) {
        return mFilteredColorRanges.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        if (view == null) {
            view = mInflater.inflate(R.layout.color_names_searchbox_item, null);
        }
        TextView ColorName = (TextView) view.findViewById(R.id.color_name_label);
        View ColorPresentation = (View) view.findViewById(R.id.color_name_representation);
        String colorName = getItem(position).name;
        ColorName.setText(colorName);
        ColorPresentation.setBackgroundColor(Color.parseColor(getItem(position).hex));
        if (mSelectedColor == null || !colorName.equals(mSelectedColor.name)) {
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.defaultBackground));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.halfTransparent));
        }
        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        if (charText.length() == 0) {
            mFilteredColorRanges.addAll(mAllColorRanges);
        } else {
            mFilteredColorRanges.clear();
            for (ColorDescription color : mAllColorRanges) {
                if (color.name.toLowerCase(Locale.getDefault()).contains(charText)) {
                    mFilteredColorRanges.add(color);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void changeColorState(int position) {
        ColorDescription color = getItem(position);
        if (mSelectedColor == null) {
            mSelectedColor = color;
        } else if (mSelectedColor.name.equals(color.name)) {
            mSelectedColor = null;
        } else {
            mSelectedColor = color;
        }
        notifyDataSetChanged();
    }

    public Scalar getSelectedColor() {
        return new Scalar(mSelectedColor.h, mSelectedColor.s, mSelectedColor.v);
    }
}