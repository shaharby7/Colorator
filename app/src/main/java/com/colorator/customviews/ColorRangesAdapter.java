package com.colorator.customviews;

import com.colorator.MainActivity;
import com.colorator.R;
import com.colorator.utils.ColorRange;

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
import java.util.List;
import java.util.Locale;

public class ColorRangesAdapter extends BaseAdapter {
    Context mContext;
    LayoutInflater inflater;
    private List<ColorRange> mAllColorRanges = new ArrayList<>();
    private List<ColorRange> mFilteredColorRanges = new ArrayList<>();
    private List<ColorRange> mSelectedColorRanges = new ArrayList<>();

    public ColorRangesAdapter(Context context, String colorRangesAssetName) {
        mContext = context;
        try {
            JSONArray colorNamesConfig = MainActivity.readConfiguration(colorRangesAssetName).getJSONArray("colors");
            for (int i = 0; i < colorNamesConfig.length(); i++) {
                mAllColorRanges.add(new ColorRange((JSONObject) colorNamesConfig.get(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mFilteredColorRanges.addAll(mAllColorRanges);
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mFilteredColorRanges.size();
    }

    @Override
    public ColorRange getItem(int position) {
        return mFilteredColorRanges.get(position);
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
        String colorName = getItem(position).getColorName();
        ColorName.setText(colorName);
        ColorPresentation.setBackgroundColor(Color.parseColor(getItem(position).getHex()));
        if (isNameInColorRanges(colorName, mSelectedColorRanges)) {
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.halfTransparent));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.defaultBackground));
        }
        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        if (charText.length() == 0) {
            mFilteredColorRanges.addAll(mAllColorRanges);
        } else {
            mFilteredColorRanges.clear();
            for (ColorRange colorRange : mAllColorRanges) {
                if (colorRange.getColorName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    mFilteredColorRanges.add(colorRange);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void changeColorState(int position) {
        String colorName = getItem(position).getColorName();
        Iterator<ColorRange> iterator = mSelectedColorRanges.iterator();
        while (iterator.hasNext()) {
            ColorRange colorRange = iterator.next();
            if (colorRange.getColorName().equals(colorName)) {
                iterator.remove();
                notifyDataSetChanged();
                return;
            }
        }
        mSelectedColorRanges.add(getItem(position));
        notifyDataSetChanged();
    }

    public void selectAllShownColors() {
        for (ColorRange colorRange : mFilteredColorRanges) {
            if (!isNameInColorRanges(colorRange.getColorName(), mSelectedColorRanges)) {
                mSelectedColorRanges.add(colorRange);
            }
        }
        notifyDataSetChanged();
    }

    public void unSelectAllShownColors() {
        mSelectedColorRanges.clear();
        notifyDataSetChanged();
    }

    public List<ColorRange> getSelectedRanges() {
        return mSelectedColorRanges;
    }

    private static boolean isNameInColorRanges(String colorName, List<ColorRange> colorRanges) {
        for (int i = 0; i < colorRanges.size(); i++) {
            if (colorRanges.get(i).getColorName().equals(colorName)) {
                return true;
            }
        }
        return false;
    }
}