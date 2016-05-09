package com.example.piotr.findmycar;

import android.os.AsyncTask;
import android.os.StrictMode;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by piotr on 2016-05-09.
 */
public class CheckConnectionTask extends AsyncTask<String, Void, String> {



    public interface AsyncResponse {
        void processFinish(String result);
    }

    public AsyncResponse delegate = null;

    public CheckConnectionTask(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... params) {
        String data_adres = params[0];
        System.out.println(data_adres);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        HttpURLConnection urlc = null;
        String ok = "";
        try {
            urlc = (HttpURLConnection) (new URL(data_adres).openConnection());
            urlc.setRequestProperty("User-Agent", "Test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1000);
            urlc.connect();
            Integer res = urlc.getResponseCode();

            if (res == 200) {
                ok = "1";
            } else {
                ok = "0";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok;
    }

    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }
}
