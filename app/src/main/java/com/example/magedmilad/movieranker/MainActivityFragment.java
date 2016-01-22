package com.example.magedmilad.movieranker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.GridView;

import com.example.magedmilad.movieranker.data.DataDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    ArrayList<Movie> items = new ArrayList<Movie>();
    CustomGrid customGrid;
    DataDbHelper favoriteDB;

    public interface Callback {

        public void onItemSelected(Movie m);
    }


    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        favoriteDB = new DataDbHelper(getContext());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateData();
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateData();

    }

    private void updateData() {
        FetchDataTask fetchDataTask = new FetchDataTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String mode = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_units_most_popular_val));
        if (mode.equals(getString(R.string.pref_units_most_popular_val))) {
            fetchDataTask.execute(0);
        } else if (mode.equals(getString(R.string.pref_units_label_highest_rated_val))) {
            fetchDataTask.execute(1);
        } else {
            Movie[] movies = favoriteDB.get_favorite();
            items.clear();
            for (Movie m : movies) {
                items.add(m);
            }
            customGrid.setItems(items);
            customGrid.notifyDataSetChanged();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        customGrid = new CustomGrid(getActivity(), items);
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view);
        gridView.setAdapter(customGrid);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ((Callback) getActivity()).onItemSelected(items.get(position));
            }
        });

        return rootView;
    }


    public class FetchDataTask extends AsyncTask<Integer, Void, Movie[]> {

        @Override
        protected void onPostExecute(Movie[] movies) {
            if (movies != null) {
                items.clear();
                for (Movie m : movies) {
                    items.add(m);
                }
                customGrid.setItems(items);
                customGrid.notifyDataSetChanged();
            }
            if (items.size() > 0 && MainActivity.M_TWO_PANE && DetailActivityFragment.loaded==false)
                ((Callback) getActivity()).onItemSelected(items.get(0));
        }

        private Movie[] parse(String json_str) throws JSONException {
            Log.v("", json_str);

            JSONObject root = new JSONObject(json_str);
            JSONArray results = root.getJSONArray("results");

            Movie[] ret = new Movie[results.length()];

            for (int i = 0; i < ret.length; i++) {
                JSONObject movie = results.getJSONObject(i);
                String original_title = movie.getString("original_title");
                String overview = movie.getString("overview");
                String release_date = movie.getString("release_date");
                String poster_path = movie.getString("poster_path");
                double popularity = movie.getDouble("popularity");
                double vote_average = movie.getDouble("vote_average");
                int vote_count = movie.getInt("vote_count");
                int id = movie.getInt("id");
                ret[i] = new Movie(id, original_title, overview, release_date, poster_path, popularity, vote_average, vote_count);
            }
            return ret;
        }


        @Override
        protected Movie[] doInBackground(Integer... params) {


            if (params.length == 0)
                return null;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JsonStr = null;
            try {

                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/discover/movie?";
                final String sort = "sort_by";
                final String popularity = "popularity.desc";
                final String rating = "vote_average.desc";
                final String APPID_PARAM = "api_key";


                Uri buildUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(sort, params[0] == 0 ? popularity : rating)
                        .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY).build();

                URL url = new URL(buildUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null)
                    return null;
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + '\n');
                }
                if (buffer.length() == 0)
                    return null;
                JsonStr = buffer.toString();
                Movie[] movies = parse(JsonStr);
                return movies;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }

}













