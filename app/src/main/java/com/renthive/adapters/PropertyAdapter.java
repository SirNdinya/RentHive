package com.renthive.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.renthive.core.Property;
import com.renthive.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {

    private List<Property> propertyList = new ArrayList<>();

    public void submitList(List<Property> properties) {
        this.propertyList = properties;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listing, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = propertyList.get(position);
        holder.tvPropertyLocation.setText(property.getLocation());
        holder.tvPropertyDescription.setText(property.getDescription());

        // Load image using Picasso or Glide
        String imageUrl = property.getImageUri();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl).into(holder.imgProperty);
        } else {
            holder.imgProperty.setImageResource(R.drawable.placeholder_image); // Default image if no URL
        }
    }


    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    static class PropertyViewHolder extends RecyclerView.ViewHolder {
        TextView tvPropertyLocation, tvPropertyPrice, tvPropertyDescription;
        ImageView imgProperty;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPropertyLocation = itemView.findViewById(R.id.tvPropertyLocation);
            tvPropertyPrice = itemView.findViewById(R.id.tvPropertyPrice);
            tvPropertyDescription = itemView.findViewById(R.id.tvPropertyDescription);
            imgProperty = itemView.findViewById(R.id.imgProperty);
        }
    }
}
