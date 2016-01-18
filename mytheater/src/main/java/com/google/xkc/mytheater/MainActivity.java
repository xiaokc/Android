package com.google.xkc.mytheater;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private RecyclerView movie_list_rv;
    private List<AMovie> movieList;
    private MovieListAdapter adapter;
    private Toolbar toolbar;

    private DBManager dbManager;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initEvent();


    }

    private void initEvent() {

        updateMovies();

    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        movie_list_rv = (RecyclerView) findViewById(R.id.movie_list_rv);
        movie_list_rv.setLayoutManager(new GridLayoutManager(this, 2));

        toolbar.setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(getResources().getColor(R.color.text_white));

        movieList = new ArrayList<>();

        dbManager = new DBManager(MainActivity.this);
        adapter = new MovieListAdapter(MainActivity.this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortBy = preferences.getString(getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_default_value));

        movieList.clear();

        //favorite movies are saved in local database
        if (sortBy != null &&
                sortBy.equalsIgnoreCase(getString(R.string.pref_sort_by_value_favorite))) {
            cursor = dbManager.queryAllFavorites();
            cursor.moveToFirst();
            if (cursor != null && cursor.getCount() > 0) {
                do {
                    String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                    String poster_path = cursor.getString(cursor.getColumnIndexOrThrow("poster_path"));
                    String original_title = cursor.getString(cursor.getColumnIndexOrThrow("original_title"));
                    String overview = cursor.getString(cursor.getColumnIndexOrThrow("overview"));
                    String release_date = cursor.getString(cursor.getColumnIndexOrThrow("release_date"));
                    String vote_average = cursor.getString(cursor.getColumnIndexOrThrow("vote_average"));

                    AMovie movie = new AMovie(
                            poster_path, original_title, overview, release_date, vote_average, id, true);
                    movieList.add(movie);
                } while (cursor.moveToNext());

            }
            cursor.close();
            adapter.setList(movieList);
            movie_list_rv.setAdapter(adapter);


        } else {
            if (Service.hasNetwork(this)) {
                FetchMoviesTask task = new FetchMoviesTask();
                task.execute(sortBy);
            }else {
                Toast.makeText(this,"Network error",Toast.LENGTH_LONG).show();
                Log.e(LOG_TAG,"Network error");
            }
        }

        adapter.setOnItemClickListener(new MovieListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                AMovie movie = movieList.get(position);

                Intent detailIntent = new Intent(MainActivity.this, MovieDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("movie", movie);
                detailIntent.putExtras(bundle);
                startActivity(detailIntent);
            }

            @Override
            public void onItemLongClick(View v, int position) {

            }
        });


    }


    class FetchMoviesTask extends AsyncTask<String, Void, List<AMovie>> {

        @Override
        protected List<AMovie> doInBackground(String... params) {
//            String temp = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=669e3ca840a4d95a84bd0adfe7d7bc63";

            HttpURLConnection conn = null;
            BufferedReader reader = null;
            String movieJsonStr = null;


            final String MOVIE_DB_BASE_URL = "http://api.themoviedb.org/3/discover/movie";
            final String ORDER_PARAM = "sort_by";
            final String API_KEY = "api_key";

            String order = params[0];

            try {
                Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL).buildUpon()
                        .appendQueryParameter(ORDER_PARAM, order)
                        .appendQueryParameter(API_KEY, Const.THE_MOVIE_DB_APIKEY)
                        .build();

                URL url = new URL(builtUri.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                InputStream inputStream = conn.getInputStream();
                if (inputStream == null) {
                    return null;
                } else {
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        builder.append(line + "\n");
                    }

                    if (builder.length() < 0) {
                        return null;
                    }

                    movieJsonStr = builder.toString();
                }

            } catch (IOException e) {
                Log.e(LOG_TAG, "io exception:" + e.getMessage());
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "reader close failed:" + e.getMessage());
                    }
                }
            }

            Log.i(LOG_TAG, "movieJsonStr=" + movieJsonStr);

            movieList = MovieDataHelper.getMovieListFromJsonStr(movieJsonStr);

            return movieList;
        }


        @Override
        protected void onPostExecute(List<AMovie> result) {
            if (result != null) {
                movieList = result;
                adapter.setList(movieList);
                movie_list_rv.setAdapter(adapter);

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMovies();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
