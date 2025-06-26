package com.example.rental;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {
    private List<Rental> rentalList;

    public AccountAdapter(List<Rental> rentalList) {
        this.rentalList = rentalList;
    }

    @NonNull
    @Override
    public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account, parent, false);
        return new AccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
        Rental rental = rentalList.get(position);
        holder.rentalName.setText("Name: " + rental.getName());
        holder.rentalCompany.setText("Company: " + rental.getCompany());
        holder.rentalPrice.setText("Price: ₹" + rental.getPrice());
        holder.rentalLocation.setText("Location: " + rental.getLocation());

        // 🚀 Add Click Listener to Open Map
        holder.btnViewOnMap.setOnClickListener(view -> {
            getLatLongFromCity(rental.getLocation(), view.getContext(), rental.getName());
        });
    }

    private void getLatLongFromCity(String cityName, Context context, String rentalName) {
        String url = "https://nominatim.openstreetmap.org/search?q=" + cityName + "&format=json";

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.length() > 0) {
                            JSONObject location = response.getJSONObject(0);
                            double latitude = location.getDouble("lat");
                            double longitude = location.getDouble("lon");

                            Log.d("Location", "Lat: " + latitude + ", Lng: " + longitude);

                            // 🚀 Now Start MapsActivity (AFTER fetching coordinates)
                            Intent intent = new Intent(context, MapsActivity.class);
                            intent.putExtra("latitude", latitude);
                            intent.putExtra("longitude", longitude);
                            intent.putExtra("name", rentalName);
                            context.startActivity(intent);
                        } else {
                            Toast.makeText(context, "Invalid rental location!", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("Volley", "Request failed", error);
                    Toast.makeText(context, "Failed to fetch location!", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonArrayRequest);
    }

    @Override
    public int getItemCount() {
        return rentalList.size();
    }

    static class AccountViewHolder extends RecyclerView.ViewHolder {
        TextView rentalName, rentalCompany, rentalPrice, rentalLocation;
        Button btnViewOnMap;

        public AccountViewHolder(@NonNull View itemView) {
            super(itemView);
            rentalName = itemView.findViewById(R.id.rental_name);
            rentalCompany = itemView.findViewById(R.id.rental_company);
            rentalPrice = itemView.findViewById(R.id.rental_price);
            rentalLocation = itemView.findViewById(R.id.rental_location);
            btnViewOnMap = itemView.findViewById(R.id.btn_view_on_map);
        }
    }
}
