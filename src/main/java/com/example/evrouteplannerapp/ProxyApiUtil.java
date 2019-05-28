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

    /* Since the emulator is running behind a virtual router, 10.0.2.2 is a special address that is 
     * required to reference the address 127.0.0.1 on the development machine. */
    private final static String BASE_URL_ROUTE_PLANNER = "http://10.0.2.2:8080/routeplanner/go";
    private final static String BASE_URL_ROUTE_POLYLINE = "http://10.0.2.2:8080/routeplanner/polyline";
    private final static String START_LAT_PARAM_QUERY = "start_lat";
    private final static String START_LNG_PARAM_QUERY = "start_lng";
    private final static String END_LAT_PARAM_QUERY = "end_lat";
    private final static String END_LNG_PARAM_QUERY = "end_lng";
    private final static String DISTANCE_QUERY = "distance";
    private final static String DISTANCE_UNIT_QUERY = "distance_unit";
    private final static String LEVEL_ID_QUERY = "level_id";
    private final static String MAX_RESULTS_QUERY = "max_results";

    /* Will be set in preferences. For now set as constants. Must be strings as appendQueryParameters() 
     * only accepts string parameters. */
    private final static String DISTANCE = "1";
    private final static String DISTANCE_UNIT = "2";
    private final static String LEVEL_ID = "3";
    private final static String MAX_RESULTS = "3";

    /**
     * Builds a URL intended to be used to query the proxy API.
     * @param originCoords
     * @param destinationCoords
     * @return
     */
    public static URL buildUrlRoutePlanner(LatLng originCoords, LatLng destinationCoords) {

        String startLat = String.valueOf(originCoords.latitude);
        String startLng = String.valueOf(originCoords.longitude);
        String endLat = String.valueOf(destinationCoords.latitude);
        String endLng = String.valueOf(destinationCoords.longitude);

        Uri builtUri = Uri.parse(BASE_URL_ROUTE_PLANNER).buildUpon()
                .appendQueryParameter(START_LAT_PARAM_QUERY, startLat)
                .appendQueryParameter(START_LNG_PARAM_QUERY, startLng)
                .appendQueryParameter(END_LAT_PARAM_QUERY, endLat)
                .appendQueryParameter(END_LNG_PARAM_QUERY, endLng)
                .appendQueryParameter(DISTANCE_QUERY, DISTANCE)
                .appendQueryParameter(DISTANCE_UNIT_QUERY, DISTANCE_UNIT)
                .appendQueryParameter(LEVEL_ID_QUERY, LEVEL_ID)
                .appendQueryParameter(MAX_RESULTS_QUERY, MAX_RESULTS)
                .build();

        return uriToUrl(builtUri);
    }

    public static URL buildUrlRoutePolyline(LatLng originCoords, LatLng destinationCoords) {

        String startLat = String.valueOf(originCoords.latitude);
        String startLng = String.valueOf(originCoords.longitude);
        String endLat = String.valueOf(destinationCoords.latitude);
        String endLng = String.valueOf(destinationCoords.longitude);

        Uri builtUri = Uri.parse(BASE_URL_ROUTE_POLYLINE).buildUpon()
                .appendQueryParameter(START_LAT_PARAM_QUERY, startLat)
                .appendQueryParameter(START_LNG_PARAM_QUERY, startLng)
                .appendQueryParameter(END_LAT_PARAM_QUERY, endLat)
                .appendQueryParameter(END_LNG_PARAM_QUERY, endLng)
                .build();

        return uriToUrl(builtUri);
    }

    /**
     * Queries the proxy API with the specified URL and gets a string response.
     * @param url
     * @return
     * @throws IOException
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
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

    private static URL uriToUrl(Uri uri) {

        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
