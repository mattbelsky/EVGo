package com.example.evrouteplannerapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.URL;

public class QueryTask extends AsyncTask<URL, Void, String> {

    @Override
    protected String doInBackground(URL... urls) {
        String response = null;
        try {
            response = ProxyApiUtil.getResponseFromHttpUrl(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        if (s == null) {
            String message = "No value returned.";
            Log.e(LoggerConstants.ERROR, message);
        } else
            Log.i(LoggerConstants.INFO, s);
    }
}
