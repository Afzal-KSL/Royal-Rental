package com.example.rental;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.content.Intent;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyAccount extends AppCompatActivity {
    private RecyclerView rentalsRecyclerView;
    private AccountAdapter accountAdapter;
    private List<Rental> rentalList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        rentalsRecyclerView = findViewById(R.id.rentalsRecyclerView);
        rentalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        rentalList = new ArrayList<>();
        accountAdapter = new AccountAdapter(rentalList);
        rentalsRecyclerView.setAdapter(accountAdapter);

        loadUserRentals();
    }

    private void loadUserRentals() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRentalsRef = FirebaseDatabase.getInstance().getReference("UserRentals");

        userRentalsRef.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String rentalId = snapshot.child("rentalId").getValue(String.class);
                                fetchRentalDetails(rentalId);
                            }
                        } else {
                            Toast.makeText(MyAccount.this, "No rentals found for user!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("Firebase", "Failed to read value", databaseError.toException());
                    }
                });
    }

    private void fetchRentalDetails(String rentalId) {
        DatabaseReference rentalsRef = FirebaseDatabase.getInstance().getReference("Rentals").child(rentalId);

        rentalsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String company = dataSnapshot.child("company").getValue(String.class);
                    String price = dataSnapshot.child("price").getValue(String.class);
                    String location = dataSnapshot.child("location").getValue(String.class);

                    rentalList.add(new Rental(name, company, price, location));
                    accountAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Failed to read rental details", databaseError.toException());
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MyAccount.this, Home.class); // Navigate to Home
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        super.onBackPressed(); // Ensure default back behavior
    }
}
