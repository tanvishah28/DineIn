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
import android.widget.TextView;

import java.util.ArrayList;

public class Admin_MenuList_Adapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<MenuItem> MenuItem;

    public Admin_MenuList_Adapter(Context context, int layout, ArrayList<MenuItem> MenuItem) {
        this.context = context;
        this.layout = layout;
        this.MenuItem = MenuItem;
    }

    @Override
    public int getCount() {
        return MenuItem.size();
    }

    @Override
    public Object getItem(int position) {
        return MenuItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView txtName, txtPrice, txtStatus, txtCategory;
    }


    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row=inflater.inflate(layout, null);

            holder.txtName = (TextView) row.findViewById(R.id.foodItem_Name);
            holder.txtPrice = (TextView) row.findViewById(R.id.foodItem_Price);
            holder.txtStatus = (TextView) row.findViewById(R.id.foodItem_available);
            holder.txtCategory = (TextView) row.findViewById(R.id.foodItem_Category);
            holder.imageView = (ImageView) row.findViewById(R.id.foodItem_Image);

            row.setTag(holder);
        }
        else {
            holder = (ViewHolder) row.getTag();
        }

        MenuItem menu = MenuItem.get(position);
        holder.txtName.setText(menu.getName());
        holder.txtPrice.setText(menu.getPrice());
        holder.txtStatus.setText(menu.getStatus());
        holder.txtCategory.setText(menu.getCategory());

        byte[] foodimage = menu.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(foodimage, 0, foodimage.length);
        holder.imageView.setImageBitmap(bitmap);

        /*if(menu.getStatus().trim().equals("Available")) {
            holder.txtStatus.setTextColor(Color.GREEN);
        } else {
            holder.txtStatus.setTextColor(Color.RED);
        }*/

        return row;
    }
}