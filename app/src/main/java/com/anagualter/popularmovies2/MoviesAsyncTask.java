package com.anagualter.popularmovies2;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.anagualter.popularmovies2.data.MovieContract;
import com.anagualter.popularmovies2.data.MovieProvider;
import com.anagualter.popularmovies2.model.Movies;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class MoviesAsyncTask extends AsyncTask<String, Void, ArrayList<Movies>> {
    public AsyncResponse delegate;
    private Context context;
    private ArrayList<Movies> moviesArrayList;
    private  Response response;
    private Request request;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE
    };

    private Cursor favoriteCursor;
    private  String sort_key;
    public MoviesAsyncTask(Context context) {
        this.context = context;
    }


    @Override
    protected ArrayList<Movies> doInBackground(String... params) {

        OkHttpClient client = new OkHttpClient();
        Uri url;

        moviesArrayList= new ArrayList<>();
        sort_key = params[0];


        if(sort_key.equals(Constants.Api.KEY_SORT_POPULAR)){
            url = new Uri.Builder()
                    .scheme(Constants.Api.SCHEME)
                    .authority(Constants.Api.BASE_API_URL)
                    .path(Constants.Api.API_VERSION)
                    .appendEncodedPath(Constants.Api.API_POPULAR_MOVIES)
                    .appendQueryParameter(Constants.Api.API_KEY_PARAM, Constants.Api.API_KEY)
                    .build();

            request = new Request.Builder()
                    .url(url.toString())
                    .build();

        } else if(sort_key.equals(Constants.Api.KEY_SORT_HIGHEST_RATED)){
            url = new Uri.Builder()
                    .scheme(Constants.Api.SCHEME)
                    .authority(Constants.Api.BASE_API_URL)
                    .path(Constants.Api.API_VERSION)
                    .appendEncodedPath(Constants.Api.API_HIGHEST_RATED)
                    .appendQueryParameter(Constants.Api.API_SORT_BY_PARAM, Constants.Api.API_SORT_BY_HIGHEST)
                    .appendQueryParameter(Constants.Api.API_KEY_PARAM, Constants.Api.API_KEY)
                    .build();
            request = new Request.Builder()
                    .url(url.toString())
                    .build();

        } else {
            favoriteCursor = context.getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    MOVIE_COLUMNS,
                    null,
                    null,
                    null
            );
        }


        if(params[0].equals(Constants.Api.KEY_SORT_FAVORITE)){

            fetchFavoriteData(favoriteCursor);

        } else{

            String responseString;
            JSONObject responseJSON = null;


            try {
                response = client.newCall(request).execute();
                Log.d("OKHTTP", "response: " + response);
                if (response.isSuccessful()) {
                    try {
                        responseString = response.body().string();
                        responseJSON = new JSONObject(responseString);

                        JSONArray responseArray = responseJSON.getJSONArray("results");
                        for (int i = 0; i < responseArray.length(); i++) {
                            JSONObject movieObject = responseArray.getJSONObject(i);

                            Movies movies = new Movies();

                            movies.setId(movieObject.getInt(Constants.Api.MOVIE_ID));
                            movies.setTitle(movieObject.getString(Constants.Api.MOVIE_TITLE));
                            movies.setOverview(movieObject.getString(Constants.Api.MOVIE_OVERVIEW));
                            movies.setPosterPath(getMoviePoster(movieObject.getString(Constants.Api.MOVIE_POSTER_PATH)));
                            movies.setBackdropPath(getMovieBackdrop(movieObject.getString(Constants.Api.MOVIE_BACKDROP_PATH)));
                            movies.setVoteAverage(movieObject.getDouble(Constants.Api.MOVIE_VOTE_AVERAGE));
                            movies.setReleaseDate(movieObject.getString(Constants.Api.MOVIE_RELEASE_DATE));
                            moviesArrayList.add(movies);
                        }


                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                Log.d("OKHTTP", "Catch: " + "no connection");
                e.printStackTrace();
            }

        }

        return moviesArrayList;
    }



    @Override
    protected void onPostExecute(ArrayList<Movies> movieList) {
        Log.d("TAG", "movie count: " + movieList.size());
        if(response != null) {
            delegate.processFinish(movieList);
        } else{
            if(!sort_key.equals(Constants.Api.KEY_SORT_FAVORITE)) {
                Toast.makeText(context,R.string.no_connection,Toast.LENGTH_LONG).show();
            } else{
                if(movieList.size() == 0){
                    Toast.makeText(context,R.string.no_favorites,Toast.LENGTH_LONG).show();
                }
            }

            delegate.processFinish(movieList);
        }
    }

    public String getMoviePoster(String path) {
        Uri url = new Uri.Builder()
                .scheme(Constants.Api.SCHEME)
                .authority(Constants.Api.BASE_IMAGE_URL)
                .appendPath("t")
                .appendPath("p")
                .appendPath(Constants.Api.IMAGE_DEFAULT_SIZE)
                .appendEncodedPath(path)
                .build();
         return url.toString();
    }

    public String getMovieBackdrop(String path) {
        Uri url = new Uri.Builder()
                .scheme(Constants.Api.SCHEME)
                .authority(Constants.Api.BASE_IMAGE_URL)
                .appendPath("t")
                .appendPath("p")
                .appendPath(Constants.Api.IMAGE_BACKDROP_SIZE)
                .appendEncodedPath(path)
                .build();
        return url.toString();
    }

    public interface AsyncResponse {
        public void processFinish(ArrayList<Movies> output);
    }


    private void fetchFavoriteData (Cursor cursor){
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Movies movies = new Movies();
                movies.setId(cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)));
                movies.setTitle(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
                movies.setOverview(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)));
                movies.setPosterPath(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER)));
                movies.setVoteAverage(cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING)));
                movies.setReleaseDate(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE)));
                moviesArrayList.add(movies);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}
