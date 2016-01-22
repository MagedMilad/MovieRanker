package com.example.magedmilad.movieranker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by magedmilad on 12/2/15.
 */
public class CustomGrid extends BaseAdapter{

    LayoutInflater inflater;
    ArrayList<Movie> items;
    Context context;

    public CustomGrid(Context context  , ArrayList<Movie> items){
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items;
        this.context=context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void setInflater(LayoutInflater inflater) {
        this.inflater = inflater;
    }

    public void setItems(ArrayList<Movie> items) {
        this.items = items;
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String url = items.get(position).getPoster_path();

        View view = convertView;
        if(view == null){
            view =  inflater.inflate(R.layout.grid_item, null);
        }
        ImageView item = (ImageView) view.findViewById(R.id.grid_item_image);
        Picasso.with(context).load("http://image.tmdb.org/t/p/w185/"+url).into(item);
        return  view;
    }
}
