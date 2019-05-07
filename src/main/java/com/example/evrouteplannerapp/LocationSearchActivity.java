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

            // Creating the adapter and assigning it to the RecyclerView in the click listener ensures
            // that the user has finished entering his search text and clicked the search button so
            // that the list of addresses is not empty.
            mLocationResultsRecyclerAdapter = new LocationResultsRecyclerAdapter(mAddresses);
            mListAddressesRecyclerView.setAdapter(mLocationResultsRecyclerAdapter);
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
    }

    private List<Address> getAddresses(String locationString) {

        int maxResults = 5;
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
