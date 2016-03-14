package com.example.piotr.findmycar;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;

public class EditActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Bundle bundle_list = getIntent().getExtras();
        String name_item = bundle_list.getString("name");

        EditText marker_title = (EditText)findViewById(R.id.marker_title_edit);
        marker_title.setText(name_item);

    }
}
