package com.example.ihsan.mymaps.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.widget.Toast;


public class MyFunction extends AppCompatActivity {
    Animation animation;
    Context c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 //       setContentView(R.layout.activity_my_function);
    c=MyFunction.this;
    }

    public void mytoast(String isipesan){
        Toast.makeText(this, isipesan, Toast.LENGTH_SHORT).show();
    }
    public void aksesclass(Class kelastujuan){
        startActivity(new Intent(c,kelastujuan));
    }


    public void showSettingGps() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(c);

        alertBuilder.setTitle("GPS Setting");
        alertBuilder.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        alertBuilder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent,1); }
        });
        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        alertBuilder.show();
    }


}
