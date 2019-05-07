package com.example.evrouteplannerapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class LocationResultsRecyclerAdapter
        extends RecyclerView.Adapter<LocationResultsRecyclerAdapter.AddressViewHolder> {

    private int mNumItems;

    public LocationResultsRecyclerAdapter(int mNumItems) {
        this.mNumItems = mNumItems;
    }

    @NonNull
    @Override
    public LocationResultsRecyclerAdapter.AddressViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.address_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        AddressViewHolder viewHolder = new AddressViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LocationResultsRecyclerAdapter.AddressViewHolder addressViewHolder, int i) {
        addressViewHolder.bind(i);
    }

    @Override
    public int getItemCount() {
        return mNumItems;
    }

    class AddressViewHolder extends RecyclerView.ViewHolder {

        TextView listItemAddressView;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            listItemAddressView = itemView.findViewById(R.id.tv_item_address);
        }

        public void bind(int listIndex) {
            listItemAddressView.setText(String.valueOf(listIndex));
        }
    }
}
