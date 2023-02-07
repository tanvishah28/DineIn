package com.example.restaurant_dinein;

public class OrderItem {
    private String mobileNo, tableId, order_Id, date_updated, order_Status;
    private String chef_Name, total_Amount, payment_Status, no_of_dishes;

    public OrderItem(String mobileNo, String tableId, String order_Id, String date_updated,
                     String order_Status, String chef_Name, String no_of_dishes, String total_Amount, String payment_Status) {
        this.mobileNo = mobileNo;
        this.tableId = tableId;
        this.order_Id = order_Id;
        this.date_updated = date_updated;
        this.order_Status = order_Status;
        this.chef_Name = chef_Name;
        this.no_of_dishes = no_of_dishes;
        this.total_Amount = total_Amount;
        this.payment_Status = payment_Status;
    }

    public String getNo_of_dishes() {
        return no_of_dishes;
    }

    public void setNo_of_dishes(String no_of_dishes) {
        this.no_of_dishes = no_of_dishes;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getOrder_Id() {
        return order_Id;
    }

    public void setOrder_Id(String order_Id) {
        this.order_Id = order_Id;
    }

    public String getDate_updated() {
        return date_updated;
    }

    public void setDate_updated(String date_updated) {
        this.date_updated = date_updated;
    }

    public String getOrder_Status() {
        return order_Status;
    }

    public void setOrder_Status(String order_Status) {
        this.order_Status = order_Status;
    }

    public String getChef_Name() {
        return chef_Name;
    }

    public void setChef_Name(String chef_Name) {
        this.chef_Name = chef_Name;
    }

    public String getTotal_Amount() {
        return total_Amount;
    }

    public void setTotal_Amount(String total_Amount) {
        this.total_Amount = total_Amount;
    }

    public String getPayment_Status() {
        return payment_Status;
    }

    public void setPayment_Status(String payment_Status) {
        this.payment_Status = payment_Status;
    }
}