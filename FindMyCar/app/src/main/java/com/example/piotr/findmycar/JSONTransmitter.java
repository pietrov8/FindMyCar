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
public class JSONTransmitter extends AsyncTask<Object, JSONObject, String> {

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;

    public JSONTransmitter(AsyncResponse delegate){
        this.delegate = delegate;
    };
    @Override
    protected String doInBackground(Object... data) {
        JSONObject json = (JSONObject) data[0];
        String url = (String) data[1];
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000);

        String jsonResponse = null;
        HttpPost post = new HttpPost(url);

        try {
            StringEntity se = new StringEntity("json="+json.toString());
            post.addHeader("content-type", "application/x-www-form-urlencoded");
            post.setEntity(se);
            HttpResponse response;
            response = client.execute(post);
            String resFromServer = org.apache.http.util.EntityUtils.toString(response.getEntity());
            jsonResponse = resFromServer;

        } catch (Exception e) { e.printStackTrace();}

        return jsonResponse;
    }

    @Override
    protected void onPostExecute(String jsonResponse) {
        delegate.processFinish(jsonResponse);
    }

}
