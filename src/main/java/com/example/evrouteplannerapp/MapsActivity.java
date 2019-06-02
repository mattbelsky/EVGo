package com.example.evrouteplannerapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.evrouteplannerapp.models.ChargingSite;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.evrouteplannerapp.Constants.DESTINATION_COORDS;
import static com.example.evrouteplannerapp.Constants.DESTINATION_TEXT;
import static com.example.evrouteplannerapp.Constants.ORIGIN_COORDS;
import static com.example.evrouteplannerapp.Constants.ORIGIN_TEXT;
import static com.example.evrouteplannerapp.Constants.TEXTVIEW_ID;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private Location mCurrentLocation;
    private Toolbar mToolbar;
    private TextView mOriginTextView;
    private TextView mDestinationTextView;
    private Button mClearButton;
    private Button mFindRouteButton;
    private ProgressBar mProgressSpinner;
    private double[] mCoordsOrigin;
    private double[] mCoordsDestination;
    private boolean mClearedTvsFlag = false;
    private String mDistance;
    private String mDistanceUnit;

    private View.OnClickListener tvClickListener = v -> {

        Intent intent = buildIntent(LocationSearchActivity.class, v);
        startActivity(intent);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);

        // Set up and get values from shared preferences. Sets defaults if nothing selected on preferences screen.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        mDistance = sharedPreferences.getString(getString(R.string.pref_distance_key), "3");
        mDistanceUnit = sharedPreferences.getString(
                getString(R.string.pref_distance_unit_key),
                getString(R.string.pref_distance_unit_miles)
        );

        Intent activityOriginIntent = getIntent();

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();

        mOriginTextView = findViewById(R.id.tv_origin);
        mDestinationTextView = findViewById(R.id.tv_destination);
        mOriginTextView.setOnClickListener(tvClickListener);
        mDestinationTextView.setOnClickListener(tvClickListener);

        // Updates the fields with any text they may have held when tvClickListener's onClick() was
        // called, as well as any new data passed from the previous activity.
        if (activityOriginIntent.hasExtra(ORIGIN_TEXT)) {
            String originText = activityOriginIntent.getStringExtra(ORIGIN_TEXT);
            mOriginTextView.setText(originText);
        }
        if (activityOriginIntent.hasExtra(DESTINATION_TEXT)) {
            String destinationText = activityOriginIntent.getStringExtra(DESTINATION_TEXT);
            mDestinationTextView.setText(destinationText);
        }
        if (activityOriginIntent.hasExtra(ORIGIN_COORDS))
            mCoordsOrigin = activityOriginIntent.getDoubleArrayExtra(ORIGIN_COORDS);
        if (activityOriginIntent.hasExtra(DESTINATION_COORDS))
            mCoordsDestination = activityOriginIntent.getDoubleArrayExtra(DESTINATION_COORDS);

        mClearButton = findViewById(R.id.b_clear);
        mClearButton.setOnClickListener(v -> {

            mOriginTextView.setText(getText(R.string.tv_origin));
            mDestinationTextView.setText(getText(R.string.tv_destination));
            mClearedTvsFlag = true;
        });

        mProgressSpinner = findViewById(R.id.progress_spinner);

        mFindRouteButton = findViewById(R.id.b_find_route);
        mFindRouteButton.setOnClickListener(v -> {

            if (mCoordsOrigin == null || mCoordsDestination == null || mClearedTvsFlag == true) {
                StringBuilder messageBuilder = new StringBuilder();
                if (mCoordsOrigin == null || mClearedTvsFlag == true)
                    messageBuilder.append("Origin is empty. ");
                if (mCoordsDestination == null || mClearedTvsFlag == true)
                    messageBuilder.append("Destination is empty.");
                String message = messageBuilder.toString();
                Toast.makeText(MapsActivity.this, message, Toast.LENGTH_SHORT).show();
                Log.w(TAG, "\"Find route\" button clicked with empty origin or destination TextViews.");
                return;
            }

            mProgressSpinner.setVisibility(View.VISIBLE);
            Log.i(TAG, "Progress spinner should be visible.");

            LatLng origin = new LatLng(mCoordsOrigin[0], mCoordsOrigin[1]);
            LatLng destination = new LatLng(mCoordsDestination[0], mCoordsDestination[1]);
            showPolyline(origin, destination);
            showChargingSites(origin, destination);

            mProgressSpinner.setVisibility(View.INVISIBLE);
            Log.i(TAG, "Progress spinner should be gone.");
        });
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (mCurrentLocation != null) {
            LatLng currentLocCoords = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(currentLocCoords));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocCoords));
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setOnMarkerClickListener(marker -> {
                marker.showInfoWindow();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15)); // Why isn't it zooming??
                return true;
            });
        }
    }

    /**
     * Creates the options menu in the toolbar and sets the text to display in it.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        mToolbar.setTitle(R.string.app_name);
        return true;
    }

    /**
     * Opens the settings activity if its icon is selected (currently the only option).
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_preferences) {
            Intent intent = buildIntent(SettingsActivity.class, null);
            startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * Updates the values of mDistance and mDistanceUnit if their corresponding preferences were changed.
     * @param sharedPreferences
     * @param key -- the key of the shared preference item
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (key.equals(getString(R.string.pref_distance_key))) {
            mDistance = sharedPreferences.getString(getString(R.string.pref_distance_key), "3");
        }
        if (key.equals(getString(R.string.pref_distance_unit_key)))
            mDistanceUnit = sharedPreferences.getString(
                    getString(R.string.pref_distance_unit_key),
                    getString(R.string.pref_distance_unit_miles)
            );
    }

    /**
     * Helper method that builds an intent.
     * @param cls -- the activity to open
     * @param v -- the view with whose interaction triggered this method, if necessary
     * @return a new intent
     */
    private Intent buildIntent(Class cls, View v) {

        String tvOriginText = mOriginTextView.getText().toString();
        String tvDestinationText = mDestinationTextView.getText().toString();

        Intent intent = new Intent(MapsActivity.this, cls);
        intent.putExtra(ORIGIN_TEXT, tvOriginText);
        intent.putExtra(DESTINATION_TEXT, tvDestinationText);

        if (v != null)
            intent.putExtra(TEXTVIEW_ID, v.getId());
        if (mCoordsOrigin != null)
            intent.putExtra(ORIGIN_COORDS, mCoordsOrigin);
        if (mCoordsDestination != null)
            intent.putExtra(DESTINATION_COORDS, mCoordsDestination);
        return intent;
    }

    /**
     * Retrieves and displays the route polyline on the map.
     * @param origin
     * @param destination
     */
    private void showPolyline(LatLng origin, LatLng destination) {

        URL url = ProxyApiUtil.buildUrlRoutePolyline(origin, destination);
        AsyncTask<URL, Void, String> future = new QueryTask().execute(url);
        String polyline;

        try {
            polyline = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return;
        }

        // The polyline may be null if the proxy API is down.
        if (polyline == null) {
            Toast.makeText(MapsActivity.this, "No results for query.", Toast.LENGTH_SHORT)
                    .show();
            Log.w(TAG, "Polyline is null. Proxy API may be unavailable.");
            return;
        }

        ArrayList<LatLng> coords = getCoordsFromPolyline(polyline);
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        PolylineOptions polylineOptions = new PolylineOptions()
                .clickable(false)
                .color(Color.BLUE)
                .startCap(new RoundCap())
                .endCap(new RoundCap());

        for (LatLng c : coords) {
            boundsBuilder.include(c);
            polylineOptions.add(c);
        }

        LatLngBounds bounds = boundsBuilder.build();
        mMap.addPolyline(polylineOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 80));
    }

    /**
     * Retrieves and displays the list of charging sites associated with route as markers on the map.
     * @param origin
     * @param destination
     */
    private void showChargingSites(LatLng origin, LatLng destination) {

        URL url = ProxyApiUtil.buildUrlRoutePlanner(origin, destination, mDistance, mDistanceUnit);
        AsyncTask<URL, Void, String> future = new QueryTask().execute(url);
        String result;
        JSONArray json; // Is this necessary?

        try {
            result = future.get();
            json = new JSONArray(result);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "No valid results.", Toast.LENGTH_SHORT).show();
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        ArrayList<ChargingSite> sites = new ArrayList<>();

        for (int i = 0; i < json.length(); i++) {
            ChargingSite site = null;
            try {
                site = mapper.readValue(json.get(i).toString(), ChargingSite.class);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            if (site != null) {
                sites.add(site);
                double lat = site.getAddressInfo().getLatitude();
                double lng = site.getAddressInfo().getLongitude();
                String title = site.getAddressInfo().getTitle();
                String snippet = site.getAddressInfo().getAddressLine1() + "\n" +
                        site.getAddressInfo().getTown() + ", " +
                        site.getAddressInfo().getStateOrProvince() + " " +
                        site.getAddressInfo().getPostcode();
                MarkerOptions options = new MarkerOptions();
                options.position(new LatLng(lat, lng));
                options.title(title).snippet(snippet);
                mMap.addMarker(options);
            }
        }
    }

    private ArrayList<LatLng> getCoordsFromPolyline(String polyline) {

        // Replaces every case of "\\\\" by removing 2 slashes, thereby avoiding an IndexOutOfBoundsException.
        polyline = polyline.replaceAll("\\\\\\\\", "\\\\");
        ArrayList<LatLng> coords = new ArrayList();
        int index = 0, len = polyline.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = polyline.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = polyline.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng point = new LatLng((((double) lat / 1E5)), ((double) lng / 1E5));
            coords.add(point);
        }

        return coords;
    }
}
