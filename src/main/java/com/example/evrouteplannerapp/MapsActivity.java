package com.example.evrouteplannerapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.widget.EditText;
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
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private Button mFindRouteButton;
    private EditText mOriginAddressEditText;
    private EditText mDestAddressEditText;

    private View.OnClickListener findRouteButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // logic for passing origin/destination coords to google location api and getting a route
            EditText etOrigin = findViewById(R.id.et_origin);
            EditText etDestination = findViewById(R.id.et_destination);
            String strOrigin = etOrigin.getText().toString();
            String strDestination = etDestination.getText().toString();

            if (strOrigin == null || strDestination == null) {
                StringBuilder messageBuilder = new StringBuilder();
                if (strOrigin == null)
                    messageBuilder.append("Origin is empty. ");
                if (strDestination == null)
                    messageBuilder.append("Destination is empty.");
                String message = messageBuilder.toString();
                Toast.makeText(MapsActivity.this, message, Toast.LENGTH_SHORT).show();
                return;
            }

            LatLng origin = getCoords(strOrigin);
            LatLng destination = getCoords(strDestination);
            URL url = ProxyApiUtil.buildUrl(origin, destination);
            new QueryTask().execute(url);
        }
    };

    private View.OnFocusChangeListener editTextFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                EditText etView = (EditText) v;
                etView.setHint("");
                etView.setTextColor(Color.BLACK);
            }
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

        mFindRouteButton = findViewById(R.id.b_find_route);
        mFindRouteButton.setOnClickListener(findRouteButtonClickListener);

        mOriginAddressEditText = findViewById(R.id.et_origin);
        mOriginAddressEditText.setOnFocusChangeListener(editTextFocusChangeListener);

        mDestAddressEditText = findViewById(R.id.et_destination);
        mDestAddressEditText.setOnFocusChangeListener(editTextFocusChangeListener);
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
        double[] lastKnownLocation = getLastKnownLocation();
        enableMyLocation(mMap);
        LatLng location = new LatLng(lastKnownLocation[0], lastKnownLocation[1]);
        mMap.addMarker(new MarkerOptions().position(location));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
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

    private double[] getLastKnownLocation() {
        final double[] latLng = new double[2];

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            latLng[0] = location.getLatitude();
                            latLng[1] = location.getLongitude();
                        }
                    }
                });
        return latLng;
    }

    private LatLng getCoords(String locationName) {
        int maxResults = 5; // decide how to handle results later
        Geocoder geocoder = new Geocoder(this, Locale.US);
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocationName(locationName, maxResults);
        } catch (IOException e) {
            return null;
        }

        Address address = addresses.get(0); // getting first result only for now -- will change later
        double lat = address.getLatitude();
        double lng = address.getLongitude();

        return new LatLng(lat, lng);
    }
}
