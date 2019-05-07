package com.example.evrouteplannerapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
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
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mCurrentLocation;
    private TextView mOriginTextView;
    private TextView mDestinationTextView;
    private Button mFindRouteButton;
    private LatLng[] mCoords; // mCoords[0] is origin, [1] is destination

    private View.OnClickListener tvClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            Intent intent = new Intent(MapsActivity.this, LocationSearchActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener findRouteButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            // logic for passing origin/destination coords to google location api and getting a route
            LatLng coordsOrigin = mCoords[0];
            LatLng coordsDest = mCoords[1];

            if (coordsOrigin == null || coordsDest == null) {
                StringBuilder messageBuilder = new StringBuilder();
                if (coordsOrigin == null)
                    messageBuilder.append("Origin is empty. ");
                if (coordsDest == null)
                    messageBuilder.append("Destination is empty.");
                String message = messageBuilder.toString();
                Toast.makeText(MapsActivity.this, message, Toast.LENGTH_SHORT).show();
                return;
            }

            URL url = ProxyApiUtil.buildUrl(coordsOrigin, coordsDest);
            new QueryTask().execute(url);
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

        mOriginTextView = findViewById(R.id.tv_origin);
        mDestinationTextView = findViewById(R.id.tv_destination);
        mOriginTextView.setOnClickListener(tvClickListener);
        mDestinationTextView.setOnClickListener(tvClickListener);

        mFindRouteButton = findViewById(R.id.b_find_route);
        mFindRouteButton.setOnClickListener(findRouteButtonClickListener);

        mCoords = new LatLng[2];
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

    private ArrayList<LatLng> getCoords(String locationName) {

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
