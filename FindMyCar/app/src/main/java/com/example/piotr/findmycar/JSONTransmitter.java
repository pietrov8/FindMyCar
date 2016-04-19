package com.example.piotr.findmycar;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

/**
 * Created by piotr on 2016-04-18.
 */
public class JSONTransmitter extends AsyncTask<JSONObject, JSONObject, JSONObject> {

    String url = "http://decleor.com.pl/fmc/main.php";

    @Override
    protected JSONObject doInBackground(JSONObject... data) {
        JSONObject json = data[0];
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000);

        JSONObject jsonResponse = null;
        HttpPost post = new HttpPost(url);
        try {
            StringEntity se = new StringEntity("json="+json.toString());
            post.addHeader("content-type", "application/x-www-form-urlencoded");
            post.setEntity(se);
            HttpResponse response = client.execute(post);

        } catch (Exception e) { e.printStackTrace();}

        return jsonResponse;
    }
}
