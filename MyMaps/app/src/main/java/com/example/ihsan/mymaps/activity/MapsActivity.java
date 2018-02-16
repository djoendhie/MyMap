package com.example.ihsan.mymaps.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ihsan.mymaps.R;
import com.example.ihsan.mymaps.helper.DirectionMapsV2;
import com.example.ihsan.mymaps.helper.GPStrack;
import com.example.ihsan.mymaps.helper.HeroHelper;
import com.example.ihsan.mymaps.model.Duration;
import com.example.ihsan.mymaps.model.Leg;
import com.example.ihsan.mymaps.model.Modelwaypoint;
import com.example.ihsan.mymaps.model.OverviewPolyline;
import com.example.ihsan.mymaps.network.ApiService;
import com.example.ihsan.mymaps.network.RetrofitConfig;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION = 1;
    @BindView(R.id.edtawal)
    EditText edtawal;
    @BindView(R.id.edtakhir)
    EditText edtakhir;
    @BindView(R.id.textjarak)
    TextView textjarak;
    @BindView(R.id.textwaktu)
    TextView textwaktu;
    @BindView(R.id.textharga)
    TextView textharga;
    @BindView(R.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R.id.btnlokasiku)
    Button btnlokasiku;
    @BindView(R.id.btnpanorama)
    Button btnpanorama;
    @BindView(R.id.linearbottom)
    LinearLayout linearbottom;
    @BindView(R.id.spinmode)
    Spinner spinmode;
    @BindView(R.id.relativemap)
    RelativeLayout relativemap;
    @BindView(R.id.frame1)
    FrameLayout frame1;
    private GoogleMap mMap;



    GPStrack gpStrack;
    Double lat,lon;
    LatLng lokasiku;
    String name_location;
    GoogleApiClient googleApiClient;
    Intent intent;
    DirectionMapsV2 directionMapsV2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
            Toast.makeText(this, "Gps already enabled", Toast.LENGTH_SHORT).show();
            //     finish();
        }
        // Todo Location Already on  ... end

        if (!hasGPSDevice(this)) {
            Toast.makeText(this, "Gps not Supported", Toast.LENGTH_SHORT).show();
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
            Toast.makeText(this, "Gps not enabled", Toast.LENGTH_SHORT).show();
            enableLoc();
        }
    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            Log.d("Location error", "Location error " + connectionResult.getErrorCode());
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(MapsActivity.this, REQUEST_LOCATION);

                                finish();
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }
    }

    private boolean hasGPSDevice(MapsActivity mapsActivity) {
        final LocationManager mgr = (LocationManager) mapsActivity
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        directionMapsV2 = new DirectionMapsV2(MapsActivity.this);

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-0.229834, 100.630917);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @OnClick({R.id.edtawal, R.id.edtakhir, R.id.btnlokasiku, R.id.btnpanorama})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.edtawal:
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(MapsActivity.this);
                    startActivityForResult(intent, 1);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;


            case R.id.edtakhir:
                try {
                    intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(MapsActivity.this);
                    startActivityForResult(intent, 2);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnlokasiku:
                akseslokasiku();
                break;


            case R.id.btnpanorama:
                akseslokasiku();
                aksespanorama();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Place place = PlaceAutocomplete.getPlace(this, data);
        //getlat dan get lon
        if (requestCode == 1 && resultCode == RESULT_OK) {
            lat = place.getLatLng().latitude;
            lon = place.getLatLng().longitude;
            name_location = place.getName().toString();
            edtawal.setText(name_location);
            mMap.clear();
            addmarker(lat, lon);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            lat = place.getLatLng().latitude;
            lon = place.getLatLng().longitude;
            name_location = place.getName().toString();
            edtakhir.setText(name_location);
            mMap.clear();
            addmarker(lat, lon);
            aksesrute();
        }
    }

    private void aksesrute() {
        ApiService service = RetrofitConfig.getInstanceRetrofit();
        Call<Modelwaypoint> modelwaypointCall = service.request_route(
                edtawal.getText().toString(), edtakhir.getText().toString()
        );
        modelwaypointCall.enqueue(new Callback<Modelwaypoint>() {
            @Override
            public void onResponse(Call<Modelwaypoint> call, Response<Modelwaypoint> response) {
                if (response.isSuccessful()) {

                  //////////////////////////////////////////////// /////////////////////////

                    List<com.example.ihsan.mymaps.model.Route> routes = response.body().getRoutes();
                    OverviewPolyline polyline = routes.get(0).getOverviewPolyline();
                    List<Leg> legs = routes.get(0).getLegs();

                    ////////////////////////////////////////////////////////////////////////////////

                    com.example.ihsan.mymaps.model.Distance distance = legs.get(0).getDistance();
                    Duration duration = legs.get(0).getDuration();
                    String jarak = distance.getText();
                    double valuejarak = Double.valueOf(distance.getValue());
                    textjarak.setText(jarak);
                    String waktu = duration.getText();
                    textwaktu.setText(waktu);

                    double harga = Math.ceil(valuejarak / 1000);
                    double total = harga * 1000;
                    textharga.setText("Rp." + HeroHelper.toRupiahFormat2(String.valueOf(total)));
                    String point = polyline.getPoints();
                    directionMapsV2.gambarRoute(mMap, point);
                }
            }

            @Override
            public void onFailure(Call<Modelwaypoint> call, Throwable t) {
                Toast.makeText(MapsActivity.this, "gagal" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addmarker(Double lat, Double lon) {
        lokasiku = new LatLng(lat, lon);
        name_location = convertname(lat, lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasiku, 15));
        mMap.addMarker(new MarkerOptions().position(lokasiku).title(name_location).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker)));
    }

    private void aksespanorama() {
        relativemap.setVisibility(View.GONE);
        frame1.setVisibility(View.VISIBLE);
        SupportStreetViewPanoramaFragment panorama = (SupportStreetViewPanoramaFragment) getSupportFragmentManager()
                .findFragmentById(R.id.panorama);
        panorama.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback() {
            @Override
            public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
                streetViewPanorama.setPosition(lokasiku);
            }
        });

    }

    private void akseslokasiku() {
        gpStrack = new GPStrack(MapsActivity.this);
        if (gpStrack.canGetLocation() && mMap != null) {
            lat = gpStrack.getLatitude();
            lon = gpStrack.getLongitude();
            mMap.clear();
            name_location = convertname(lat, lon);
            Toast.makeText(MapsActivity.this, "lat " + lat + "\nlon " + lon, Toast.LENGTH_SHORT).show();
            lokasiku = new LatLng(lat, lon);
            //add marker
            mMap.addMarker(new MarkerOptions().position(lokasiku).title(name_location)).setIcon(
                    BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker)
            );
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasiku, 16));

        }
    }

    private String convertname(Double lat, Double lon) {
        name_location = null;
        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        try {
            List<Address> list = geocoder.getFromLocation(lat,lon, 1);
            if(list != null&&list.size()>0) {
                name_location = list.get(0).getAddressLine(0) + "" + list.get(0).getCountryName();

                //fetch data from addresses
            }else{
                Toast.makeText(this, "kosong", Toast.LENGTH_SHORT).show();
                //display Toast message
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name_location;
    }
}
