package com.example.ihsan.mymaps;

import android.os.Bundle;
import android.view.View;

import com.example.ihsan.mymaps.activity.ListMapActivity;
import com.example.ihsan.mymaps.activity.MapsActivity;
import com.example.ihsan.mymaps.activity.PlacePickerActivity;
import com.example.ihsan.mymaps.helper.MyFunction;

public class MainActivity extends MyFunction {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onMap(View view) {
        aksesclass(MapsActivity.class);
    }

    public void onPlacePicker(View view) {
      aksesclass(PlacePickerActivity.class);
    }

    public void onListMAp(View view) {
        aksesclass(ListMapActivity.class);
    }
}
