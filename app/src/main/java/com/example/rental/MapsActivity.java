package com.example.rental;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView mapView;
    private double rentalLatitude, rentalLongitude;
    private String rentalName;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize Map
        Configuration.getInstance().load(getApplicationContext(), getSharedPreferences("osmdroid", MODE_PRIVATE));
        mapView = findViewById(R.id.map);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        // Get Rental Location from Intent
        rentalLatitude = getIntent().getDoubleExtra("latitude", 0.0);
        rentalLongitude = getIntent().getDoubleExtra("longitude", 0.0);
        rentalName = getIntent().getStringExtra("name");

        // Check Permissions and Enable Location
        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        // Show Rental Location on Map
        showRentalLocation();
    }

    private void showRentalLocation() {
        if (rentalLatitude == 0.0 || rentalLongitude == 0.0) {
            Toast.makeText(this, "Invalid rental location!", Toast.LENGTH_SHORT).show();
            return;
        }

        GeoPoint rentalPoint = new GeoPoint(rentalLatitude, rentalLongitude);
        mapView.getController().setCenter(rentalPoint);
        mapView.getController().setZoom(15.0);

        // Add Marker for Rental Location
        Marker rentalMarker = new Marker(mapView);
        rentalMarker.setPosition(rentalPoint);
        rentalMarker.setTitle(rentalName);
        mapView.getOverlays().add(rentalMarker);

        // Get Current Location and Draw Route
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionsIfNecessary(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
            return;
        }

        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double userLat = location.getLatitude();
                    double userLon = location.getLongitude();
                    Log.d("User Location", "Lat: " + userLat + ", Lon: " + userLon);

                    // Draw Route
                    drawRoute(userLat, userLon, rentalLatitude, rentalLongitude);
                } else {
                    Toast.makeText(MapsActivity.this, "Unable to get current location!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void drawRoute(double startLat, double startLon, double endLat, double endLon) {
        String apiKey = "5b3ce3597851110001cf62484e922e0fb5c94276b563c84d7d622358";
        String url = "https://api.openrouteservice.org/v2/directions/driving-car?api_key=" + apiKey +
                "&start=" + startLon + "," + startLat + "&end=" + endLon + "," + endLat;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("Route Response", response.toString()); // Log full response

                        JSONArray features = response.getJSONArray("features");
                        if (features.length() == 0) {
                            Log.e("Route Error", "No route features found!");
                            return;
                        }

                        JSONObject geometry = features.getJSONObject(0).getJSONObject("geometry");
                        if (geometry.has("coordinates")) {
                            JSONArray coordinates = geometry.getJSONArray("coordinates");
                            ArrayList<GeoPoint> routePoints = new ArrayList<>();

                            for (int i = 0; i < coordinates.length(); i++) {
                                JSONArray point = coordinates.getJSONArray(i);
                                double lon = point.getDouble(0);
                                double lat = point.getDouble(1);
                                routePoints.add(new GeoPoint(lat, lon));
                            }

                            // Draw route
                            drawPolyline(routePoints);
                        } else {
                            Log.e("Route Error", "No 'coordinates' found!");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Route Error", "JSON Parsing Error: " + e.getMessage());
                    }
                }, error -> Log.e("Route Error", "Failed to get route: " + error.toString())
        );

        queue.add(request);
    }



    private void drawPolyline(ArrayList<GeoPoint> routePoints) {
        if (routePoints.isEmpty()) {
            Log.e("Route Error", "No points to draw on the map!");
            return;
        }

        Polyline polyline = new Polyline();
        polyline.setPoints(routePoints);
        polyline.setColor(0xFF006400); // Dark Green color
        polyline.setWidth(10.0f); // Thicker route
        polyline.getPaint().setStrokeCap(Paint.Cap.ROUND); // Rounded edges
        polyline.getPaint().setPathEffect(new DashPathEffect(new float[]{20, 10}, 0)); // Dashed effect

        mapView.getOverlays().add(polyline);
        mapView.invalidate();

        // Add Start and End Markers
        addMarker(routePoints.get(0), "Start");
        addMarker(routePoints.get(routePoints.size() - 1), "End");
    }

    private void addMarker(GeoPoint point, String title) {
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setTitle(title);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(marker);
    }



    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showRentalLocation();
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
