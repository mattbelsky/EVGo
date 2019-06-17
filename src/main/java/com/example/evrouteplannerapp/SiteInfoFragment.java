package com.example.evrouteplannerapp;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import static com.example.evrouteplannerapp.Constants.DESTINATION_COORDS;
import static com.example.evrouteplannerapp.Constants.SITE_ADDR_1;
import static com.example.evrouteplannerapp.Constants.SITE_ADDR_2;
import static com.example.evrouteplannerapp.Constants.SITE_COST;
import static com.example.evrouteplannerapp.Constants.SITE_POWER_KW;
import static com.example.evrouteplannerapp.Constants.SITE_TITLE;

public class SiteInfoFragment extends Fragment implements ViewTreeObserver.OnGlobalLayoutListener,
        View.OnClickListener {

    private final String TAG = "SiteInfoFragment";

    private TextView mTitleTextView;
    private TextView mAddress1TextView;
    private TextView mAddress2TextView;
    private TextView mPowerKWTextView;
    private TextView mCostTextView;
    private Button mNavigateButton;
    private LatLng mCoords;
    private String mTitle;
    private String mAddress1;
    private String mAddress2;
    private String mPowerKW;
    private String mCost;
    private boolean mStateChangedFlag;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_site_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        mStateChangedFlag = false;
        boolean incompleteDataFlag = false;
        try {
            mTitleTextView = view.findViewById(R.id.tv_site_name);
            mAddress1TextView = view.findViewById(R.id.tv_site_address_1);
            mAddress2TextView = view.findViewById(R.id.tv_site_address_2);
            mPowerKWTextView = view.findViewById(R.id.tv_power_kw);
            mCostTextView = view.findViewById(R.id.tv_cost);

            double[] coords = bundle.getDoubleArray(DESTINATION_COORDS);
            mCoords = new LatLng(coords[0], coords[1]);
            mTitle = bundle.getString(SITE_TITLE);
            mAddress1 = bundle.getString(SITE_ADDR_1);
            mAddress2 = bundle.getString(SITE_ADDR_2);
            mPowerKW = bundle.getString(SITE_POWER_KW);
            mCost = bundle.getString(SITE_COST);
        } catch (NullPointerException e) {
            incompleteDataFlag = true;
        }

        if (!incompleteDataFlag) {
            mTitleTextView.setText(mTitle);
            mAddress1TextView.setText(mAddress1);
            mAddress2TextView.setText(mAddress2);
            mPowerKWTextView.setText(mPowerKW + " KW");
            mCostTextView.setText(mCost);

            // Hides the "Find Route" button. This is necessary because view.bringToFront() apparently
            // doesn't work for views that have children.
            getActivity().findViewById(R.id.b_find_route).setVisibility(View.GONE);

            /* Observer's listener is called when the view's state changes, in this case, when visibility
             * changes from GONE to VISIBLE. The state is altered here and a flag set to true so that,
             * in this specific scenario, this object is given time to construct the view and measure
             * the dimensions of its children before attempting to animate the view. The view's height
             * will be 0 unless the listener is triggered in this way.
             */
            getView().setVisibility(View.VISIBLE);
            mStateChangedFlag = true;
            view.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }

        mNavigateButton = view.findViewById(R.id.b_navigate);
        mNavigateButton.setOnClickListener(this);
    }

    /**
     * When the navigate button is clicked, opens the default maps app with a marker at the supplied location.
     * @param v
     */
    @Override
    public void onClick(View v) {

        String lat = String.valueOf(mCoords.latitude);
        String lng = String.valueOf(mCoords.longitude);
//        Uri location = Uri.parse("geo:0,0?q=" + lat + "," + lng + "(" + mTitle + ")");
        Uri location = Uri.parse("google.navigation:q=" + lat + "," + lng + "&mode=d");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
        mapIntent.setPackage("com.google.android.apps.maps");

        // Verify it resolves
        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
        boolean isIntentSafe = activities.size() > 0;

        // Start an activity if it's safe
        if (isIntentSafe)
            startActivity(mapIntent);
    }

    /**
     * Called every time the layout's state is changed. When the flag, manually set, is true, animates
     * this fragment's parent view to slide up from the bottom of the screen. To do this, gets the height
     * of the parent activity -- more specifically, the height of the constraint layout encompassing
     * the entire screen -- and the height of the view containing all the elements of this fragment,
     * and defines the animation so that the view only slides up as far as it is tall.
     */
    @Override
    public void onGlobalLayout() {

        if (mStateChangedFlag) {

            float start = getActivity().findViewById(R.id.cl_maps_activity).getHeight();
            float end = getView().getHeight();
            ObjectAnimator animator = ObjectAnimator.ofFloat(getView(), "translationY", start, start - end);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setDuration(500);
            animator.start();
            mStateChangedFlag = false;
        }
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        getActivity().findViewById(R.id.b_find_route).setVisibility(View.VISIBLE);

        // Must remove listener at this point, otherwise the program crashes.
        getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }
}
