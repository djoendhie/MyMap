package com.example.ihsan.mymaps.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ihsan.mymaps.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashSet;

public class ListMapActivity extends AppCompatActivity {

    ListFragment listFragment ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_map);
        listFragment =(ListFragment)getSupportFragmentManager().findFragmentById(R.id.listmap);
        Mapadapter mapadapter = new Mapadapter(ListMapActivity.this,lokasi);
        listFragment.setListAdapter(mapadapter);
        AbsListView listView = listFragment.getListView();
        listView.setRecyclerListener(listener);

    }

    private AbsListView.RecyclerListener listener = new AbsListView.RecyclerListener() {
        @Override
        public void onMovedToScrapHeap(View view) {
            ViewHolder holder = (ViewHolder)view.getTag();
            holder.map.clear();
            holder.map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
    };


    public  static namalokasi[] lokasi =new namalokasi[]{
            new namalokasi("jakarta barat",new LatLng(-6.1972953,106.7937623)),
            new namalokasi("jakarta selatan",new LatLng(-6.214735,106.8427373)),
            new namalokasi("jakarta timur",new LatLng(-6.2611295,106.7649303)),
            new namalokasi("jakarta utara",new LatLng(-6.1237748,106.8296171)),
            new namalokasi("jakarta pusat",new LatLng(-6.1753924,106.8249641)),

    };

    private static class namalokasi{
        String name;
        LatLng location;

        public namalokasi(String name, LatLng location) {
            this.name = name;
            this.location = location;
        }
    }
    class ViewHolder implements OnMapReadyCallback {

        MapView mapView;

        TextView title;

        GoogleMap map;

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(getApplicationContext());
            map = googleMap;
            namalokasi data = (namalokasi) mapView.getTag();
            if (data != null) {
                setMapLocation(map, data);
            }
        }

        /**
         * Initialises the MapView by calling its lifecycle methods.
         */
        public void initializeMapView() {
            if (mapView != null) {
                // Initialise the MapView
                mapView.onCreate(null);
                // Set the map ready callback to receive the GoogleMap object
                mapView.getMapAsync(this);
            }
        }

    }

    private void setMapLocation(GoogleMap map, namalokasi data) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(data.location,16));
        map.addMarker(new MarkerOptions().position(data.location)).setIcon(BitmapDescriptorFactory.
        fromResource(R.mipmap.ic_marker));
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    }


    private class Mapadapter extends ArrayAdapter<namalokasi> {
        HashSet<MapView> mapViews = new HashSet<>();
        Mapadapter(Context context, namalokasi[] namalokasis){
            super(context,R.layout.tampilanmap,R.id.text,namalokasis);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder;

            // Check if a view can be reused, otherwise inflate a layout and set up the view holder
            if (row == null) {
                // Inflate view from layout file
                row = getLayoutInflater().inflate(R.layout.tampilanmap, null);

                // Set up holder and assign it to the View
                holder = new ViewHolder();
                holder.mapView = (MapView) row.findViewById(R.id.map1);
                holder.title = (TextView) row.findViewById(R.id.text);
                // Set holder as tag for row for more efficient access.
                row.setTag(holder);

                // Initialise the MapView
                holder.initializeMapView();

                // Keep track of MapView
                mapViews.add(holder.mapView);
            } else {
                // View has already been initialised, get its holder
                holder = (ViewHolder) row.getTag();
            }

            // Get the NamedLocation for this item and attach it to the MapView
            namalokasi item = getItem(position);
            holder.mapView.setTag(item);

            // Ensure the map has been initialised by the on map ready callback in ViewHolder.
            // If it is not ready yet, it will be initialised with the NamedLocation set as its tag
            // when the callback is received.
            if (holder.map != null) {
                // The map is already ready to be used
                setMapLocation(holder.map, item);
            }

            // Set the text label for this item
            holder.title.setText(item.name);

            return row;
        }

        /**
         * Retuns the set of all initialised {@link MapView} objects.
         *
         * @return All MapViews that have been initialised programmatically by this adapter
         */
        public HashSet<MapView> getMaps() {
            return mapViews;
        }
    }

}
