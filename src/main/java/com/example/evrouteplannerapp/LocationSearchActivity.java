package com.example.evrouteplannerapp;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.TransitionSet;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.example.evrouteplannerapp.Constants.ADDRESS;
import static com.example.evrouteplannerapp.Constants.DESTINATION_COORDS;
import static com.example.evrouteplannerapp.Constants.DESTINATION_TEXT;
import static com.example.evrouteplannerapp.Constants.ORIGIN_COORDS;
import static com.example.evrouteplannerapp.Constants.ORIGIN_TEXT;
import static com.example.evrouteplannerapp.Constants.TEXTVIEW_ID;

public class LocationSearchActivity extends AppCompatActivity
        implements LocationResultsRecyclerAdapter.AddressListItemClickListener {

    private static final String TAG = "LocationSearchActivity";

    private EditText mSearchEditText;
    private FloatingActionButton mSearchButton;
    private RecyclerView mListAddressesRecyclerView;
    private LocationResultsRecyclerAdapter mLocationResultsRecyclerAdapter;
    private List<Address> mAddresses;
    private int mTvId; // the id of the TextView that was clicked to launch this activity
    private String mMapsTvOriginText;
    private String mMapsTvDestinationText;
    private double[] mCoordsOrigin;
    private double[] mCoordsDestination;

    private View.OnClickListener searchButtonClickListener = v -> {

        String locationString = mSearchEditText.getText().toString();
        mAddresses = getAddresses(locationString);

        // Creating the adapter and assigning it to the RecyclerView in the click listener ensures
        // that the user has finished entering his search text and clicked the search button so
        // that the list of addresses is not empty.
        mLocationResultsRecyclerAdapter = new LocationResultsRecyclerAdapter(mAddresses,
                LocationSearchActivity.this);
        mListAddressesRecyclerView.setAdapter(mLocationResultsRecyclerAdapter);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);

        Intent activityOriginIntent = getIntent();
        if (activityOriginIntent.hasExtra(TEXTVIEW_ID))
            mTvId = activityOriginIntent.getIntExtra(TEXTVIEW_ID, -1);
        if (activityOriginIntent.hasExtra(ORIGIN_TEXT))
            mMapsTvOriginText = activityOriginIntent.getStringExtra(ORIGIN_TEXT);
        if (activityOriginIntent.hasExtra(DESTINATION_TEXT))
            mMapsTvDestinationText = activityOriginIntent.getStringExtra(DESTINATION_TEXT);
        if (activityOriginIntent.hasExtra(ORIGIN_COORDS))
            mCoordsOrigin = activityOriginIntent.getDoubleArrayExtra(ORIGIN_COORDS);
        if (activityOriginIntent.hasExtra(DESTINATION_COORDS))
            mCoordsDestination = activityOriginIntent.getDoubleArrayExtra(DESTINATION_COORDS);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSearchEditText = findViewById(R.id.et_search_location);
        mSearchButton = findViewById(R.id.b_search_location);
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
    public void onListItemClick(View v, int index) {

        TextView item = (TextView) v;
        String addressStr = item.getText().toString();
        Address address = mAddresses.get(index);

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(ADDRESS, addressStr);
        intent.putExtra(TEXTVIEW_ID, mTvId);

        /* Updates the appropriate fields depending on whether the id of the TextView whose click handler
         * launched this activity matches the id of the origin or destination TextView. */
        if (mTvId == R.id.tv_origin) {
            mMapsTvOriginText = addressStr;
            mCoordsOrigin = getCoords(address);
        } else if (mTvId == R.id.tv_destination) {
            mMapsTvDestinationText = addressStr;
            mCoordsDestination = getCoords(address);
        }

        if (mCoordsOrigin != null)
            intent.putExtra(ORIGIN_COORDS, mCoordsOrigin);
        if (mCoordsDestination != null)
            intent.putExtra(DESTINATION_COORDS, mCoordsDestination);

        // These fields should not be null, but the check is for safety.
        if (mMapsTvOriginText != null)
            intent.putExtra(ORIGIN_TEXT, mMapsTvOriginText);
        if (mMapsTvDestinationText != null)
            intent.putExtra(DESTINATION_TEXT, mMapsTvDestinationText);

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

    private double[] getCoords(Address address) {
        return new double[] {
                address.getLatitude(),
                address.getLongitude()
        };
    }
}
