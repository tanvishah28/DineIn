package com.example.restaurant_dinein;

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

public class Manager_FeedbackList_Adapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<FeedbackList> FeedbackList;

    public Manager_FeedbackList_Adapter(Context context, int layout, ArrayList<FeedbackList> FeedbackList) {
        this.context = context;
        this.layout = layout;
        this.FeedbackList = FeedbackList;
    }

    @Override
    public int getCount() {
        return FeedbackList.size();
    }

    @Override
    public Object getItem(int position) {
        return FeedbackList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        TextView textView_orderno, textView_mobileno, textview_quality, textview_quantity;
        TextView textview_service, textview_price, textview_ambience;
        LinearLayout linear_layout_feedback;
    }


    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=inflater.inflate(layout, null);

            holder.textView_orderno = (TextView) row.findViewById(R.id.textView_orderno);
            holder.textView_mobileno = (TextView) row.findViewById(R.id.textView_mobileno);
            holder.textview_quality = (TextView) row.findViewById(R.id.textview_quality);
            holder.textview_quantity = (TextView) row.findViewById(R.id.textview_quantity);
            holder.textview_service = (TextView) row.findViewById(R.id.textview_service);
            holder.textview_price = (TextView) row.findViewById(R.id.textview_price);
            holder.textview_ambience = (TextView) row.findViewById(R.id.textview_ambience);
            holder.linear_layout_feedback = (LinearLayout) row.findViewById(R.id.linear_layout_feedback);

            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        FeedbackList feedbackList = FeedbackList.get(position);
        holder.textView_mobileno.setText(feedbackList.getMobileNo());
        holder.textView_orderno.setText(feedbackList.getOrder_Id());
        holder.textview_quality.setText(feedbackList.getQuality());
        holder.textview_quantity.setText(feedbackList.getQuantity());
        holder.textview_price.setText(feedbackList.getPrice());
        holder.textview_service.setText(feedbackList.getService());
        holder.textview_ambience.setText(feedbackList.getAmbience());

        if(Float.parseFloat(feedbackList.getAvgRating()) > 3.5) {
            holder.linear_layout_feedback.setBackgroundResource(R.drawable.high_feedback);
        }
        else {
            holder.linear_layout_feedback.setBackgroundResource(R.drawable.low_feedback);
        }

        return row;
    }
}