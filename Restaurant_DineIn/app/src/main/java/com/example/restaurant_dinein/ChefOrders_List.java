package com.example.restaurant_dinein;

public class ChefOrders_List {
    private String date;
    private String mobno;
    private String tableno;
    private String orderno;
    private String order_Status;
    private String noOfItems;

    public ChefOrders_List(String date, String mobno, String tableno, String orderno,String order_Status, String noOfItems) {
        this.date = date;
        this.mobno = mobno;
        this.tableno = tableno;
        this.orderno = orderno;
        this.order_Status = order_Status;
        this.noOfItems = noOfItems;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMobno() {
        return mobno;
    }

    public void setMobno(String mobno) {
        this.mobno = mobno;
    }

    public String getTableno() {
        return tableno;
    }

    public void setTableno(String tableno) {
        this.tableno = tableno;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public  String getOrder_Status() {return order_Status;}

    public  void setOrder_Status(String order_Status) { this.order_Status = order_Status; }

    public String getNoOfItems() {
        return noOfItems;
    }

    public void setNoOfItems(String noOfItems) {
        this.noOfItems = noOfItems;
    }
}
