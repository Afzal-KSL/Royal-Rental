package com.example.rental;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.io.File;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

public class RentalAdapter extends RecyclerView.Adapter<RentalAdapter.ViewHolder> {
    private List<Rent> rentalList;
    private final Context context;

    public RentalAdapter(Context context, List<Rent> rentalList) {
        this.context = context;
        this.rentalList = rentalList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rental, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rent rent = rentalList.get(position);
        holder.name.setText(rent.getName() != null ? rent.getName() : "N/A");
        holder.company.setText(rent.getCompany() != null ? rent.getCompany() : "N/A");
        holder.seller.setText(rent.getSname() != null ? "By: " + rent.getSname() : "By: Unknown");
        holder.price.setText(context.getString(R.string.price_format, rent.getPrice()));

        String imageUriString = rent.getImageUri();
        Log.d("RentalAdapter", "Stored Image URI: " + imageUriString);

        if (imageUriString != null && !imageUriString.isEmpty()) {
            Uri imageUri = Uri.parse(imageUriString);
            File imgFile = new File(imageUri.getPath());

            if (imgFile.exists()) {
                Log.d("RentalAdapter", "Retrieved Image Path: " + rent.getImageUri());
                // ✅ File exists, load with Glide
                Glide.with(holder.image.getContext())
                        .load(rent.getImageUri())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(holder.image);

                Log.d("RentalAdapter", "✅ Image loaded successfully: " + imgFile.getAbsolutePath());
            } else {
                // ❌ File does not exist, use default placeholder
                holder.image.setImageResource(R.drawable.placeholder);
                Log.e("RentalAdapter", "❌ ERROR: Image file NOT found: " + imgFile.getAbsolutePath());
            }
        } else {
            // ❌ Image URI is null/empty, use default placeholder
            holder.image.setImageResource(R.drawable.placeholder);
            Log.e("RentalAdapter", "❌ ERROR: Image URI is NULL or EMPTY!");
        }

        holder.viewMoreButton.setOnClickListener(v -> {
            Toast.makeText(context, "Clicked: " + rent.getName(), Toast.LENGTH_SHORT).show();
            // You can add an intent here to navigate to a detailed view
        });
        holder.viewMoreButton.setOnClickListener(v -> myCartIntent(rent));
    }

    public void myCartIntent(Rent rent) {
        Intent intent = new Intent(context, Cart.class);
        intent.putExtra("item_name", rent.getName());
        intent.putExtra("item_company", rent.getCompany());
        intent.putExtra("item_seller", rent.getSname());
        intent.putExtra("item_seller_mail", rent.getSmail());
        intent.putExtra("item_description", rent.getDescription());
        intent.putExtra("item_location", rent.getLocation());
        intent.putExtra("item_price", rent.getPrice());
        intent.putExtra("item_image", rent.getImageUri());
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return rentalList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, company, seller, price;
        ImageView image;
        Button viewMoreButton;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            company = itemView.findViewById(R.id.item_company);
            seller = itemView.findViewById(R.id.item_seller);
            price = itemView.findViewById(R.id.item_price);
            viewMoreButton = itemView.findViewById(R.id.item_button);
        }
    }

}
