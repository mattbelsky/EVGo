package com.example.evrouteplannerapp;

import android.content.Context;
import android.location.Address;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class LocationResultsRecyclerAdapter
        extends RecyclerView.Adapter<LocationResultsRecyclerAdapter.AddressViewHolder> {

    private List<Address> mAddressList;

    public LocationResultsRecyclerAdapter(List<Address> mAddressList) {
        this.mAddressList = mAddressList;
    }

    /**
     * Creates new views (invoked by the layout manager).
     * @param viewGroup -- in this case, the RecyclerView (?)
     * @param i
     * @return a view holder referencing the newly created view
     */
    @NonNull
    @Override
    public LocationResultsRecyclerAdapter.AddressViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.address_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        // Creates a new view using the new layout inflater and creates a new view holder from the view.
        TextView listItem = (TextView) inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        AddressViewHolder viewHolder = new AddressViewHolder(listItem);

        return viewHolder;
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager).
     * @param viewHolder -- the reference to the view whose contents will be replaced
     * @param position -- the view holder's position in the RecyclerView
     */
    @Override
    public void onBindViewHolder(@NonNull LocationResultsRecyclerAdapter.AddressViewHolder viewHolder, int position) {

        StringBuilder addressBuilder = new StringBuilder();
        Address address = mAddressList.get(position);

        int maxLines = address.getMaxAddressLineIndex();
        if (maxLines >= 0) {
            for (int i = 0; i <= maxLines; i++) {
                String addressLine = address.getAddressLine(i);
                addressBuilder.append(addressLine + "\n");
            }
        }

        String addressStr = addressBuilder.toString();
        viewHolder.listItem.setText(addressStr);
    }

    /**
     * The number of views in the RecyclerView
     * @return the size of the list of addresses passed to the adapter -- should be the same as the
     * number of views
     */
    @Override
    public int getItemCount() {
        return mAddressList.size();
    }

    /**
     * Inner class that provides a reference to the views for each data item. Note: complex views may
     * need more than one view per item, and you provide access to all the views for a data item in a
     * view holder.
     */
    class AddressViewHolder extends RecyclerView.ViewHolder {

        TextView listItem;

        public AddressViewHolder(@NonNull TextView listItem) {
            super(listItem);
            this.listItem = listItem;
        }
    }
}
