package com.renthive.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.renthive.R;
import com.renthive.core.Listing;
import java.util.ArrayList;
import java.util.List;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingViewHolder> {

    private List<Listing> listingList = new ArrayList<>();
    private Context context;

    public ListingAdapter(Context context) {
        this.context = context;
    }

    public void submitList(List<Listing> listings) {
        this.listingList = listings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listing, parent, false);
        return new ListingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
        Listing listing = listingList.get(position);
        holder.tvListingPrice.setText("Price: $" + listing.getPrice());
        holder.tvListingDetails.setText(listing.getDescription());

        // Load image
        if (listing.getImageURL() != null) {
            holder.imgListing.setImageURI(Uri.parse(listing.getImageURL()));

        }
    }

    @Override
    public int getItemCount() {
        return listingList.size();
    }

    static class ListingViewHolder extends RecyclerView.ViewHolder {
        TextView tvListingPrice, tvListingDetails;
        ImageView imgListing;

        public ListingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvListingPrice = itemView.findViewById(R.id.tvListingPrice);
            tvListingDetails = itemView.findViewById(R.id.tvListingDetails);
            imgListing = itemView.findViewById(R.id.imgListing);
        }
    }
}
