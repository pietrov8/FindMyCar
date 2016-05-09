package com.example.piotr.findmycar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MarkerInfo extends FragmentActivity implements OnMapReadyCallback {
    GoogleMap mMap;
    TextView marker_title;
    TextView marker_cor_lat;
    TextView marker_cor_long;
    TextView marker_date_create;
    TextView marker_description;
    Button marker_select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_info);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle_list = getIntent().getExtras();
        final String name_item = bundle_list.getString("name");
        final String lon = bundle_list.getString("long");
        final String lat = bundle_list.getString("lat");

        marker_title = (TextView) findViewById(R.id.marker_title_id);
        marker_cor_lat = (TextView) findViewById(R.id.marker_coordinates_lat_id);
        marker_cor_long = (TextView) findViewById(R.id.marker_coordinates_long_id);
        marker_date_create = (TextView) findViewById(R.id.marker_date_id);
        marker_description = (TextView) findViewById(R.id.marker_description_id);
        marker_select = (Button) findViewById(R.id.btn_choose_marker);

        JSONObject toSend = new JSONObject();

        try {
            toSend.put("action", "getAllMarkers");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String address = preferences.getString("address","");
        System.out.println(address);
        if(!address.equalsIgnoreCase("")) {
            JSONTransmitter asyncTask = (JSONTransmitter) new JSONTransmitter(new JSONTransmitter.AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    try {
                        JSONArray pages = new JSONArray(output);
                        for (int i = 0; i < pages.length(); ++i) {
                            JSONObject rec = pages.getJSONObject(i);
                            String name_task = rec.getString("nazwa");
                            String latitude = rec.getString("latitude");
                            String longituide = rec.getString("longitude");
                            String description = rec.getString("opis");
                            String date_create = rec.getString("data_utworzenia");
                            if (name_task.equals(name_item)) {
                                marker_title.setText(name_task);
                                marker_cor_lat.setText(latitude);
                                marker_cor_long.setText(longituide);
                                marker_description.setText(description);
                                marker_date_create.setText(date_create);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute(toSend,address);
        } else {
            Toast.makeText(this, "Ustaw poprawny adres w ustawieniach aplikacji", Toast.LENGTH_LONG).show();
        }


        marker_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle put_to_camera = new Bundle();
                put_to_camera.putString("lat", String.valueOf(marker_cor_lat.getText()));
                put_to_camera.putString("long", String.valueOf(marker_cor_long.getText()));
                put_to_camera.putString("name", name_item);
                Intent ii = new Intent(getApplicationContext(), CameraViewActivity.class);
                ii.putExtras(put_to_camera);
                startActivity(ii);
            }
        });


    }

    public void onMapReady(GoogleMap googleMap) {
        Bundle bundle_list = getIntent().getExtras();
        final String name_item = bundle_list.getString("name");
        final Double lon = Double.parseDouble(bundle_list.getString("long"));
        final Double lat = Double.parseDouble(bundle_list.getString("lat"));

        LatLng position = new LatLng(lat, lon);

        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

        mMap.addMarker(new MarkerOptions()
                .title(name_item)
                .snippet("The most populous city in Australia.")
                .position(position));

    }

}
