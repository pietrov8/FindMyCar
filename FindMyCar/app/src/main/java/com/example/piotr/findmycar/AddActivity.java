package com.example.piotr.findmycar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class AddActivity extends Activity implements OnLocationChangedListener {

    @Override
    public void setTitle(CharSequence title) {
        setTitle(R.string.Activity_add_title);
    }

    private MyCurrentLocation myCurrentLocation;
    EditText marker_title;
    TextView marker_lat;
    TextView marker_long;
    EditText descriptionTextView;
    private double mMyLatitude = 0;
    private double mMyLongitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        marker_title = (EditText) findViewById(R.id.marker_title);
        marker_lat = (TextView) findViewById(R.id.marker_coordinates_lat);
        marker_long = (TextView) findViewById(R.id.marker_coordinates_long);
        descriptionTextView = (EditText) findViewById(R.id.marker_title_text);


        try {
            JSONObject toSend = new JSONObject();
            toSend.put("nazwa", marker_title.getText().toString());
            toSend.put("latitude", marker_lat.getText().toString());
            toSend.put("longitude", marker_long.getText().toString());
            toSend.put("opis", descriptionTextView.getText().toString());
            toSend.put("data_utworzenia", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            toSend.put("aktywny", 1);
            toSend.put("usuniety", 0);

            JSONTransmitter transmitter = new JSONTransmitter();
            transmitter.execute(new JSONObject[] {toSend});

        } catch (JSONException e) {
            e.printStackTrace();
        }

        setupListeners();

        /*** CHECK GPS ***/
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER )) {
            buildAlertMessageNoGps();
        }


    }

    public void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.gps_disabled_message)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void onLocationChanged(Location location) {
        mMyLatitude = location.getLatitude();
        mMyLongitude = location.getLongitude();
        descriptionTextView.setText(""+ mMyLatitude + " " + mMyLongitude);

    }
    private void setupListeners() {
        myCurrentLocation = new MyCurrentLocation(this);
        myCurrentLocation.buildGoogleApiClient(this);
        myCurrentLocation.start();
    }
    @Override
    protected void onStop() {
        myCurrentLocation.stop();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myCurrentLocation.start();
    }

}
