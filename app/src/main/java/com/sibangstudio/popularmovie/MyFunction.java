package com.sibangstudio.popularmovie;

/**
 * Created by wayanmastrayasa on 11/12/17.
 */

import android.net.ParseException;

import com.sibangstudio.popularmovie.data.MovieData;

import org.json.JSONException;
import org.json.JSONObject;


public class MyFunction {

    public static final String TAG_ID = "id";
    public static final String TAG_VOTE_AVERAGE = "vote_average";
    public static final String TAG_POPULARITY = "popularity";
    public static final String TAG_ORIGINAL_TITLE = "original_title";
    public static final String TAG_BACKDROP_PARH = "backdrop_path";
    public static final String TAG_RELEASE_DATE = "release_date";
    public static final String TAG_TITLE = "title";
    public static final String TAG_POSTER = "poster_path";
    public static final String TAG_OVERVIEW = "overview";



    public static MovieData setDariJson(JSONObject data){

        MovieData movie = new MovieData();

        try {
            movie.setId(data.getString(TAG_ID));
            movie.setVote_average(data.getString(TAG_VOTE_AVERAGE));
            movie.setPopularity(data.getString(TAG_POPULARITY));
            movie.setOriginal_title(data.getString(TAG_ORIGINAL_TITLE));
            movie.setBackdrop_path(data.getString(TAG_BACKDROP_PARH));
            movie.setRelease_date(data.getString(TAG_RELEASE_DATE));
            movie.setTitle(data.getString(TAG_TITLE));
            movie.setPoster_path(data.getString(TAG_POSTER));
            movie.setOverview(data.getString(TAG_OVERVIEW));


        } catch (JSONException e1) {

        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        return movie;
    }
}
