package com.example.ihsan.mymaps.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.example.ihsan.mymaps.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlacePickerActivity extends AppCompatActivity {

    @BindView(R.id.bt_ppicker)
    Button btPpicker;
    @BindView(R.id.tv_place_id)
    TextView tvPlaceId;
    GeoDataApi geoDataApi;
        GoogleApiClient apiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_picker);
        ButterKnife.bind(this);
       }

    @OnClick(R.id.bt_ppicker)
    public void onViewClicked() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try{
            startActivityForResult(builder.build(PlacePickerActivity.this),1);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1&&resultCode==RESULT_OK){
            Place place = PlacePicker.getPlace(PlacePickerActivity.this,data);
            place.getViewport();
            place.getAddress();
            place.getAttributions();
            place.getWebsiteUri();
            place.getRating();
            String informasialamat =String.format("place : %s \n "+
                    "alamat %s \n"+"latlong : %s",place.getName(),place.getAddress(),
                    place.getLatLng().latitude+","+place.getLatLng().longitude);
            tvPlaceId.setText(informasialamat);

//
//            geoDataApi.getPlaceById().addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
//                @Override
//                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
//                    if (task.isSuccessful()) {
//                        PlaceBufferResponse places = task.getResult();
//                        Place myPlace = places.get(0);
//                        Log.i(TAG, "Place found: " + myPlace.getName());
//                        places.release();
//                    } else {
//                        Log.e(TAG, "Place not found.");
//                    }
//                }
//            });


        }

    }
}
