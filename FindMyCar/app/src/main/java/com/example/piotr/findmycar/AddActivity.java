package com.example.piotr.findmycar;

import android.app.Activity;
import android.os.Bundle;

public class AddActivity extends Activity {

    @Override
    public void setTitle(CharSequence title) {
        setTitle(R.string.Activity_add_title);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
    }

}
