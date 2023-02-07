package com.example.restaurant_dinein;

public class FeedbackList {
    private String mobileNo, order_Id, quality, quantity, price, service, ambience, avgRating;

    public FeedbackList(String order_Id, String mobileNo, String quality, String avgRating,
                        String quantity, String price, String service, String ambience) {
        this.order_Id = order_Id;
        this.mobileNo = mobileNo;
        this.quality = quality;
        this.avgRating = avgRating;
        this.quantity = quantity;
        this.price = price;
        this.service = service;
        this.ambience = ambience;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getOrder_Id() {
        return order_Id;
    }

    public void setOrder_Id(String order_Id) {
        this.order_Id = order_Id;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getAmbience() {
        return ambience;
    }

    public void setAmbience(String ambience) {
        this.ambience = ambience;
    }

    public String getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(String avgRating) {
        this.avgRating = avgRating;
    }
}
