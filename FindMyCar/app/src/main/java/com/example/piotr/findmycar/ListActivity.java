package com.example.piotr.findmycar;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class ListActivity extends Activity {
    private ListView mainListView;
    private ArrayAdapter<String> listAdapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ArrayList<String> list = new ArrayList<String>();
        list.add("Znacznik 1");
        list.add("Znacznik 2");
        list.add("Znacznik 3");
        list.add("Znacznik 4");
        list.add("Znacznik 5");
        list.add("Znacznik 6");
        list.add("Znacznik 7");
        list.add("Znacznik 8");
        list.add("Znacznik 9");
        list.add("Znacznik 10");
        list.add("Znacznik 11");
        list.add("Znacznik 12");
        list.add("Znacznik 13");
        list.add("Znacznik 14");
        list.add("Znacznik 15");
        list.add("Znacznik 16");
        list.add("Znacznik 17");
        list.add("Znacznik 18");
        list.add("Znacznik 19");
        list.add("Znacznik 20");
        list.add("Znacznik 21");
        list.add("Znacznik 22");

        //instantiate custom adapter
        MyCustomAdapter adapter = new MyCustomAdapter(list, this);

        //handle listview and assign adapter
        ListView lView = (ListView)findViewById(R.id.listView);
        lView.setAdapter(adapter);
//
//        lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                TableRow mView = (TableRow) view.findViewById(R.id.hidden_view);
//
//                switch(mView.getVisibility()){
//                    case View.VISIBLE:
//                        mView.setVisibility(View.GONE);
//                        break;
//                    case View.GONE:
//                        mView.setVisibility(View.VISIBLE);
//                        break;
//                }
//            }
//        });
    }

}
