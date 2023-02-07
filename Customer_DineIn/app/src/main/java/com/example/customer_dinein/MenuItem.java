package com.example.customer_dinein;

public class MenuItem {
    private String name;
    private String price;
    private String status;
    private String category;
    private String quantity;
    private byte[] image;

    public MenuItem(String name, String price, String status, String category, String quantity, byte[] image) {
        this.name = name;
        this.price = price;
        this.status = status;
        this.category = category;
        this.quantity = quantity;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
