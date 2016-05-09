package com.example.piotr.findmycar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends Activity implements OnLocationChangedListener {
    private MyCurrentLocation myCurrentLocation;
    double mMyLatitude = 0;
    double mMyLongitude = 0;
    Button update_coordinates;
    Button change_marker;
    EditText marker_title;
    TextView marker_lat;
    TextView marker_long;
    EditText descriptionTextView;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        setupListeners();

        Bundle bundle_list = getIntent().getExtras();
        final String name_item = bundle_list.getString("name");

        marker_title = (EditText)findViewById(R.id.marker_title_edit);
        marker_lat = (TextView)findViewById(R.id.marker_coordinates_lat);
        marker_long = (TextView)findViewById(R.id.marker_coordinates_long);
        descriptionTextView = (EditText)findViewById(R.id.marker_title_text_edit);
        update_coordinates = (Button)findViewById(R.id.update_coordinates);
        change_marker = (Button)findViewById(R.id.change_marker);

        JSONObject toSend = new JSONObject();
        try {
            toSend.put("action", "getAllMarkers");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String address = preferences.getString("address","");
        System.out.println(address);
        if(!address.equalsIgnoreCase("")) {
        JSONTransmitter asyncTask = (JSONTransmitter) new JSONTransmitter(new JSONTransmitter.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                try {
                    JSONArray pages     =  new JSONArray(output);
                    for (int i = 0; i < pages.length(); ++i) {
                        JSONObject rec = pages.getJSONObject(i);
                        String name_task = rec.getString("nazwa");
                        String latitude = rec.getString("latitude");
                        String longituide = rec.getString("longitude");
                        String description = rec.getString("opis");
                        if (name_task.equals(name_item)) {
                            marker_title.setText(name_task);
                            marker_lat.setText(latitude);
                            marker_long.setText(longituide);
                            descriptionTextView.setText(description);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).execute(toSend,address);
        } else {
            Toast.makeText(EditActivity.this, "Ustaw poprawny adres w ustawieniach aplikacji", Toast.LENGTH_LONG).show();
        }

        change_marker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String marker_title_val = marker_title.getText().toString();
                final String marker_lat_val = marker_lat.getText().toString();
                final String marker_long_val = marker_long.getText().toString();
                final String descriptionTextView_val = descriptionTextView.getText().toString();
                int error = 0;
                int error1 = 0;
                int error2 = 0;
                int error3 = 0;

                if (marker_title_val.length() == 0) {
                    error1++;
                }
                if (marker_lat_val.length() == 0) {
                    error2++;
                }
                if (descriptionTextView_val.length() == 0) {
                    error3++;
                }

                error = error1 + error2 + error3;

                if (error > 0) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
                    String message_validation="";
                    String message_validation1="";
                    String message_validation2="";
                    String message_validation3="";
                    if (error > 0) {
                        message_validation1 = getString(R.string.validation_title);
                    }
                    if (error2 > 0) {
                        message_validation2 = getString(R.string.validation_coordinates);
                    }
                    if (error3 > 0) {
                        message_validation3 = getString(R.string.validation_description);
                    }
                    message_validation = message_validation1 +"\n" + message_validation2 + "\n" + message_validation3;
                    builder.setMessage(message_validation);
                    builder.setPositiveButton(R.string.correct_text,null);
                    builder.setTitle(R.string.validation_error);
                    builder.setCancelable(true);

                    final AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    JSONObject toSend2 = new JSONObject();
                    try {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        //get current date time with Date()
                        Date date = new Date();
                        toSend2.put("action", "updateMarker");
                        toSend2.put("nazwa", marker_title_val);
                        toSend2.put("latitude", marker_lat_val);
                        toSend2.put("longitude", marker_long_val);
                        toSend2.put("opis", descriptionTextView_val);
                        toSend2.put("data_utworzenia", dateFormat.format(date));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println(toSend2);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String address = preferences.getString("address","");
                    System.out.println(address);
                    if(!address.equalsIgnoreCase("")) {
                    JSONTransmitter asyncTask = (JSONTransmitter) new JSONTransmitter(new JSONTransmitter.AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            System.out.println(output);
                        }
                    }).execute(toSend2,address);
                    } else {
                        Toast.makeText(EditActivity.this, "Ustaw poprawny adres w ustawieniach aplikacji", Toast.LENGTH_LONG).show();
                    }

                    Intent i = new Intent(getApplicationContext(), ListActivity.class);
                    finish();
                    startActivity(i);
                }
            }
        });

        update_coordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGPSAlert();
            }
        });

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
                            marker_lat.setText(mMyLatitude+"");
                            marker_long.setText(mMyLongitude+"");
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
