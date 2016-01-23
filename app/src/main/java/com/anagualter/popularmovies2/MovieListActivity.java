package com.anagualter.popularmovies2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.anagualter.popularmovies2.data.MovieContract;
import com.anagualter.popularmovies2.model.Movies;

import java.util.ArrayList;


public class MovieListActivity extends AppCompatActivity implements MoviesAsyncTask.AsyncResponse {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private static int index;
    private GridView movieGridView;
    private ArrayList<Movies> movies;
    private MovieGridViewAdapter mAdapter;
    private MoviesAsyncTask moviesAsyncTask;
    private String key;

    private SharedPreferences sortPrefs;
    private SharedPreferences.Editor editor;
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        movieGridView = (GridView) findViewById(R.id.movieGridView);
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
        sortPrefs = getSharedPreferences("sort_prefs", 0);
        editor = sortPrefs.edit();
        if(sortPrefs.contains("sort_by")){
            key = sortPrefs.getString("sort_by", null);
        } else{
            key = Constants.Api.KEY_SORT_POPULAR;
        }

        if(savedInstanceState == null) {
            Log.i("Cycle", "savedInstanceState null onCreate");
            initializeAsyncTask();
        }
        else {
            Log.i("Cycle", "savedInstanceState onCreate");
            movies =  savedInstanceState.getParcelableArrayList("movie_parcel");
            processFinish(movies);
        }
        if (findViewById(R.id.movie_detail_container_two_pane) != null) {
            mTwoPane = true;
        }



        // TODO: If exposing deep links into your app, handle intents here.
    }


    public void processFinish(ArrayList<Movies> moviesList){

        movies = moviesList;
        mAdapter = new MovieGridViewAdapter(LayoutInflater.from(this),movies);
        movieGridView.setAdapter(mAdapter);

        movieGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movies movie = movies.get(position);
                if (mTwoPane) {

                    Bundle movieBundle = new Bundle();
                    movieBundle.putInt(Constants.Api.MOVIE_ID, movie.getId());
                    movieBundle.putString(Constants.Api.MOVIE_TITLE, movie.getTitle());
                    movieBundle.putString(Constants.Api.MOVIE_OVERVIEW, movie.getOverview());
                    movieBundle.putString(Constants.Api.MOVIE_RELEASE_DATE, movie.getReleaseDate());
                    movieBundle.putString(Constants.Api.MOVIE_BACKDROP_PATH, movie.getBackdropPath());
                    movieBundle.putString(Constants.Api.MOVIE_POSTER_PATH, movie.getPosterPath());
                    movieBundle.putString(Constants.Api.MOVIE_VOTE_AVERAGE, String.valueOf(movie.getVoteAverage()));
                    MovieDetailFragment fragment = new MovieDetailFragment();
                    fragment.setArguments(movieBundle);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_container_two_pane, fragment)
                            .commit();

                } else {
                    Intent movieIntent = new Intent(MovieListActivity.this, MovieDetailActivity.class);
                    movieIntent.putExtra(Constants.Api.MOVIE_ID, movie.getId());
                    movieIntent.putExtra(Constants.Api.MOVIE_TITLE, movie.getTitle());
                    movieIntent.putExtra(Constants.Api.MOVIE_OVERVIEW, movie.getOverview());
                    movieIntent.putExtra(Constants.Api.MOVIE_RELEASE_DATE, movie.getReleaseDate());
                    movieIntent.putExtra(Constants.Api.MOVIE_BACKDROP_PATH, movie.getBackdropPath());
                    movieIntent.putExtra(Constants.Api.MOVIE_POSTER_PATH, movie.getPosterPath());
                    movieIntent.putExtra(Constants.Api.MOVIE_VOTE_AVERAGE, String.valueOf(movie.getVoteAverage()));
                    index = movieGridView.getFirstVisiblePosition();
                    startActivity(movieIntent);
                }

            }
        });


    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i("Cycle", "onSaveInstanceState");
        outState.putParcelableArrayList("movie_parcel", movies);
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i("savedInstance Async", "have");
        if(savedInstanceState !=null) {
            movies =  savedInstanceState.getParcelableArrayList("movie_parcel");
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort_popular) {
            key = Constants.Api.KEY_SORT_POPULAR;
            editor.putString("sort_by",  Constants.Api.KEY_SORT_POPULAR);
            editor.commit();
            initializeAsyncTask();

            return true;

        } else if(id == R.id.sort_highest){
            key = Constants.Api.KEY_SORT_HIGHEST_RATED;
            editor.putString("sort_by",  Constants.Api.KEY_SORT_HIGHEST_RATED);
            editor.commit();
            initializeAsyncTask();

            return true;

        } else if(id == R.id.view_favorites){
            key = Constants.Api.KEY_SORT_FAVORITE;
            editor.putString("sort_by",  Constants.Api.KEY_SORT_FAVORITE);
            editor.commit();
            initializeAsyncTask();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Cycle", "onResume");
        movieGridView.setSelection(index);

    }

    @Override
    protected void onPause() {
        super.onPause();
        movieGridView.setSelection(index);
        Log.i("Cycle", "onPause");
        overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);

    }

    public void initializeAsyncTask() {
        moviesAsyncTask  = new MoviesAsyncTask(this);
        moviesAsyncTask.execute(key);
        moviesAsyncTask.delegate = this;
    }





}
