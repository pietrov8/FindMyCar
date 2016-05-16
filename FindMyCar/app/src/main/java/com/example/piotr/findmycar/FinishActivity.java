package com.example.piotr.findmycar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class FinishActivity extends Activity {
    ImageView imageOK;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        Button delete_marker = (Button) findViewById(R.id.btn_delete_marker_finish);
        Button back_finish = (Button) findViewById(R.id.btn_back_finish);
        Button close_finish = (Button) findViewById(R.id.btn_close_finish);

        imageOK = (ImageView) findViewById(R.id.imageOk);

        Bundle bundle_list = getIntent().getExtras();
        final String name_item = bundle_list.getString("name");

        back_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent i = new Intent(getApplicationContext(), Home_Screen.class);
                startActivity(i);
            }
        });


        close_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Home_Screen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
            }
        });

        delete_marker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(FinishActivity.this);
                    alert.setTitle(R.string.delete_alert_title);
                    alert.setMessage(R.string.delete_alert);
                    alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JSONObject toSend = new JSONObject();
                            try {
                                toSend.put("action", "removeMarker");
                                toSend.put("nazwa", name_item);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            String address = preferences.getString("address","");
                            if(!address.equalsIgnoreCase("")) {
                                JSONTransmitter asyncTask = (JSONTransmitter) new JSONTransmitter(new JSONTransmitter.AsyncResponse() {
                                    @Override
                                    public void processFinish(String output) {
                                        System.out.println(output);
                                    }
                                }).execute(toSend, address);
                            } else {
                                Toast.makeText(FinishActivity.this, "Ustaw poprawny adres w ustawieniach aplikacji", Toast.LENGTH_LONG).show();
                            }
                            showToast();
                            Intent i = new Intent(getApplicationContext(), ListActivity.class);
                            startActivity(i);
                        }
                    });
                    alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showToast2();
                        }
                    });

                    alert.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FinishActivity.this);
                    builder.setTitle(R.string.no_internet);
                    builder.setMessage(R.string.no_internet_information_delete_markers);
                    builder.setPositiveButton(R.string.ok, null);
                    builder.show();
                }
            }
        });

    }
    public void showToast(){
        Toast.makeText(this, R.string.delete_marker, Toast.LENGTH_SHORT).show();
    }
    public void showToast2(){
        Toast.makeText(this, R.string.delete_marker_error, Toast.LENGTH_SHORT).show();
    }
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Home_Screen.class);
        startActivity(intent);
    }
}
