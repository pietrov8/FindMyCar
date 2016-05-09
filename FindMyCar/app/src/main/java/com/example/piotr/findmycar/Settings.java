package com.example.piotr.findmycar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class Settings extends Activity {
    private String[] arraySpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button check_device = (Button) findViewById(R.id.compontents_check);
        Button check_url = (Button) findViewById(R.id.test_connection);
        Button save_changes = (Button) findViewById(R.id.save_changes_settings);
        Button discard_changes = (Button) findViewById(R.id.no_save_changes_settings);

        final EditText adress = (EditText) findViewById(R.id.adress_ftp_file);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String address = preferences.getString("address","");

        adress.setText(address);

        this.arraySpinner = new String[] {
                "polski", "angielski"
        };
        Spinner s = (Spinner) findViewById(R.id.select_language);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);

        // *** Sprawdzenie podzespołów ****//

        check_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "";

                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE );
                boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                if (statusOfGPS) {
                    text += "GPS jest włączony \n \n";
                } else {
                    text += "GPS jest wyłączony \n \n";
                }


                int numCameras = Camera.getNumberOfCameras();
                if (numCameras > 0) {
                    text += "Twoje urządzenie posiada wymaganą kamerę \n \n";
                } else {
                    text += "Do działania aplikacji wymagane jest posiadanie kamery \n \n";
                }

                PackageManager manager_sensor = getPackageManager();
                boolean hasAccelerometer = manager_sensor.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
                if (hasAccelerometer) {
                    text += "Twoje urządzenie posiada wymagany kompas \n \n";
                } else {
                    text += "Do działania aplikacji wymagane jest posiadanie kompasu w telefonie. Ty go nie masz \n \n";
                }

                if (isConnected()) {
                    text += "Posiadasz dostęp do internetu. Fajnie! \n \n";
                } else {
                    text += "Do działania aplikacji wymagane jest połączenie z internetem. Udostępnij go ;) \n \n";
                }

                final AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                builder.setTitle("Sprawdź czy wszystko gra");
                builder.setMessage(text).setCancelable(true).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });;
                final AlertDialog alert = builder.create();
                alert.show();
            }
        });

        check_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String input = adress.getText().toString();
                CheckConnectionTask checkConnectionTask = (CheckConnectionTask) new CheckConnectionTask(new CheckConnectionTask.AsyncResponse() {
                    @Override
                    public void processFinish(String result) {
                        String text = "";
                        if (result == "1") {
                            text += "Adres poprawny \n";
                        } else {
                            text += "Nie można uzyskac połączenia z adresem, popraw go \n";
                        }
                        final AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                        builder.setTitle("Sprawdzanie połączenia z adresem");
                        builder.setMessage(text).setCancelable(true).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                        final AlertDialog alert = builder.create();
                        alert.show();
                    }
                }).execute(input);
            }
        });

        save_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String input = adress.getText().toString();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("address",input);
                editor.apply();
                finish();
            }
        });

        discard_changes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
}
