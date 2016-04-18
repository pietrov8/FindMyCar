package com.example.piotr.findmycar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class EditActivity extends Activity implements OnLocationChangedListener {
    private MyCurrentLocation myCurrentLocation;
    double mMyLatitude = 0;
    double mMyLongitude = 0;
    Button update_coordinates;
    EditText marker_title;
    TextView marker_coordinates_edit;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        setupListeners();

        Bundle bundle_list = getIntent().getExtras();
        String name_item = bundle_list.getString("name");

        marker_title = (EditText)findViewById(R.id.marker_title_edit);
        update_coordinates = (Button)findViewById(R.id.update_coordinates);
        marker_coordinates_edit = (TextView) findViewById(R.id.marker_coordinates_edit);

        marker_title.setText(name_item);


        marker_coordinates_edit.setText(""+ mMyLatitude + mMyLongitude);

        update_coordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGPSAlert();
            }
        });

        try {
            JSONObject toSend = new JSONObject();
            toSend.put("msg", name_item);

            JSONTransmitter transmitter = new JSONTransmitter();
            transmitter.execute(new JSONObject[] {toSend});

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateGPSAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.update_coordinates_question)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER )) {
                            buildAlertMessageNoGps();
                        }
                        if (location != null) {
                            mMyLatitude = location.getLatitude();
                            mMyLongitude = location.getLongitude();
                            marker_coordinates_edit.setText("" + mMyLatitude + " " + mMyLongitude);
                        }
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
    public void onLocationChanged(Location location) {
        this.location = location;
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
