package com.example.piotr.findmycar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class ListActivity extends Activity {
    ListView mainListView;
    ArrayList<String> listAdapter = new ArrayList<String>();
    MyCustomAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listAdapter.add("Znacznik 1");
        listAdapter.add("Znacznik 2");
        listAdapter.add("Znacznik 3");
        listAdapter.add("Znacznik 4");
        listAdapter.add("Znacznik 5");
        listAdapter.add("Znacznik 6");
        listAdapter.add("Znacznik 7");
        listAdapter.add("Znacznik 8");

        adapter = new MyCustomAdapter(listAdapter, this);

        final ListView mainListView = (ListView)findViewById(R.id.listView);
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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int titulo = (int) info.position;
        String name_item = (String) ((TextView) info.targetView
                .findViewById(R.id.list_item_string)).getText();
        View view = info.targetView;
        Integer item_number = (Integer) item.getItemId();
        switch (item_number) {
            case R.id.edit_btn:
                Bundle put = new Bundle();
                put.putString("name", name_item);
                Intent i = new Intent(getApplicationContext(), EditActivity.class);
                i.putExtras(put);
                startActivity(i);
                break;
            case R.id.delete_btn:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(R.string.delete_alert_title);
                alert.setMessage(R.string.delete_alert);
                alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listAdapter.remove(Integer.parseInt(String.valueOf(titulo)));
                        adapter.notifyDataSetChanged();
                        showToast();
                    }
                });
                alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showToast2();
                    }
                });

                alert.show();

                break;
            case R.id.get_marker:
                Bundle put_to_camera = new Bundle();
                put_to_camera.putString("name", name_item);
                Intent ii = new Intent(getApplicationContext(), CameraViewActivity.class);
                ii.putExtras(put_to_camera);
                startActivity(ii);
                break;
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
