package com.example.evrouteplannerapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.example.evrouteplannerapp.Constants.SITE_ADDR_1;
import static com.example.evrouteplannerapp.Constants.SITE_ADDR_2;
import static com.example.evrouteplannerapp.Constants.SITE_COST;
import static com.example.evrouteplannerapp.Constants.SITE_POWER_KW;
import static com.example.evrouteplannerapp.Constants.SITE_TITLE;

public class SiteInfoFragment extends Fragment {

    private final String TAG = "SiteInfoFragment";

    private TextView mTitleTextView;
    private TextView mAddress1TextView;
    private TextView mAddress2TextView;
    private TextView mPowerKWTextView;
    private TextView mCostTextView;
    private String mTitle;
    private String mAddress1;
    private String mAddress2;
    private String mPowerKW;
    private String mCost;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_site_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = this.getArguments();
        boolean flag = false;
        try {
            mTitleTextView = view.findViewById(R.id.tv_site_name);
            mAddress1TextView = view.findViewById(R.id.tv_site_address_1);
            mAddress2TextView = view.findViewById(R.id.tv_site_address_2);
            mPowerKWTextView = view.findViewById(R.id.tv_power_kw);
            mCostTextView = view.findViewById(R.id.tv_cost);

            mTitle = bundle.getString(SITE_TITLE);
            mAddress1 = bundle.getString(SITE_ADDR_1);
            mAddress2 = bundle.getString(SITE_ADDR_2);
            mPowerKW = bundle.getString(SITE_POWER_KW);
            mCost = bundle.getString(SITE_COST);
        } catch (NullPointerException e) {
            flag = true;
            Log.w(TAG, "Incomplete data set sent to this fragment.");
        }

        if (flag == false) {
            mTitleTextView.setText(mTitle);
            mAddress1TextView.setText(mAddress1);
            mAddress2TextView.setText(mAddress2);
            mPowerKWTextView.setText(mPowerKW + " KW");
            mCostTextView.setText("$" + mCost); // TODO Add functionality for switching currencies.

            // Hides the "Find Route" button. This is necessary because view.bringToFront() apparently
            // doesn't work for views that have children.
            getActivity().findViewById(R.id.b_find_route).setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        getActivity().findViewById(R.id.b_find_route).setVisibility(View.VISIBLE);
    }
}
