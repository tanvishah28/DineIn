package com.example.customer_dinein;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class OrderList_Adapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<OrderItem> OrderItem;

    public OrderList_Adapter(Context context, int layout, ArrayList<OrderItem> OrderItem) {
        this.context = context;
        this.layout = layout;
        this.OrderItem = OrderItem;
    }

    @Override
    public int getCount() {
        return OrderItem.size();
    }

    @Override
    public Object getItem(int position) {
        return OrderItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        TextView textView_TableNo, textView_no_of_dishes, textView_order_DateTime;
        TextView textView_OrderStatus, textView_PaymentAmt, textView_PaymentStatus;
        LinearLayout linear_layout_payableAmt, linear_layout_paymentstatus;
    }


    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=inflater.inflate(layout, null);

            holder.textView_TableNo = (TextView) row.findViewById(R.id.textView_TableNo);
            holder.textView_no_of_dishes = (TextView) row.findViewById(R.id.textView_no_of_dishes);
            holder.textView_order_DateTime = (TextView) row.findViewById(R.id.textView_order_DateTime);
            holder.textView_OrderStatus = (TextView) row.findViewById(R.id.textView_OrderStatus);
            holder.textView_PaymentAmt = (TextView) row.findViewById(R.id.textView_PaymentAmt);
            holder.textView_PaymentStatus = (TextView) row.findViewById(R.id.textView_PaymentStatus);
            holder.linear_layout_payableAmt = (LinearLayout) row.findViewById(R.id.linear_layout_payableAmt);
            holder.linear_layout_paymentstatus = (LinearLayout) row.findViewById(R.id.linear_layout_paymentstatus);

            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        OrderItem order = OrderItem.get(position);
        holder.textView_TableNo.setText(order.getTableId());
        holder.textView_no_of_dishes.setText(order.getNo_of_dishes());
        holder.textView_order_DateTime.setText(order.getDate_updated());
        holder.textView_OrderStatus.setText(order.getOrder_Status());
        holder.textView_PaymentAmt.setText(order.getTotal_Amount());
        holder.textView_PaymentStatus.setText(order.getPayment_Status());

        holder.linear_layout_paymentstatus.setVisibility(View.VISIBLE);
        holder.linear_layout_paymentstatus.setVisibility(View.VISIBLE);

        if(order.getOrder_Status().trim().equals("Cancelled")) {
            holder.linear_layout_paymentstatus.setVisibility(View.GONE);
        }

        return row;
    }
}