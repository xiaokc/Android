package com.google.xkc.mytheater;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xkc on 1/15/16.
 */
public class MovieDetailActivity extends Activity implements View.OnClickListener {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private Toolbar toolbar;

    private AMovie movie;

    private ImageView poster_thumbnail_iv;
    private TextView original_title_tv;
    private TextView user_rating_tv;
    private TextView release_date_tv;
    private TextView overview_tv;

    private RecyclerView movie_trailers_rv;
    private List<ATrailer> trailerList;
    private TrailerListAdapter trailerListAdapter;

    private RecyclerView movie_reviews_rv;
    private List<AReview> reviewList;
    private ReviewListAdapter reviewListAdapter;

    private ImageButton favorite_btn;
    private boolean isFavorite;
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        initView();

        initEvent();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);

        poster_thumbnail_iv = (ImageView) findViewById(R.id.poster_thumbnail_iv);
        original_title_tv = (TextView) findViewById(R.id.original_title_tv);
        user_rating_tv = (TextView) findViewById(R.id.user_rating_tv);
        release_date_tv = (TextView) findViewById(R.id.release_date_tv);
        overview_tv = (TextView) findViewById(R.id.overview_tv);

        movie_trailers_rv = (RecyclerView) findViewById(R.id.movie_trailers_rv);
        movie_trailers_rv.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        movie_reviews_rv = (RecyclerView) findViewById(R.id.movie_reviews_rv);
        movie_reviews_rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        toolbar.setTitle(getString(R.string.movie_detail));
        toolbar.setTitleTextColor(getResources().getColor(R.color.text_white));

        favorite_btn = (ImageButton) findViewById(R.id.favorite_btn);

        dbManager = new DBManager(MovieDetailActivity.this);

    }

    private void initEvent() {
        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();
        movie = (AMovie) bundle.getSerializable("movie");

        showMovieDetail(movie);
        showTrailersAndReviews(movie);

        favorite_btn.setOnClickListener(this);
    }

    private void showTrailersAndReviews(AMovie movie) {
        String id = movie.getId();
        if (Service.hasNetwork(this)) {
            FetchMovieDetailsTask task = new FetchMovieDetailsTask();
            task.execute(id);
        }else {
            Toast.makeText(this, "Network error", Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG,"Network error");
        }

    }

    private void showMovieDetail(AMovie movie) {
        String original_title = movie.getOriginal_title();
        String poster_path = movie.getPoster_path();
        String user_rating = movie.getVote_average();
        String release_date = movie.getRelease_date();
        String overview = movie.getOverview();
        isFavorite = movie.isFavorite() || dbManager.isExist(movie);

        Picasso.with(this).load(poster_path).into(poster_thumbnail_iv);
        original_title_tv.setText(original_title);
        user_rating_tv.setText(user_rating);
        release_date_tv.setText(release_date);
        overview_tv.setText(overview);

        favorite_btn.setBackground(isFavorite
                ? getResources().getDrawable(R.drawable.ic_favorite_2)
                : getResources().getDrawable(R.drawable.ic_favorite_1));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.favorite_btn:
                //allow users to mark a movie as a favorite in the details view
                // by tapping a button(star).
                // This is for a local movies collection that
                // you will maintain and does not require an API request*.

                movie.setFavorite(!movie.isFavorite());
                isFavorite = movie.isFavorite();
                favorite_btn.setBackground(isFavorite
                        ? getResources().getDrawable(R.drawable.ic_favorite_2)
                        : getResources().getDrawable(R.drawable.ic_favorite_1));

                boolean isExist = dbManager.isExist(movie);
                if (isFavorite) {
                    if (!isExist) {
                        Log.i(LOG_TAG, "movie is not exist");
                        dbManager.addOneMovie(movie);
                    }
                } else {
                    if (isExist) {
                        dbManager.deleteOneMovie(movie);
                    }
                }
                break;
        }

    }


    class FetchMovieDetailsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String id = params[0];
            final String BASE_URL = "http://api.themoviedb.org/3/movie/";
            final String URLSTR = BASE_URL.concat(id);
            final String API_KEY = "api_key";
            final String APPEND_TO_RESPONSE = "append_to_response";

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String detailsStr = null;

            Uri builtUri = Uri.parse(URLSTR).buildUpon()
                    .appendQueryParameter(API_KEY, Const.THE_MOVIE_DB_APIKEY)
                    .appendQueryParameter(APPEND_TO_RESPONSE, "videos".concat(",").concat("reviews"))
                    .build();

//            Log.i(LOG_TAG, "builtUri=" + builtUri);

            try {
                URL url = new URL(builtUri.toString());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                if (inputStream == null) {
                    return null;
                } else {
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder builder = new StringBuilder();
                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        builder.append(line + "\n");
                    }

                    if (builder.length() <= 0) {
                        return null;
                    } else {
                        detailsStr = builder.toString();
                    }
                }

                Log.i(LOG_TAG, "detailsStr: " + detailsStr);


            } catch (IOException e) {
                Log.e(LOG_TAG, "io exception: " + e.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "BufferReader close failed: " + e.getMessage());
                    }
                }
            }

            return detailsStr;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            reviewList = MovieDataHelper.getReviewListFromJsonStr(result);
            reviewListAdapter = new ReviewListAdapter(MovieDetailActivity.this, reviewList);
            movie_reviews_rv.setAdapter(reviewListAdapter);


            trailerList = MovieDataHelper.getTrailerListFromJsonStr(result);
            trailerListAdapter = new TrailerListAdapter(MovieDetailActivity.this, trailerList);
            movie_trailers_rv.setAdapter(trailerListAdapter);


            trailerListAdapter.setOnItemClickListener(new TrailerListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int pos) {
                    ATrailer trailer = trailerList.get(pos);
                    String trailerUrl = String.format("http://www.youtube.com/watch?v=%1$s", trailer.getId());
                    if (trailer.getSite().equalsIgnoreCase("YouTube")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(trailerUrl));
                        startActivity(intent);
                    } else {
                        throw new UnsupportedOperationException("Only YouTube is supported!");
                    }
                }

                @Override
                public void onItemLongClick(View v, int pos) {

                }
            });

        }
    }


}
