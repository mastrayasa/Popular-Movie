package com.sibangstudio.popularmovie;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sibangstudio.popularmovie.adapter.MovieAdapter;
import com.sibangstudio.popularmovie.data.MovieData;
import com.sibangstudio.popularmovie.helper.MyFunction;
import com.sibangstudio.popularmovie.provider.MovieContract;
import com.sibangstudio.popularmovie.provider.MovieDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.DirAdapterOnClickHandler, SwipeRefreshLayout.OnRefreshListener ,
        LoaderManager.LoaderCallbacks<Cursor>{



    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    private final static String MODE_POPULAR = "popular";
    private final static String MODE_TOP_RATING = "rating";
    private final static String MODE_FAVORITE = "favorite";

    private static final int TASK_LOADER_ID = 0;

    private Boolean isInitLoader = false;

    /*
     * References to RecyclerView and Adapter to reset the list to its
     * "pretty" state when the reset menu item is clicked.
     */
    private MovieAdapter mAdapter;
    private RecyclerView mRecyclerView;

    SwipeRefreshLayout swipe;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    List<MovieData> dirList = new ArrayList<MovieData>();

    private SQLiteDatabase mDb;

    JSONArray jArray = null;
    JSONObject resultRoot = null;
    JSONObject json_data = null;
    //JSONObject json_Detail = null;

    private int page = 1;

    String mode = MODE_POPULAR;

    GridLayoutManager layoutGrid3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipe.setOnRefreshListener(this);

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_numbers);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
         * circle. We didn't make the rules (or the names of Views), we just follow them.
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        /*
         * A LinearLayoutManager is responsible for measuring and positioning item views within a
         * RecyclerView into a linear list. This means that it can produce either a horizontal or
         * vertical list depending on which parameter you pass in to the LinearLayoutManager
         * constructor. By default, if you don't specify an orientation, you get a vertical list.
         * In our case, we want a vertical list, so we don't need to pass in an orientation flag to
         * the LinearLayoutManager constructor.
         *
         * There are other LayoutManagers available to display your data in uniform grids,
         * staggered grids, and more! See the developer documentation for more details.
         */
        //LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        GridLayoutManager layoutGrid = new GridLayoutManager(this, 2);



        mRecyclerView.setLayoutManager(layoutGrid);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new MovieAdapter(MainActivity.this, this, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);


        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e(LOG_TAG, "Load More " + page);

                loadMovies();

            }
        });


        loadMovies();


        // Create a DB helper (this will create the DB if run for the first time)
        MovieDbHelper dbHelper = new MovieDbHelper(this);

        // Keep a reference to the mDb until paused or killed. Get a writable database
        // because you will be adding restaurant customers
        mDb = dbHelper.getWritableDatabase();

    }


    public void loadMovies() {

        if (mode.equals( MODE_POPULAR )) {
            loadPopularMovie();
        } else if (mode.equals(MODE_TOP_RATING)) {
            loadMovieByRating();
        } else if (mode.equals(MODE_FAVORITE)) {
           loadFavoriteMovies();
        }
    }


    private void loadPopularMovie() {

        mode = MODE_POPULAR;

        //http://api.themoviedb.org/3/movie/popular?api_key=###&page=1
        Uri builtUri = Uri.parse( BuildConfig.TMDB_BASE_URL).buildUpon()
                .appendPath("movie")
                .appendPath("popular")
                .appendQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                .appendQueryParameter("page", String.valueOf(page))
                .build();

        loadMovieData(builtUri.toString());
    }

    private void loadMovieByRating() {

        mode = MODE_TOP_RATING;

        //http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&api_key=###&page=1
        Uri builtUri = Uri.parse(BuildConfig.TMDB_BASE_URL).buildUpon()
                .appendPath("discover")
                .appendPath("movie")
                .appendQueryParameter("sort_by", "vote_average.desc")
                .appendQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                .appendQueryParameter("page", String.valueOf(page))
                .build();

        loadMovieData(builtUri.toString());
    }

    /**
     * This method will get the user's preferred location for weather, and then tell some
     * background method to get the weather data in the background.
     */
    private void loadMovieData(String url) {

        Log.d(LOG_TAG, url);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        olahData(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showErrorMessage(error.getMessage());

            }
        });

        mLoadingIndicator.setVisibility(View.VISIBLE);
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    /**
     * Query the mDb and get all guests from the waitlist table
     *
     * @return Cursor containing the list of guests
     */
    public void loadFavoriteMovies() {

        if( isInitLoader == false){
            getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
            isInitLoader = true;
        }else {
            getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
        }

        /*mode = MODE_FAVORITE;

        showDirDataView();

        Log.e(LOG_TAG, "loadFavoriteMovies");

        Cursor cursor = mDb.query(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                MovieContract.MovieEntry.COLUMN_TIMESTAMP
        );


        if (cursor != null) {
            if (cursor.moveToFirst()) {

                mAdapter.clearData();

                do {


                    MovieData movie = new MovieData();
                    movie.setId(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID)));
                    movie.setVote_average(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
                    movie.setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
                    movie.setPopularity(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POPULARITY)));
                    movie.setPoster_path(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POASTER_PATH)));
                    movie.setOriginal_title(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITILE)));
                    movie.setBackdrop_path(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH)));
                    movie.setOverview(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
                    movie.setRelease_date(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));

                    dirList.add(movie);

                    Log.e(LOG_TAG, "add dir");

                } while (cursor.moveToNext());

                mAdapter.setDirData(dirList);

            } else {
                Log.e(LOG_TAG, "no move");
                showErrorMessage("Anda belum memilih film favorit anda");
            }
            cursor.close();
        } else {
            Log.e(LOG_TAG, "cursor null");
        }*/
    }



    public void loadFavoriteMovies2(Cursor cursor) {

        mode = MODE_FAVORITE;

        showDirDataView();



        if (cursor != null) {
            if (cursor.moveToFirst()) {

                mAdapter.clearData();

                do {


                    MovieData movie = new MovieData();
                    movie.setId(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID)));
                    movie.setVote_average(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)));
                    movie.setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
                    movie.setPopularity(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POPULARITY)));
                    movie.setPoster_path(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POASTER_PATH)));
                    movie.setOriginal_title(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITILE)));
                    movie.setBackdrop_path(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH)));
                    movie.setOverview(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
                    movie.setRelease_date(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));

                    dirList.add(movie);

                    Log.e(LOG_TAG, "add dir");

                } while (cursor.moveToNext());

                mAdapter.setDirData(dirList);

            } else {
                Log.e(LOG_TAG, "no move");
                showErrorMessage("Anda belum memilih film favorit anda");
            }
            cursor.close();
        } else {
            Log.e(LOG_TAG, "cursor null 2");
        }
    }


    public void olahData(String s) {

        showDirDataView();

        try {

            resultRoot = new JSONObject(s);

            jArray = resultRoot.getJSONArray("results");


            // deklarasikan panjang array sejumlah array jarray

            if (jArray.length() > 0) {
                for (int i = 0; i < jArray.length(); i++) {
                    json_data = jArray.getJSONObject(i);

                    MovieData aha = MyFunction.setDariJson(json_data);

                    dirList.add(aha);
                    //Log.e("Add", aha.getTitle());
                }

                mAdapter.setDirData(dirList);

                mAdapter.setLoaded();

                page++;
            }


        } catch (JSONException e1) {
            Toast.makeText(getBaseContext(), "Opsss...", Toast.LENGTH_LONG)
                    .show();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onClick(MovieData data) {

        /*Log.e("Movie",data.getTitle());
        Context context = this;
        Toast.makeText(context, data.getTitle(), Toast.LENGTH_SHORT)
                .show();*/

        Intent ed = new Intent(this, MovieDetail.class);
        ed.putExtra("movie", data);
        startActivity(ed);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.main, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sort_by_popular) {
            if (!mode.equals( MODE_POPULAR )) {
                mode = MODE_POPULAR;
                setTitle(getResources().getString(R.string.app_name));
                mAdapter.clearData();
                page = 1;
                loadMovies();
                return true;
            }
        } else if (id == R.id.action_sort_by_rating) {

            if (!mode.equals(MODE_TOP_RATING)) {
                mode = MODE_TOP_RATING;
                setTitle(getResources().getString(R.string.titleRating));
                mAdapter.clearData();
                page = 1;
                loadMovies();
                return true;
            }

        } else if (id == R.id.action_sort_by_favorite) {

            if (!mode.equals(MODE_FAVORITE)) {
                mode = MODE_FAVORITE;
                setTitle(getResources().getString(R.string.titleFavorite));
                mAdapter.clearData();
                page = 1;
                loadMovies();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRefresh() {
        dirList.clear();
        page = 1;
        swipe.setRefreshing(false);

        loadMovies();

    }


    /**
     * This method will make the View for the weather data visible and
     * hide the error message.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showDirDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);

        mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    /**
     * This method will make the error message visible and hide the weather
     * View.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't
     * need to check whether each view is currently visible or invisible.
     */
    private void showErrorMessage(String msg) {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        mErrorMessageDisplay.setText(msg);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mTaskData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Will implement to load data

                // COMPLETED (5) Query and load all task data in the background; sort by priority
                // [Hint] use a try/catch block to catch any errors in loading data

                try {
                    return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI ,
                            null,
                            null,
                            null,
                            MovieContract.MovieEntry.COLUMN_TIMESTAMP);

                } catch (Exception e) {
                    Log.e(LOG_TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        loadFavoriteMovies2(data);
        Log.e(LOG_TAG, "222222222222222222222222222222222222222222222222");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
