package com.example.customer_dinein;

public class OrderItem {
    private String tableId, order_Id, date_updated, order_Status, total_Amount, no_of_dishes, payment_Status;

    public OrderItem(String tableId, String order_Id, String date_updated,
                     String order_Status, String no_of_dishes, String total_Amount, String payment_Status) {
        this.tableId = tableId;
        this.order_Id = order_Id;
        this.date_updated = date_updated;
        this.order_Status = order_Status;
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