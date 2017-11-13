package com.sibangstudio.popularmovie.data;

import android.provider.BaseColumns;

/**
 * Created by ibti on 11/13/17.
 */

public class MovieContract  {


    /*private String vote_average;
    private String title;
    private String popularity;
    private String poster_path;
    private String original_title;
    private String backdrop_path;
    private String overview;
    private String release_date;*/

    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_POASTER_PATH = "poster_path";
        public static final String COLUMN_ORIGINAL_TITILE = "original_title";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_TIMESTAMP = "timestamp";


    }
}
