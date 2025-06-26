package com.example.rental;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.os.Build;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;

import java.util.HashMap;

public class Cart extends AppCompatActivity {
    private ImageView itemImage;
    private TextView itemName, itemCompany, itemSeller, itemSellerMail, itemDescription, itemLocation, itemPrice;
    private Button cartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize Views
        itemImage = findViewById(R.id.item_image);
        itemName = findViewById(R.id.item_name);
        itemCompany = findViewById(R.id.item_company);
        itemSeller = findViewById(R.id.item_seller);
        itemSellerMail = findViewById(R.id.item_seller_mail);
        itemDescription = findViewById(R.id.item_description);
        itemLocation = findViewById(R.id.item_location);
        itemPrice = findViewById(R.id.item_price);
        cartButton = findViewById(R.id.cart_button);

        // Get Data from Intent
        Intent intent = getIntent();
        itemName.setText("Product Name: " + intent.getStringExtra("item_name"));
        itemCompany.setText("Company Name: " + intent.getStringExtra("item_company"));
        itemSeller.setText("Seller Name: " + intent.getStringExtra("item_seller"));
        itemSellerMail.setText("Seller Mail: " + intent.getStringExtra("item_seller_mail"));
        itemDescription.setText("Product Description: " + intent.getStringExtra("item_description"));
        itemLocation.setText("Product Location: " + intent.getStringExtra("item_location"));
        itemPrice.setText("Price: ₹" + intent.getStringExtra("item_price"));

        // Set Image (If using Glide or Picasso)
        String imageUrl = intent.getStringExtra("item_image");
        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(itemImage);
        }

        // Buy Now Button Click (You can modify it to add the item to a cart)
        cartButton.setOnClickListener(view -> {
            Toast.makeText(this, "Adding item to cart...", Toast.LENGTH_SHORT).show();

            // Get current user ID
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            if (userId == null) {
                Toast.makeText(Cart.this, "User not logged in!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Get item details from Intent
            String selectedItemName = getIntent().getStringExtra("item_name");

            if (selectedItemName == null) {
                Toast.makeText(Cart.this, "Error: No item name found", Toast.LENGTH_SHORT).show();
                return;
            }

            // Reference to Rentals in Firebase
            DatabaseReference rentalsRef = FirebaseDatabase.getInstance().getReference("Rentals");

            rentalsRef.orderByChild("name").equalTo(selectedItemName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String rentalId = snapshot.getKey(); // Rental Firebase Key

                            if (rentalId != null) {
                                addToUserCart(userId, rentalId);
                            } else {
                                Toast.makeText(Cart.this, "Rental ID not found!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(Cart.this, "Item not found in Firebase!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Firebase", "Database error: " + databaseError.getMessage());
                }
            });
        });
    }

    private void addToUserCart(String userId, String rentalId) {
        DatabaseReference userRentalsRef = FirebaseDatabase.getInstance().getReference("UserRentals");

        String cartId = userRentalsRef.push().getKey(); // Generate unique ID

        HashMap<String, Object> cartData = new HashMap<>();
        cartData.put("userId", userId);
        cartData.put("rentalId", rentalId);
        cartData.put("timestamp", System.currentTimeMillis());

        userRentalsRef.child(cartId).setValue(cartData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase", "Item successfully added to cart");
                    Toast.makeText(Cart.this, "Item added to cart!", Toast.LENGTH_SHORT).show();
                    NotificationHelper.showNotification(Cart.this, "Cart Updated", "Your item has been added!");
                    // Check & Request Notification Permission for Android 13+
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
                        } else {
                            // Permission granted, show notification
                            NotificationHelper.showNotification(this, "Ready to Rent", "Your item has been Notified to Place!");
                        }
                    } else {
                        // For Android 12 and below, directly show notification
                        NotificationHelper.showNotification(this, "Ready to Rent", "Your item has been Notified to Place!");
                    }
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Error adding item to cart", e));
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, BorrowView.class); // Navigate to parent activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        super.onBackPressed(); // Ensures system handles the back press correctly
    }
}