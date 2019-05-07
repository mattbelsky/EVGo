package com.example.evrouteplannerapp;

import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationSearchActivity extends AppCompatActivity {

    private static final int NUM_ADDRESSES = 100;
    private EditText mSearchEditText;
    private FloatingActionButton mSearchButton;
    private RecyclerView mListAddressesRecyclerView;
    private LocationResultsRecyclerAdapter mLocationResultsRecyclerAdapter;
    private List<Address> mAddresses;

    private View.OnClickListener searchButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            String locationString = mSearchEditText.getText().toString();
            mAddresses = getAddresses(locationString);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);

        mSearchEditText = findViewById(R.id.et_search_location);
        mSearchButton = findViewById(R.id.floatingActionButton);
        mSearchButton.setOnClickListener(searchButtonClickListener);
        mListAddressesRecyclerView = findViewById(R.id.rv_location_results);

        // Configuring the layout management for the RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mListAddressesRecyclerView.setLayoutManager(layoutManager);
        mListAddressesRecyclerView.setHasFixedSize(true);

        mLocationResultsRecyclerAdapter = new LocationResultsRecyclerAdapter(NUM_ADDRESSES);

    }

    private List<Address> getAddresses(String locationString) {

        int maxResults = 10; // See if this is an ideal number of results
        Geocoder geocoder = new Geocoder(this, Locale.US);
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocationName(locationString, maxResults);
        } catch (IOException e) {
            return null;
        }

        return addresses;
    }
}
