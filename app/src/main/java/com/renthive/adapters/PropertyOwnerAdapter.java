package com.renthive.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.renthive.R;
import com.renthive.core.PropertyOwner;

import java.util.List;

public class PropertyOwnerAdapter extends RecyclerView.Adapter<PropertyOwnerAdapter.ViewHolder> {

    private List<PropertyOwner> owners;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public PropertyOwnerAdapter(List<PropertyOwner> owners) {
        this.owners = owners;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_property_owner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PropertyOwner owner = owners.get(position);

        holder.nameTextView.setText(owner.getName());
        holder.emailTextView.setText(owner.getEmail());
        holder.phoneTextView.setText(owner.getPhoneNumber());

        // View Documents
        holder.viewDocsButton.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(owner.getUniversityArea()));  // Assuming URL is stored in universityArea
            context.startActivity(intent);
        });

        // Approve Property Owner
        holder.approveButton.setOnClickListener(v -> {
            firestore.collection("propertyOwners")
                    .document(owner.getPropertyOwnerID())
                    .update("verificationStatus", true)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(v.getContext(), "Owner Approved!", Toast.LENGTH_SHORT).show();
                        owners.remove(position);
                        notifyItemRemoved(position);
                    })
                    .addOnFailureListener(e -> Toast.makeText(v.getContext(), "Approval failed!", Toast.LENGTH_SHORT).show());
        });

        // Reject Property Owner
        holder.rejectButton.setOnClickListener(v -> {
            firestore.collection("propertyOwners")
                    .document(owner.getPropertyOwnerID())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(v.getContext(), "Owner Rejected!", Toast.LENGTH_SHORT).show();
                        owners.remove(position);
                        notifyItemRemoved(position);
                    })
                    .addOnFailureListener(e -> Toast.makeText(v.getContext(), "Rejection failed!", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public int getItemCount() {
        return owners.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, emailTextView, phoneTextView;
        Button viewDocsButton, approveButton, rejectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textName);
            emailTextView = itemView.findViewById(R.id.textEmail);
            phoneTextView = itemView.findViewById(R.id.textPhone);
            viewDocsButton = itemView.findViewById(R.id.buttonViewDocs);
            approveButton = itemView.findViewById(R.id.buttonApprove);
            rejectButton = itemView.findViewById(R.id.buttonReject);
        }
    }
}
