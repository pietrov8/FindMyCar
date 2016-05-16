package com.example.piotr.findmycar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ListActivity extends Activity {
    ListView mainListView;
    ArrayList<String> listAdapter = new ArrayList<String>();
    MyCustomAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        JSONObject toSend = new JSONObject();
        try {
            toSend.put("action", "getAllMarkers");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String address = preferences.getString("address","");
        if(!address.equalsIgnoreCase(""))
        {
            JSONTransmitter asyncTask = (JSONTransmitter) new JSONTransmitter(new JSONTransmitter.AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    try {
                        JSONArray pages = new JSONArray(output);
                        for (int i = 0; i < pages.length(); ++i) {
                            JSONObject rec = pages.getJSONObject(i);
                            String name_task = rec.getString("nazwa");
                            listAdapter.add(name_task);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute(toSend,address);
        } else {
            Toast.makeText(this, "Ustaw poprawny adres w ustawieniach aplikacji", Toast.LENGTH_LONG).show();
        }

        adapter = new MyCustomAdapter(listAdapter, this);

        final ListView mainListView = (ListView) findViewById(R.id.listView);
        mainListView.setAdapter(adapter);

        registerForContextMenu(mainListView);

        //Obsłużenie wyświetlenia menu
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView text = (TextView) view.findViewById(R.id.list_item_string);

                registerForContextMenu(text);
                view.showContextMenu();
            }
        });
    }
    // Utworzenie menu do listy znaczników
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.listView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            String titulo = (String) ((TextView) info.targetView
                    .findViewById(R.id.list_item_string)).getText();
            menu.setHeaderTitle(titulo);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_list_marker, menu);
        }
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
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int titulo = (int) info.position;
        final String name_item = (String) ((TextView) info.targetView
                .findViewById(R.id.list_item_string)).getText().toString();
        View view = info.targetView;
        Integer item_number = (Integer) item.getItemId();
        final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.trash);
        final MediaPlayer mp2 = MediaPlayer.create(getApplicationContext(), R.raw.menu_select_1);

        switch (item_number) {
            case R.id.edit_btn:
                Bundle put = new Bundle();
                put.putString("name", name_item);
                Intent i = new Intent(getApplicationContext(), EditActivity.class);
                i.putExtras(put);
                if (isConnected()) {
                    finish();
                    startActivity(i);
                    mp2.start();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
                    builder.setTitle(R.string.no_internet);
                    builder.setMessage(R.string.no_internet_information_edit_markers);
                    builder.setPositiveButton(R.string.ok, null);
                    builder.show();
                }
                break;
            case R.id.delete_btn:
                if (isConnected()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle(R.string.delete_alert_title);
                    alert.setMessage(R.string.delete_alert);
                    alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listAdapter.remove(Integer.parseInt(String.valueOf(titulo)));
                            adapter.notifyDataSetChanged();
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
                                Toast.makeText(ListActivity.this, "Ustaw poprawny adres w ustawieniach aplikacji", Toast.LENGTH_LONG).show();
                            }
                            showToast();
                            mp.start();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
                    builder.setTitle(R.string.no_internet);
                    builder.setMessage(R.string.no_internet_information_delete_markers);
                    builder.setPositiveButton(R.string.ok, null);
                    builder.show();
                }

                break;
            case R.id.get_marker:
                if (isConnected()) {
                    JSONObject toSend = new JSONObject();
                    try {
                        toSend.put("action", "getAllMarkers");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    String address = preferences.getString("address","");
                    if(!address.equalsIgnoreCase("")) {
                        JSONTransmitter asyncTask = (JSONTransmitter) new JSONTransmitter(new JSONTransmitter.AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                                try {
                                    Bundle put_to_camera = new Bundle();
                                    JSONArray pages = new JSONArray(output);
                                    for (int i = 0; i < pages.length(); ++i) {
                                        JSONObject rec = pages.getJSONObject(i);
                                        String name_task = rec.getString("nazwa");
                                        String latitude = rec.getString("latitude");
                                        String longituide = rec.getString("longitude");
                                        if (name_task.equals(name_item)) {
                                            put_to_camera.putString("lat", latitude);
                                            put_to_camera.putString("long", longituide);
                                        }
                                    }
                                    put_to_camera.putString("name", name_item);
                                    Intent ii = new Intent(getApplicationContext(), CameraViewActivity.class);
                                    ii.putExtras(put_to_camera);
                                    finish();
                                    startActivity(ii);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).execute(toSend,address);
                    } else {
                        Toast.makeText(ListActivity.this, "Ustaw poprawny adres w ustawieniach aplikacji", Toast.LENGTH_LONG).show();
                    }

                    mp2.start();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
                    builder.setTitle(R.string.no_internet);
                    builder.setMessage(R.string.no_internet_information_choose_markers);
                    builder.setPositiveButton(R.string.ok, null);
                    builder.show();
                }
                break;
            case R.id.get_marker_info:
                if (isConnected()) {
                    JSONObject toSend = new JSONObject();
                    try {
                        toSend.put("action", "getAllMarkers");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    String address = preferences.getString("address","");
                    if(!address.equalsIgnoreCase("")) {
                        JSONTransmitter asyncTask = (JSONTransmitter) new JSONTransmitter(new JSONTransmitter.AsyncResponse() {
                            @Override
                            public void processFinish(String output) {
                                try {
                                    Bundle put2 = new Bundle();
                                    JSONArray pages = new JSONArray(output);
                                    for (int i = 0; i < pages.length(); ++i) {
                                        JSONObject rec = pages.getJSONObject(i);
                                        String name_task = rec.getString("nazwa");
                                        String latitude = rec.getString("latitude");
                                        String longituide = rec.getString("longitude");
                                        if (name_task.equals(name_item)) {
                                            put2.putString("lat", latitude);
                                            put2.putString("long", longituide);
                                        }
                                    }
                                    put2.putString("name", name_item);
                                    Intent iii = new Intent(getApplicationContext(), MarkerInfo.class);
                                    iii.putExtras(put2);
                                    startActivity(iii);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).execute(toSend,address);
                    } else {
                        Toast.makeText(ListActivity.this, "Ustaw poprawny adres w ustawieniach aplikacji", Toast.LENGTH_LONG).show();
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
                    builder.setTitle(R.string.no_internet);
                    builder.setMessage(R.string.no_internet_information_info_markers);
                    builder.setPositiveButton(R.string.ok, null);
                    builder.show();
                }
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }
    public void showToast(){
        Toast.makeText(this, R.string.delete_marker, Toast.LENGTH_SHORT).show();
    }
    public void showToast2(){
        Toast.makeText(this, R.string.delete_marker_error, Toast.LENGTH_SHORT).show();
    }
}
