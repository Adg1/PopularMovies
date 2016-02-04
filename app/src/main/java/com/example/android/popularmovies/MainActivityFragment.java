package com.example.android.popularmovies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Movie;
import android.media.Image;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private ArrayList<MovieData> mMovieList = new ArrayList<>();
    private MovieAdapter movieAdapter;
    ProgressDialog progressDialog;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        progressDialog  = new ProgressDialog(getActivity());
        progressDialog.setTitle("Fetching movies..");
        progressDialog.show();
        FetchWeatherTask weatherTask = new FetchWeatherTask();
        weatherTask.execute();

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.forecastfragment, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        movieAdapter = new MovieAdapter(getActivity(), mMovieList);
        GridView listView = (GridView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(movieAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieData forecast = movieAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class).putExtra("movie_data", forecast);
                startActivity(intent);
                //Toast.makeText(getActivity(), forecast ,Toast.LENGTH_SHORT).show();
            }
        });




        return rootView;
    }

    public class FetchWeatherTask extends AsyncTask<Void, Void, ArrayList<MovieData>> {

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();


        private ArrayList<MovieData> getWeatherDataFromJson(String forecastJsonStr) throws JSONException {

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray results = forecastJson.getJSONArray("results");
            ArrayList<MovieData> resultStrs = new ArrayList<>();

            for (int i = 0; i < results.length(); i++) {

                MovieData nMovie = new MovieData();
                JSONObject movieData = results.getJSONObject(i);

                nMovie.setTitle(movieData.getString("title"));
                nMovie.setOverview(movieData.getString("overview"));
                nMovie.setReleaseDate(movieData.getString("release_date"));
                nMovie.setPosterPath(movieData.getString("poster_path"));
                nMovie.setPopularity((float) movieData.getDouble("popularity"));
                nMovie.setVoteAverage((float) movieData.getDouble("vote_average"));
                nMovie.setVoteCount(movieData.getInt("vote_count"));
                resultStrs.add(nMovie);
            }
            return resultStrs;

        }

        @Override
        protected ArrayList<MovieData> doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String sortPref = prefs.getString("sort",
                        getString(R.string.default_pref));
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                String baseUrl = "http://api.themoviedb.org/3/discover/movie?sort_by="+sortPref+"&api_key=b887f8d9fd7dc0cbdff1b721fdf3cd2b";
                URL url = new URL(baseUrl);

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();



                return getWeatherDataFromJson(forecastJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error " + e.getMessage());
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error " + e.getMessage());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieData> result){
            if (result!=null){
                mMovieList.clear();
                mMovieList.addAll(result);
                movieAdapter.notifyDataSetChanged();
            }
            progressDialog.dismiss();
        }


    }
}




