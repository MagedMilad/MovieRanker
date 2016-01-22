package com.example.magedmilad.movieranker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

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

import com.example.magedmilad.movieranker.data.DataDbHelper;


public class DetailActivityFragment extends Fragment {

    ImageView poster;
    static boolean loaded=false;
    Movie movie;
    View rootView;
    DataDbHelper favoriteDB;
    static final String DETAIL_URI = "URI";
    GetImageView getImageView;
    GetTrailersAndReviews getTrailersAndReviews;
    ShareActionProvider mShareActionProvider;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favoriteDB = new DataDbHelper(getContext());
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_detail, menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);

        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();


        rootView = inflater.inflate(R.layout.fragment_detail, container, false);


        if (arguments != null) {
            movie = arguments.getParcelable(DetailActivityFragment.DETAIL_URI);
            loaded = true;
            ((TextView) (rootView.findViewById(R.id.tv_title))).setText(movie.getOriginal_title());
            ((TextView) (rootView.findViewById(R.id.tv_date))).setText(movie.getRelease_date());
            ((TextView) (rootView.findViewById(R.id.tv_overview))).setText(movie.getOverview());
            ((TextView) (rootView.findViewById(R.id.tv_vote))).setText(String.valueOf(movie.getVote_average()));
            poster = (ImageView) rootView.findViewById(R.id.iv_poster);

            getImageView = new GetImageView();
            getImageView.execute();
            if (getTrailersAndReviews != null && getTrailersAndReviews.isCancelled() == false) {
                getTrailersAndReviews.cancel(true);
            }
            if(!(movie.reviews.size() > 0 || movie.trailers.size() > 0)) {
                getTrailersAndReviews = new GetTrailersAndReviews();
                getTrailersAndReviews.execute(movie.getId());
            }
            else{
                addTrailersAndReviews();
            }
            CheckBox favoriteCheckBox = (CheckBox) rootView.findViewById(R.id.cb_favorite);
            if (favoriteDB.is_favorite(movie.id)) {
                favoriteCheckBox.setChecked(true);
            }
            favoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        favoriteDB.insert(movie);
                    } else {
                        favoriteDB.delete(movie.id);
                    }
                }
            });
        }
        return rootView;
    }

    private void addTrailersAndReviews(){
        LinearLayout trailers_list = (LinearLayout) rootView.findViewById(R.id.lv_trailers);

        if(movie.trailers.size() > 0){
            ((TextView) (rootView.findViewById(R.id.tv_trailer))).setText("Trailers");

        }
        for(int i=0;i<movie.trailers.size();i++){
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View vi = inflater.inflate(R.layout.trailer_item, null);
            TextView trailer_name = (TextView) vi.findViewById(R.id.tv_trailer_name);
            trailer_name.setText(movie.trailers.get(i).name);
            final String key = movie.trailers.get(i).key;
            vi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key)).addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET));
                }
            });

            trailers_list.addView(vi);
        }
        LinearLayout reviews_list = (LinearLayout) rootView.findViewById(R.id.lv_review);
        if(movie.reviews.size() > 0){
            ((TextView) (rootView.findViewById(R.id.tv_review))).setText("Reviews");
        }
        for(int i=0;i<movie.reviews.size();i++){
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View vi = inflater.inflate(R.layout.review_item, null);
            TextView author = (TextView) vi.findViewById(R.id.tv_review_author);
            TextView content = (TextView) vi.findViewById(R.id.tv_review_content);
            author.setText(movie.reviews.get(i).author);
            content.setText(movie.reviews.get(i).content);
            reviews_list.addView(vi);
        }
    }


    public class GetImageView extends AsyncTask<Void, Void, RequestCreator> {

        @Override
        protected void onPostExecute(RequestCreator requestCreator) {
            requestCreator.into(poster);
        }

        @Override
        protected RequestCreator doInBackground(Void... params) {

            RequestCreator requestCreator = Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185/" + movie.getPoster_path());
            return requestCreator;
        }


    }


    public class GetTrailersAndReviews extends AsyncTask<Integer, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            if (isCancelled())
                return;

            addTrailersAndReviews();
            if (mShareActionProvider != null) {

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.setType("text/plain");
                if (movie.trailers.size() > 0)
                    shareIntent.putExtra(Intent.EXTRA_TEXT,
                            "http://www.youtube.com/watch?v=" + movie.trailers.get(0).key);
                else
                    shareIntent.putExtra(Intent.EXTRA_TEXT,
                            "No trailers for that movie !");
                mShareActionProvider.setShareIntent(shareIntent);
            }




        }

        private void parseTrailers(String json_str) throws JSONException {
            Log.v("", json_str);

            JSONObject root = new JSONObject(json_str);
            JSONArray results = root.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject res = results.getJSONObject(i);
                String key = res.getString("key");
                String name = res.getString("name");
                Trailer trailer = new Trailer(name, key);
                movie.trailers.add(trailer);
            }
            return;
        }

        private void parsereviews(String json_str) throws JSONException {
            Log.v("", json_str);

            JSONObject root = new JSONObject(json_str);
            JSONArray results = root.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject res = results.getJSONObject(i);
                String author = res.getString("author");
                String content = res.getString("content");
                Review review = new Review(author, content);
                movie.reviews.add(review);
            }
            return;
        }

        void getDetails(int id, boolean Trailer) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JsonStr = null;
            try {

                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie/" + id + "/" + (Trailer ? "videos" : "reviews" + "?");
                final String APPID_PARAM = "api_key";


                Uri buildUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, BuildConfig.Movie_API_KEY).build();

                URL url = new URL(buildUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null)
                    return;
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + '\n');
                }
                if (buffer.length() == 0)
                    return;
                JsonStr = buffer.toString();
                if (Trailer)
                    parseTrailers(JsonStr);
                else
                    parsereviews(JsonStr);
                return;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
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
            return;
        }

        @Override
        protected Void doInBackground(Integer... params) {
            getDetails(params[0], true);
            getDetails(params[0], false);
            return null;
        }
    }

}
