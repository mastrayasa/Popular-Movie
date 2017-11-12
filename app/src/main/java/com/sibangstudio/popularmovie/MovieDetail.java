package com.sibangstudio.popularmovie;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sibangstudio.popularmovie.data.MovieData;
import com.squareup.picasso.Picasso;

public class MovieDetail extends AppCompatActivity {


    MovieData movie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setTitle("Detail Movie");

        Intent intent = getIntent();
        movie = (MovieData) intent.getSerializableExtra("movie");


        TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        TextView txtReleaseDate = (TextView) findViewById(R.id.txtReleaseDate);
        TextView txtRating = (TextView) findViewById(R.id.txtRating);
        TextView txtOverview = (TextView) findViewById(R.id.txtOverview);
        ImageView imgPoster = (ImageView) findViewById(R.id.imgPoster);
        ImageView imgBackdrop = (ImageView) findViewById(R.id.imgBackdrop);

        txtTitle.setText( movie.getOriginal_title() );
        txtReleaseDate.setText(movie.getRelease_date() );
        txtRating.setText(movie.getVote_average());
        txtOverview.setText(movie.getOverview());


        String image = "http://image.tmdb.org/t/p/w154" + movie.getPoster_path();
        Picasso.with(this)
                .load(image)
                .into(imgPoster);

        String image2 = "http://image.tmdb.org/t/p/w500" + movie.getBackdrop_path();
        Picasso.with(this)
                .load(image2)
                .into(imgBackdrop);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);

        Log.e("a","a");

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Log.e("a","a");

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }



        //noinspection SimplifiableIfStatement
        else if (id == R.id.action_share) {
            Log.e("b","b");
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, movie.getTitle() );
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
        }

        return super.onOptionsItemSelected(item);
    }

}
