package com.example.android.popularmovies;

import android.content.Context;
import android.graphics.Movie;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Aman on 02-Feb-16.
 */
public class MovieAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<MovieData> mMovieList;

    public MovieAdapter(Context c, ArrayList<MovieData> movieList) {
        mContext = c;
        mMovieList = movieList;
    }

    public int getCount() {
        return mMovieList.size();
    }

    public MovieData getItem(int position) {
        return mMovieList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.list_item_forecast, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.list_item_forecast_textview);
        //imageView.setLayoutParams(new GridView.LayoutParams(120, 180));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String imageBaseUrl = "http://image.tmdb.org/t/p/w185/";
        String imagePath = mMovieList.get(position).getPosterPath();
        Picasso.with(mContext).load(imageBaseUrl + imagePath).into(imageView);
        return convertView;
    }

}
