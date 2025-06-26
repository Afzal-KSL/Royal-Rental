package com.example.rental;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.RequestQueue;
import org.json.JSONArray;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.example.rental.data.MyDbHandler;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class RentalForm extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    String storedImagePath;

    private ImageView productImage;
    EditText productName, productCompany, productSeller, sellerMail, productDescription, productPrice, productLocation;
    MyDbHandler db;

    private Uri saveImageToAppStorage(Uri sourceUri, Context context) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(sourceUri);
            File file = new File(context.getFilesDir(), "img_" + System.currentTimeMillis() + ".jpg"); // Unique filename
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            return Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData(); // Original content:// URI

                    // Save image to internal storage and get the new URI
                    Uri savedImageUri = saveImageToAppStorage(selectedImageUri, this);

                    if (savedImageUri != null) {
                        productImage.setImageURI(savedImageUri); // Display image
                        storedImagePath = savedImageUri.toString(); // Store this in SQLite
                    } else {
                        Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental_form);

        productImage = findViewById(R.id.product_image);
        productName = findViewById(R.id.product_name_value);
        productCompany = findViewById(R.id.product_company_value);
        productSeller = findViewById(R.id.product_seller_value);
        sellerMail = findViewById(R.id.seller_mail_value);
        productDescription = findViewById(R.id.product_description_value);
        productPrice = findViewById(R.id.product_price_value);
        productLocation = findViewById(R.id.product_location_value);

        db = new MyDbHandler(RentalForm.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            productImage.setImageURI(imageUri); // Display image
        }
    }

    public void selectImageFromGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    public void addRentalToDB(View view) {
        String name = productName.getText().toString().trim();
        String company = productCompany.getText().toString().trim();
        String seller = productSeller.getText().toString().trim();
        String email = sellerMail.getText().toString().trim();
        String description = productDescription.getText().toString().trim();
        String price = productPrice.getText().toString().trim();
        String location = productLocation.getText().toString().trim();

        if (name.isEmpty() || company.isEmpty() || seller.isEmpty() || email.isEmpty() || description.isEmpty() || price.isEmpty() || location.isEmpty()) {
            Log.d("RentalForm", "All fields must be filled.");
            return;
        }

        Rent tool = new Rent(storedImagePath != null ? storedImagePath : "", name, company, seller, email, description, price, location);
        Log.e("RentalForm", "Stored Image Path: " + storedImagePath);
        getLatLongFromCity(location, tool);
        db.addRental(tool);

        Log.d("RentalForm", "Successfully Inserted into DB: " + tool.getName());

        // Clear input fields after inserting
        productName.setText("");
        productCompany.setText("");
        productSeller.setText("");
        sellerMail.setText("");
        productDescription.setText("");
        productPrice.setText("");
        productLocation.setText("");

        // Clear the image field
        productImage.setImageResource(android.R.color.transparent); // Reset ImageView
        storedImagePath = null; // Clear stored path

        Intent intent = new Intent(this, BorrowView.class);
        startActivity(intent);
    }

    public void sendLocationToFirebase(Rent tool) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Rentals");

        String rentId = databaseReference.push().getKey();
        if (rentId != null) {
            tool.setId(Integer.parseInt(rentId.hashCode() + "")); // Store rentId as an integer
            databaseReference.child(rentId).setValue(tool)
                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Rental stored successfully with ID: " + rentId))
                    .addOnFailureListener(e -> Log.e("Firebase", "Failed to store rental", e));
        }
    }


    public void getLatLongFromCity(String cityName, Rent tool) {
        String url = "https://nominatim.openstreetmap.org/search?q=" + cityName + "&format=json";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if (response.length() > 0) { // Ensure response is not empty
                                JSONObject location = response.getJSONObject(0);
                                double latitude = location.getDouble("lat");
                                double longitude = location.getDouble("lon");

                                Log.d("Location", "Lat: " + latitude + ", Lng: " + longitude);

                                tool.setLatitude(latitude);
                                tool.setLongitude(longitude);
                                sendLocationToFirebase(tool);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Request failed", error);
                    }
                });

        queue.add(jsonArrayRequest);
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RentalForm.this, Home.class); // Navigate to Home
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        super.onBackPressed(); // Ensure default back behavior
    }
}