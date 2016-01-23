package com.anagualter.popularmovies2;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;



public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        if (savedInstanceState == null) {
            Bundle movieBundle = new Bundle();
            movieBundle.putInt(Constants.Api.MOVIE_ID, getIntent().getIntExtra(Constants.Api.MOVIE_ID,0));
            movieBundle.putString(Constants.Api.MOVIE_TITLE, getIntent().getStringExtra(Constants.Api.MOVIE_TITLE));
            movieBundle.putString(Constants.Api.MOVIE_OVERVIEW, getIntent().getStringExtra(Constants.Api.MOVIE_OVERVIEW));
            movieBundle.putString(Constants.Api.MOVIE_RELEASE_DATE, getIntent().getStringExtra(Constants.Api.MOVIE_RELEASE_DATE));
            movieBundle.putString(Constants.Api.MOVIE_BACKDROP_PATH, getIntent().getStringExtra(Constants.Api.MOVIE_BACKDROP_PATH));
            movieBundle.putString(Constants.Api.MOVIE_POSTER_PATH, getIntent().getStringExtra(Constants.Api.MOVIE_POSTER_PATH));
            movieBundle.putString(Constants.Api.MOVIE_VOTE_AVERAGE, String.valueOf(getIntent().getStringExtra(Constants.Api.MOVIE_VOTE_AVERAGE)));


            MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
            movieDetailFragment.setArguments(movieBundle);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, movieDetailFragment, "movieDetail")
                    .commit();
        }
    }



    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);

    }
}
