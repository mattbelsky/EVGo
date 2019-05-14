package com.example.evrouteplannerapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static com.example.evrouteplannerapp.Constants.DESTINATION_COORDS;
import static com.example.evrouteplannerapp.Constants.DESTINATION_TEXT;
import static com.example.evrouteplannerapp.Constants.ORIGIN_COORDS;
import static com.example.evrouteplannerapp.Constants.ORIGIN_TEXT;
import static com.example.evrouteplannerapp.Constants.TEXTVIEW_ID;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mCurrentLocation;
    private TextView mOriginTextView;
    private TextView mDestinationTextView;
    private Button mClearButton;
    private Button mFindRouteButton;
    private double[] mCoordsOrigin;
    private double[] mCoordsDestination;
    private ArrayList<LatLng> mPolylineCoords;
    private boolean mClearedTvsFlag = false;

    private View.OnClickListener tvClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            String tvOriginText = mOriginTextView.getText().toString();
            String tvDestinationText = mDestinationTextView.getText().toString();

            Intent intent = new Intent(MapsActivity.this, LocationSearchActivity.class);
            intent.putExtra(TEXTVIEW_ID, v.getId());
            intent.putExtra(ORIGIN_TEXT, tvOriginText);
            intent.putExtra(DESTINATION_TEXT, tvDestinationText);

            if (mCoordsOrigin != null)
                intent.putExtra(ORIGIN_COORDS, mCoordsOrigin);
            if (mCoordsDestination != null)
                intent.putExtra(DESTINATION_COORDS, mCoordsDestination);

            startActivity(intent);
        }
    };

    private View.OnClickListener clearButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            mOriginTextView.setText(getText(R.string.tv_origin));
            mDestinationTextView.setText(getText(R.string.tv_destination));
            mClearedTvsFlag = true;
        }
    };

    private View.OnClickListener findRouteButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (mCoordsOrigin == null || mCoordsDestination == null || mClearedTvsFlag == true) {
                StringBuilder messageBuilder = new StringBuilder();
                if (mCoordsOrigin == null || mClearedTvsFlag == true)
                    messageBuilder.append("Origin is empty. ");
                if (mCoordsDestination == null || mClearedTvsFlag == true)
                    messageBuilder.append("Destination is empty.");
                String message = messageBuilder.toString();
                Toast.makeText(MapsActivity.this, message, Toast.LENGTH_SHORT).show();
                return;
            }

            LatLng originLatLng = new LatLng(mCoordsOrigin[0], mCoordsOrigin[1]);
            LatLng destinationLatLng = new LatLng(mCoordsDestination[0], mCoordsDestination[1]);
            URL url = ProxyApiUtil.buildUrlRoutePolyline(originLatLng, destinationLatLng);
            AsyncTask<URL, Void, String> queryResult = new QueryTask().execute(url);
            String polyline = null;
            try {
                polyline = queryResult.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            ArrayList<LatLng> coords = getCoordsFromPolyline(polyline);
            PolylineOptions polylineOptions = new PolylineOptions().clickable(false);

            for (LatLng c : coords) {
                polylineOptions.add(c);
            }

            mMap.addPolyline(polylineOptions);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Intent activityOriginIntent = getIntent();

        mOriginTextView = findViewById(R.id.tv_origin);
        mDestinationTextView = findViewById(R.id.tv_destination);
        mOriginTextView.setOnClickListener(tvClickListener);
        mDestinationTextView.setOnClickListener(tvClickListener);

        // Updates the fields with any text they may have held when tvClickListener's onClick()
        // was called, as well as any new data passed from the previous activity.
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
        mClearButton.setOnClickListener(clearButtonClickListener);
        mFindRouteButton = findViewById(R.id.b_find_route);
        mFindRouteButton.setOnClickListener(findRouteButtonClickListener);
        mPolylineCoords = null;
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
        enableMyLocation(mMap);
        getLastKnownLocation();

        if (mCurrentLocation != null) {
            LatLng currentLocCoords = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(currentLocCoords));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocCoords));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation(mMap);
            }
        }
    }

    private void enableMyLocation(GoogleMap map) {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    private void getLastKnownLocation() {

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {

                    @Override
                    public void onSuccess(Location location) {
                        if (location != null)
                            mCurrentLocation = location;
                        else
                            Toast.makeText(MapsActivity.this, "Please ensure location is enabled.",
                                    Toast.LENGTH_SHORT).show();
                    }
                });
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

    private ArrayList<LatLng> getCoordsFromName(String locationName) {

        int maxResults = 10; // See if this is an ideal number of results
        Geocoder geocoder = new Geocoder(this, Locale.US);
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocationName(locationName, maxResults);
        } catch (IOException e) {
            return null;
        }

        // The list to return
        ArrayList<LatLng> coordsList = new ArrayList<>();
        for (Address a : addresses) {
            LatLng coords = new LatLng(a.getLatitude(), a.getLongitude());
            coordsList.add(coords);
        }

        return coordsList;
    }
}
