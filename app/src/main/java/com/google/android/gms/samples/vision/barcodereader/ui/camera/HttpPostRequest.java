package com.google.android.gms.samples.vision.barcodereader.ui.camera;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Sreekanth on 20-02-2018.
 */

public class HttpPostRequest extends AsyncTask<String, Void, String> {

    public int responseCode;
    protected void onPreExecute() {
    }

    protected String doInBackground(String... params) {
        String stringUrl = params[0];
        try {

            URL url = new URL(stringUrl); // here is your URL path

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            responseCode = conn.getResponseCode();
            System.out.println(responseCode + "12123333333333333333333333333333333333333333333333333333333333333333333");
            return "succeeded";
        } catch (Exception e) {
            return new String("Exception: " + e.getMessage());
        }

    }

    @Override
    protected void onPostExecute(String result) {
        //Toast.makeText(getApplicationContext(), result,
        //Toast.LENGTH_LONG).show();
    }
}
