package com.example.rental;

public class Rent {
    private int id;
    private String name, company, sname, smail, description, price, location, imageUri;
    private double latitude, longitude;

    public Rent(int id, String imageUri, String name, String company, String sname, String smail, String description, String price, String location) {
        this.id = id;
        this.imageUri = imageUri;
        this.name = name;
        this.company = company;
        this.sname = sname;
        this.smail = smail;
        this.description = description;
        this.price = price;
        this.location = location;
    }

    public Rent(String imageUri, String name, String company, String sname, String smail, String description, String price, String location) {
        this.imageUri = imageUri;
        this.name = name;
        this.company = company;
        this.sname = sname;
        this.smail = smail;
        this.description = description;
        this.price = price;
        this.location = location;
    }

    public Rent(){

    }

    public int getId(){
        return id;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getName() {
        return name;
    }
    public String getCompany() {
        return company;
    }
    public String getSname() {
        return sname;
    }
    public String getSmail() {
        return smail;
    }
    public String getDescription() {
        return description;
    }
    public String getPrice() {
        return price;
    }
    public String getLocation() {
        return location;
    }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }

    public void setId(int id){
        this.id = id;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public void setSmail(String smail) {
        this.smail = smail;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

}