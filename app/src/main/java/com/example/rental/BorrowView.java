package com.example.rental;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rental.data.MyDbHandler;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BorrowView extends AppCompatActivity {
    private RecyclerView recyclerView, sortFilterRecyclerView;
    private RentalAdapter adapter;
    private List<Rent> fullRentalList; // Stores all items (SQLite + Firebase)
    private List<Rent> filteredList;   // Stores searched/filtered items
    private SortFilterAdapter sortFilterAdapter;

    private DatabaseReference databaseReference; // Firebase Reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_view);

        androidx.appcompat.widget.SearchView searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        sortFilterRecyclerView = findViewById(R.id.sortFilterRecyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sortFilterRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        fullRentalList = new ArrayList<>();
        filteredList = new ArrayList<>();

        adapter = new RentalAdapter(this, filteredList);
        recyclerView.setAdapter(adapter);

        // Fetch data from SQLite
        fetchSQLiteData();

        // Fetch data from Firebase
        fetchFirebaseData();

        // Setup search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterList(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });

        // Setup sorting and filtering
        setupSortFilterOptions();
    }

    // Fetch rentals from SQLite
    private void fetchSQLiteData() {
        MyDbHandler db = new MyDbHandler(this);
        List<Rent> sqliteRentals = db.getAllRentals();
        fullRentalList.addAll(sqliteRentals);
        filteredList.addAll(sqliteRentals);
        adapter.notifyDataSetChanged();
    }

    // Fetch rentals from Firebase
    private void fetchFirebaseData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Rentals");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Rent rental = snapshot.getValue(Rent.class);
                    if (rental != null) {
                        fullRentalList.add(rental);
                    }
                }
                filteredList.clear();
                filteredList.addAll(fullRentalList);
                adapter.notifyDataSetChanged(); // Refresh RecyclerView
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Failed to read values", databaseError.toException());
            }
        });
    }

    // Search filter function
    private void filterList(String text) {
        filteredList.clear();
        if (text.isEmpty()) {
            filteredList.addAll(fullRentalList); // Show all if search is empty
        } else {
            for (Rent rent : fullRentalList) {
                if (rent.getName().toLowerCase().contains(text.toLowerCase()) ||
                        rent.getCompany().toLowerCase().contains(text.toLowerCase())) {
                    filteredList.add(rent);
                }
            }
        }
        adapter.notifyDataSetChanged(); // Refresh RecyclerView
    }

    // Setup sorting and filtering options
    private void setupSortFilterOptions() {
        List<String> options = new ArrayList<>();
        options.add("Sort: Price Low to High");
        options.add("Sort: Price High to Low");
        options.add("Filter: Location Nearby");
        options.add("Filter: Most Popular");

        sortFilterAdapter = new SortFilterAdapter(options, this::handleSortFilterClick);
        sortFilterRecyclerView.setAdapter(sortFilterAdapter);
    }

    // Handle sorting and filtering clicks
    private void handleSortFilterClick(String option) {
        if (option.contains("Price Low to High")) {
            filteredList.sort((a, b) -> Integer.compare(Integer.parseInt(a.getPrice()), Integer.parseInt(b.getPrice())));
        } else if (option.contains("Price High to Low")) {
            filteredList.sort((a, b) -> Integer.compare(Integer.parseInt(b.getPrice()), Integer.parseInt(a.getPrice())));
        } else if (option.contains("Location Nearby")) {
            // Example filter (Implement your logic)
        } else if (option.contains("Most Popular")) {
            // Example filter (Implement your logic)
        }

        adapter.notifyDataSetChanged(); // Refresh RecyclerView
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Home.class); // Navigate to Home
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        super.onBackPressed(); // Call default back behavior
    }
}
