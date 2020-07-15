package com.colorator.ColoratorOptions.DetectorOptions;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;

import com.colorator.R;
import com.colorator.customviews.ColorNamesAdapter;

public class ColorNamesDetectorArgsFragment extends DetectorArgsAbstractClass {
    private View mRootView;
    private SearchView mColorSearchBox;
    private ListView mListView;
    private ColorNamesAdapter mAdapter;
    public static final String TAG = "ColorNamesDetectorArgsFragment";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "called onCreateView");
        super.onCreate(savedInstanceState);
        mRootView = inflater.inflate(R.layout.choose_color_names_fragment, container, false);
        setAdapterToListview();
        setSearchBoxView();
        return mRootView;
    }

    private void setAdapterToListview() {
        mListView = mRootView.findViewById(R.id.color_names_listview);
        mAdapter = new ColorNamesAdapter(getContext(), "color_names");
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAdapter.changeColorState(position);
            }
        });
    }

    private void setSearchBoxView() {
        mColorSearchBox = mRootView.findViewById(R.id.search_color_name);
        int id = mColorSearchBox.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        EditText txtSearch = (EditText) mColorSearchBox.findViewById(id);
        txtSearch.setHintTextColor(Color.LTGRAY);
        txtSearch.setTextColor(Color.WHITE);
        mColorSearchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.filter(newText);
                return false;
            }
        });
    }

    @Override
    public Object getDetectorsArgs() {
        return mAdapter.getSelectedColor();
    }
}
