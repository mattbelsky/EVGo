package com.example.evrouteplannerapp;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationSearchActivity extends AppCompatActivity
        implements LocationResultsRecyclerAdapter.AddressListItemClickListener {

    // The name of the integer attached to the intent that launched this activity
    private static final String TEXTVIEW_ID = "TEXTVIEW_ID";

    private EditText mSearchEditText;
    private FloatingActionButton mSearchButton;
    private RecyclerView mListAddressesRecyclerView;
    private LocationResultsRecyclerAdapter mLocationResultsRecyclerAdapter;
    private List<Address> mAddresses;
    private int mTvId; // the id of the TextView that was clicked to launch this activity

    private View.OnClickListener searchButtonClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            String locationString = mSearchEditText.getText().toString();
            mAddresses = getAddresses(locationString);

            // Creating the adapter and assigning it to the RecyclerView in the click listener ensures
            // that the user has finished entering his search text and clicked the search button so
            // that the list of addresses is not empty.
            mLocationResultsRecyclerAdapter = new LocationResultsRecyclerAdapter(mAddresses,
                    LocationSearchActivity.this);
            mListAddressesRecyclerView.setAdapter(mLocationResultsRecyclerAdapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);

        Intent activityOriginIntent = getIntent();
        if (activityOriginIntent.hasExtra(TEXTVIEW_ID))
            mTvId = activityOriginIntent.getIntExtra(TEXTVIEW_ID, -1);

        mSearchEditText = findViewById(R.id.et_search_location);
        mSearchButton = findViewById(R.id.floatingActionButton);
        mSearchButton.setOnClickListener(searchButtonClickListener);
        mListAddressesRecyclerView = findViewById(R.id.rv_location_results);

        // Configuring the layout management for the RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mListAddressesRecyclerView.setLayoutManager(layoutManager);
        mListAddressesRecyclerView.setHasFixedSize(true);
    }

    /**
     * Sends the user back to MapsActivity upon clicking one of the addresses listed in the RecyclerView.
     * This method is listed in this activity for the sake of better organization. While the logic
     * could easily be written in the onClick() method of the adapter's view holder, it is presumably
     * better practice to keep the logic for all user interactions with an activity in the activity
     * class itself. Doing this also streamlines the transfer of data between activities.
     * @param v -- the view that was clicked (actually a TextView)
     */
    @Override
    public void onListItemClick(View v) {

        TextView item = (TextView) v;
        String address = item.getText().toString();

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("ADDRESS", address);
        intent.putExtra(TEXTVIEW_ID, mTvId);
        startActivity(intent);
    }

    /**
     * Gets a list of addresses from the Google Geolocation API based on the place name or address entered.
     * @param locationString -- the location to query the Geolocation API for
     * @return a list of addresses
     */
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
