package com.example.evrouteplannerapp;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class ProxyApiUtil {

    private final static String BASE_URL = "<url>";
    private final static String START_LAT_PARAM_QUERY = "start_lat";
    private final static String START_LNG_PARAM_QUERY = "start_lng";
    private final static String END_LAT_PARAM_QUERY = "end_lat";
    private final static String END_LNG_PARAM_QUERY = "end_lng";
    private final static String DISTANCE_QUERY = "distance";
    private final static String DISTANCE_UNIT_QUERY = "distance_unit";
    private final static String LEVEL_ID_QUERY = "level_id";
    private final static String MAX_RESULTS_QUERY = "max_results";

    // Will be set in preferences. For now set as constants. Must be strings as appendQueryParameters()
    // only accepts string parameters.
    private final static String DISTANCE = "1";
    private final static String DISTANCE_UNIT = "2";
    private final static String LEVEL_ID = "3";
    private final static String MAX_RESULTS = "3";

    public static URL buildUrl(LatLng originCoords, LatLng destinationCoords) {
        String startLat = String.valueOf(originCoords.latitude);
        String startLng = String.valueOf(originCoords.longitude);
        String endLat = String.valueOf(destinationCoords.latitude);
        String endLng = String.valueOf(destinationCoords.longitude);
        URL url = null;

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(START_LAT_PARAM_QUERY, startLat)
                .appendQueryParameter(START_LNG_PARAM_QUERY, startLng)
                .appendQueryParameter(END_LAT_PARAM_QUERY, endLat)
                .appendQueryParameter(END_LNG_PARAM_QUERY, endLng)
                .appendQueryParameter(DISTANCE_QUERY, DISTANCE)
                .appendQueryParameter(DISTANCE_UNIT_QUERY, DISTANCE_UNIT)
                .appendQueryParameter(LEVEL_ID_QUERY, LEVEL_ID)
                .appendQueryParameter(MAX_RESULTS_QUERY, MAX_RESULTS)
                .build();
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput)
                return scanner.next();
            else
                return null;
        } finally {
            urlConnection.disconnect();
        }
    }
}
