package com.google.xkc.mytheater;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xkc on 1/14/16.
 */
public class MovieDataHelper {

    static final String POSTER_BASE_PATH = "http://image.tmdb.org/t/p";

    //Then you will need a ‘size’, which will be one of the following: "w92", "w154",
    // "w185", "w342", "w500", "w780", or "original". For most phones we recommend using “w185”.
    static final String POSTER_SIZE = "/w342/";
    static final String LOG_TAG = "MovieDataHelper";

    public static List<AMovie> getMovieListFromJsonStr(String movieJsonStr) {
        List<AMovie> movieList = new ArrayList<>();

        try {
            JSONObject movieJsonObj = new JSONObject(movieJsonStr);
            JSONArray resultJsonArr = movieJsonObj.getJSONArray("results");
            int movieCnt = resultJsonArr.length();

            for (int i = 0; i < movieCnt; i++) {
                JSONObject aMovie = resultJsonArr.getJSONObject(i);

                //You will need to append a base path ahead of this relative path
                // to build the complete url you will need to fetch the image using Picasso.
                String id = aMovie.getString("id");
                String poster_path = aMovie.getString("poster_path");
                String overview = aMovie.getString("overview");
                String original_title = aMovie.getString("original_title");
                String vote_average = aMovie.getString("vote_average");
                String release_date = aMovie.getString("release_date");

                AMovie movie = new AMovie(POSTER_BASE_PATH + POSTER_SIZE + poster_path,
                        original_title,overview,release_date,vote_average,id);

               movieList.add(movie);

            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "getMovieListFromJsonStr json exception:" + e.getMessage());
        }

        return movieList;

    }

    public static List<ATrailer> getTrailerListFromJsonStr(String jsonStr){
        List<ATrailer> trailerList = new ArrayList<>();
        Log.i(LOG_TAG,"jsonStr="+jsonStr);
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            if (jsonObject.has("videos")) {
                Log.i(LOG_TAG,"has videos");
                JSONObject videos = jsonObject.getJSONObject("videos");
                JSONArray results = videos.getJSONArray("results");

                int trailerCnt = results.length();

                for (int i = 0; i < trailerCnt; i++) {
                    JSONObject aTrailer = results.getJSONObject(i);

                    String id = aTrailer.getString("id");
                    String site = aTrailer.getString("site");

                    ATrailer trailer = new ATrailer(id, site);

                    trailerList.add(trailer);

                }
            }

            }catch(JSONException e){
                Log.e(LOG_TAG, "getTrailerListFromJsonStr json exception:" + e.getMessage());
            }

        return trailerList;
    }

    public static List<AReview> getReviewListFromJsonStr(String jsonStr){
        List<AReview> reviewList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONObject reviews = jsonObject.getJSONObject("reviews");
            JSONArray results = reviews.getJSONArray("results");
            int reviewCnt = results.length();

            for (int i = 0; i < reviewCnt; i ++){
                JSONObject aReview = results.getJSONObject(i);
                String author = aReview.getString("author");
                String content = aReview.getString("content");

                AReview review = new AReview(author,content);
                reviewList.add(review);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "getReviewListFromJsonStr json exception:" + e.getMessage());
        }

        return reviewList;

    }



}
