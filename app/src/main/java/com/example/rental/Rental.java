package com.example.rental;

public class Rental {
    private String name, company, price, location;
//    private double latitude, longitude;

    public Rental() {
        // Default constructor required for Firebase
    }

    public Rental(String name, String company, String price, String location) {
        this.name = name;
        this.company = company;
        this.price = price;
        this.location = location;
    }


    public Rental(String name, String company, String price, String location, double latitude, double longitude) {
        this.name = name;
        this.company = company;
        this.price = price;
        this.location = location;
//        this.latitude = latitude;
//        this.longitude = longitude;
    }

    public String getName() { return name; }
    public String getCompany() { return company; }
    public String getPrice() { return price; }
    public String getLocation() { return location; }
//    public double getLatitude() { return latitude; }
//    public double getLongitude() { return longitude; }
}
