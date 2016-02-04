package com.example.android.popularmovies;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailActivityFragment extends Fragment {

    public MovieDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent =getActivity().getIntent();
        View rootView =inflater.inflate(R.layout.fragment_movie_detail, container, false);
        if(intent!=null && intent.hasExtra("movie_data")){
            Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
                    "Roboto-Light.ttf");
            Typeface tf1 = Typeface.createFromAsset(getActivity().getAssets(),
                    "Roboto-Bold.ttf");
            MovieData forecastStr = (MovieData) intent.getSerializableExtra("movie_data");
            ((TextView) rootView.findViewById(R.id.detail_text)).setText(forecastStr.getTitle());
            ((TextView) rootView.findViewById(R.id.detail_text)).setTypeface(tf1);
            ((TextView) rootView.findViewById(R.id.movie_synopsis)).setText(forecastStr.getOverview());
            ((TextView) rootView.findViewById(R.id.movie_synopsis)).setTypeface(tf);
            ((TextView) rootView.findViewById(R.id.movie_release)).setText("Release: " + forecastStr.getReleaseDate());
            ((TextView) rootView.findViewById(R.id.movie_release)).setTypeface(tf1);
            ((TextView) rootView.findViewById(R.id.movie_rating)).setText(("Rating: "+ forecastStr.getVoteAverage()+"/10.0"));
            ((TextView) rootView.findViewById(R.id.movie_rating)).setTypeface(tf1);
            ImageView imageView= (ImageView) rootView.findViewById(R.id.imageView);

            String imageBaseUrl = "http://image.tmdb.org/t/p/w185/";
            String imagePath = forecastStr.getPosterPath();
            Picasso.with(getActivity()).load(imageBaseUrl + imagePath).into(imageView);
        }
        return rootView;
    }
}
