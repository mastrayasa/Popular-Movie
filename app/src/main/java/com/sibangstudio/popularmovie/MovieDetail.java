package com.sibangstudio.popularmovie;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ParseException;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sibangstudio.popularmovie.adapter.ReviewAdapter;
import com.sibangstudio.popularmovie.adapter.TrailerAdapter;
import com.sibangstudio.popularmovie.provider.MovieContract;
import com.sibangstudio.popularmovie.data.MovieData;
import com.sibangstudio.popularmovie.provider.MovieDbHelper;
import com.sibangstudio.popularmovie.data.ReviewData;
import com.sibangstudio.popularmovie.data.TrailerData;
import com.sibangstudio.popularmovie.helper.MyFunction;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieDetail extends AppCompatActivity implements TrailerAdapter.DirAdapterOnClickHandler, ReviewAdapter.DirAdapterOnClickHandler {


    MovieData movie;

    private SQLiteDatabase mDb;

    private TrailerAdapter mAdapter;
    private RecyclerView mRecyclerView;
    List<TrailerData> TrailerList = new ArrayList<TrailerData>();


    private ReviewAdapter reviewAdapter;
    private RecyclerView mRecyclerViewReview;
    List<ReviewData> reviewList = new ArrayList<ReviewData>();

    private final static String LOG_TAG = MovieDetail.class.getSimpleName();

    TextView txtErrorTrailer;
    TextView txtErrorReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setTitle(getResources().getString(R.string.detail_title));

        Intent intent = getIntent();
        movie = (MovieData) intent.getSerializableExtra("movie");



        txtErrorTrailer = (TextView) findViewById(R.id.txtErrorTrailer);
        txtErrorReview = (TextView) findViewById(R.id.txtErrorReview);


        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        TextView txtReleaseDate = (TextView) findViewById(R.id.txtReleaseDate);
        TextView txtRating = (TextView) findViewById(R.id.txtRating);
        TextView txtOverview = (TextView) findViewById(R.id.txtOverview);
        ImageView imgPoster = (ImageView) findViewById(R.id.imgPoster);
        ImageView imgBackdrop = (ImageView) findViewById(R.id.imgBackdrop);

        txtTitle.setText(movie.getOriginal_title());
        txtReleaseDate.setText(movie.getRelease_date());
        txtRating.setText(movie.getVote_average());
        txtOverview.setText(movie.getOverview());


        String image = "http://image.tmdb.org/t/p/w154" + movie.getPoster_path();
        Picasso.with(this)
                .load(image)
                .placeholder(R.drawable.poster_placeholder)
                .into(imgPoster);

        String image2 = "http://image.tmdb.org/t/p/w500" + movie.getBackdrop_path();
        Picasso.with(this)
                .load(image2)
                .into(imgBackdrop);


        // Create a DB helper (this will create the DB if run for the first time)
        MovieDbHelper dbHelper = new MovieDbHelper(this);

        // Keep a reference to the mDb until paused or killed. Get a writable database
        // because you will be adding restaurant customers
        mDb = dbHelper.getWritableDatabase();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveMovieToFavorite(movie, view);
            }
        });


        createTrailer(movie.getId());

        createReview(movie.getId());
    }

    private void createReview(String id){





        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecyclerViewReview = (RecyclerView) findViewById(R.id.rv_review);



        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);



        mRecyclerViewReview.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerViewReview.setHasFixedSize(true);

        reviewAdapter = new ReviewAdapter(MovieDetail.this, (ReviewAdapter.DirAdapterOnClickHandler) this);
        mRecyclerViewReview.setAdapter(reviewAdapter);

        mRecyclerViewReview.setNestedScrollingEnabled(false);






        String url = "http://api.themoviedb.org/3/movie/" + id + "/reviews?api_key=b5481a85cbb44c13c6c6931834845104";
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        olahDataReview(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    public void olahDataReview(String s) {

        JSONArray jArray = null;
        JSONObject resultRoot = null;
        JSONObject json_data = null;
        JSONObject json_Detail = null;

        try {

            resultRoot = new JSONObject(s);

            jArray = resultRoot.getJSONArray("results");


            // deklarasikan panjang array sejumlah array jarray

            if (jArray.length() > 0) {
                for (int i = 0; i < jArray.length(); i++) {
                    json_data = jArray.getJSONObject(i);

                    ReviewData data = MyFunction.setReviewFromJson(json_data);

                    reviewList.add(data);
                    //Log.e("Add", aha.getTitle());
                }

                reviewAdapter.setDirData(reviewList);
            }else{
                txtErrorReview.setVisibility(View.VISIBLE);
                mRecyclerViewReview.setVisibility(View.GONE);
            }



        } catch (JSONException e1) {
            Toast.makeText(getBaseContext(), "Opsss...", Toast.LENGTH_LONG)
                    .show();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
    }

    private void createTrailer(String id){





        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_trailer);

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
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        GridLayoutManager layoutGrid = new GridLayoutManager(this,2);

        mRecyclerView.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new TrailerAdapter(MovieDetail.this, this);
        mRecyclerView.setAdapter(mAdapter);






        String url = "http://api.themoviedb.org/3/movie/" + id + "/videos?api_key=b5481a85cbb44c13c6c6931834845104";
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        olahDataTrailer(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void olahDataTrailer(String s) {

        JSONArray jArray = null;
        JSONObject resultRoot = null;
        JSONObject json_data = null;
        //JSONObject json_Detail = null;

        try {

            resultRoot = new JSONObject(s);

            jArray = resultRoot.getJSONArray("results");


            // deklarasikan panjang array sejumlah array jarray

            if (jArray.length() > 0) {
                for (int i = 0; i < jArray.length(); i++) {
                    json_data = jArray.getJSONObject(i);

                    TrailerData data = MyFunction.setTrailerFromJson(json_data);

                    TrailerList.add(data);
                    //Log.e("Add", aha.getTitle());
                }

                mAdapter.setDirData(TrailerList);
            }else{
                txtErrorTrailer.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            }



        } catch (JSONException e1) {
            Toast.makeText(getBaseContext(), "Opsss...", Toast.LENGTH_LONG)
                    .show();
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);

        Log.e("a", "a");

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Log.e("a", "a");

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }


        //noinspection SimplifiableIfStatement
        else if (id == R.id.action_share) {
            Log.e("b", "b");
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, movie.getTitle());
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
        }

        return super.onOptionsItemSelected(item);
    }


    private void saveMovieToFavorite(MovieData data, View view) {


        Log.e(LOG_TAG, "Save movie to Favorite");

        Cursor cursor = mDb.query(
                MovieContract.MovieEntry.TABLE_NAME,
                null,
                MovieContract.MovieEntry.COLUMN_ORIGINAL_TITILE + "=?",
                new String[]{data.getOriginal_title()},
                null,
                null,
                null
        );


        if (cursor != null) {

            if (!(cursor.moveToFirst()) || cursor.getCount() == 0) {

                ContentValues cv = new ContentValues();
                cv.put(MovieContract.MovieEntry.COLUMN_ID, data.getId());
                cv.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, data.getVote_average());
                cv.put(MovieContract.MovieEntry.COLUMN_TITLE, data.getTitle());
                cv.put(MovieContract.MovieEntry.COLUMN_POPULARITY, data.getPopularity());
                cv.put(MovieContract.MovieEntry.COLUMN_POASTER_PATH, data.getPoster_path());
                cv.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITILE, data.getOriginal_title());
                cv.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, data.getBackdrop_path());
                cv.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, data.getOverview());
                cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, data.getRelease_date());

                mDb.insert(MovieContract.MovieEntry.TABLE_NAME, null, cv);

                Snackbar.make(view, getResources().getString(R.string.saveToFavorite), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();

            } else {
                Snackbar.make(view, getResources().getString(R.string.alreadyOnFavorite), Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }


        }


    }


    @Override
    public void onClick(TrailerData data) {
        Uri uri = Uri.parse("https://www.youtube.com/watch?v=" + data.getKey());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public void onClick(ReviewData weatherForDay) {

    }
}
