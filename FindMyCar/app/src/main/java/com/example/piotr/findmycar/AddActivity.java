package com.example.piotr.findmycar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    Button add_marker_button;


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
        add_marker_button = (Button)findViewById(R.id.add_marker);

        add_marker_button.setOnClickListener(new View.OnClickListener() {
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
                if (isConnected()){
                    if (error > 0) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
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
                        builder.setPositiveButton(R.string.correct_text, null);
                        builder.setTitle(R.string.validation_error);
                        builder.setCancelable(true);

                        final AlertDialog alert = builder.create();
                        alert.show();
                    } else {

                        JSONObject toSend = new JSONObject();
                        try {
                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            //get current date time with Date()
                            Date date = new Date();
                            toSend.put("action", "addMarker");
                            toSend.put("nazwa", marker_title_val);
                            toSend.put("latitude", marker_lat_val);
                            toSend.put("longitude", marker_long_val);
                            toSend.put("opis", descriptionTextView_val);
                            toSend.put("data_utworzenia", dateFormat.format(date));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println(toSend);

                        JSONTransmitter asyncTask = (JSONTransmitter) new JSONTransmitter(new JSONTransmitter.AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                                System.out.println(output);
                            }
                        }).execute(toSend);

                        Intent i = new Intent(getApplicationContext(), ListActivity.class);
                        finish();
                        startActivity(i);
                    }} else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);
                        builder.setTitle(R.string.no_internet);
                        builder.setMessage(R.string.no_internet_information_add_marker);
                        builder.setPositiveButton(R.string.ok, null);
                        builder.show();
                    }

            }
        });

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


     public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
    @Override
    public void onLocationChanged(Location location) {
        mMyLatitude = location.getLatitude();
        mMyLongitude = location.getLongitude();
        marker_lat.setText(mMyLatitude+"");
        marker_long.setText(mMyLongitude+"");

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
