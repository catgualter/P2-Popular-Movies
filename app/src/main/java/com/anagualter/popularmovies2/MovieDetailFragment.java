package com.anagualter.popularmovies2;


import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anagualter.popularmovies2.data.MovieContract;
import com.anagualter.popularmovies2.model.MovieTrailers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class MovieDetailFragment extends Fragment {
    private static int index;

    private View movieDetailView;
    private int movieId;
    private String backdropPath;
    private String posterPath;
    private String movieTitle;
    private String movieReleaseDate;
    private String movieOverview;
    private String movieRating;
    private Toast mToast;
    private OkHttpClient client;
    private Uri url;
    private Response response;
    private ExpandableHeightGridView movieTrailersGridView;

    private ArrayList<MovieTrailers> movieTrailersArrayList;
    private MovieTrailers firstTrailer;
    private MoviesTrailersGridViewAdapter trailerAdapter;
    private FloatingActionButton faveButton;
    private MovieListActivity movieListActivity;
    private ShareActionProvider mShareActionProvider;
    private Intent mShareIntent;
    public MovieDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieId = getArguments().getInt(Constants.Api.MOVIE_ID);
//            backdropPath = getArguments().getString(Constants.Api.MOVIE_BACKDROP_PATH);
            posterPath = getArguments().getString(Constants.Api.MOVIE_POSTER_PATH);
            movieTitle = getArguments().getString(Constants.Api.MOVIE_TITLE);
            movieReleaseDate = getArguments().getString(Constants.Api.MOVIE_RELEASE_DATE);
            movieOverview = getArguments().getString(Constants.Api.MOVIE_OVERVIEW);
            movieRating = getArguments().getString(Constants.Api.MOVIE_VOTE_AVERAGE);
        }
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (getActivity().findViewById(R.id.movie_detail_container_two_pane) != null) {
            movieListActivity = (MovieListActivity) getActivity();
        }

        movieDetailView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        movieTrailersGridView = (ExpandableHeightGridView) movieDetailView.findViewById(R.id.movieTrailerGrid);

        movieTrailersGridView.setExpanded(true);

        ImageView backdrop = (ImageView) movieDetailView.findViewById(R.id.backdrop);
        ImageView poster = (ImageView) movieDetailView.findViewById(R.id.moviePoster);

        TextView title = (TextView) movieDetailView.findViewById(R.id.title);
        TextView readReviews = (TextView) movieDetailView.findViewById(R.id.read_reviews);
        TextView releaseDate = (TextView) movieDetailView.findViewById(R.id.release_date);
        TextView overview = (TextView) movieDetailView.findViewById(R.id.overview);
        TextView ratings = (TextView) movieDetailView.findViewById(R.id.rating);

       faveButton = (FloatingActionButton) movieDetailView.findViewById(R.id.faveButton);

        title.setText(movieTitle);

        if(movieReleaseDate.isEmpty() || movieReleaseDate.equals("null")){
            releaseDate.setText(getResources().getString(R.string.release_date_label) +" " + "Not Found");
        } else{
            releaseDate.setText(getResources().getString(R.string.release_date_label) +" " + movieReleaseDate);
        }

        if(movieOverview.isEmpty() || movieOverview.equals("null")) {
            overview.setText(getResources().getString(R.string.no_overview));
        } else{
            overview.setText(movieOverview);
        }

        ratings.setText(movieRating);

//        Picasso.with(getActivity())
//                .load(backdropPath)
//                .noFade()
//                .into(backdrop);


        Picasso.with(getActivity())
                .load(posterPath)
                .placeholder(R.color.primary)
                .noFade()
                .into(poster);

        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... params) {
                return MovieUtility.isFavorited(getActivity(), movieId);
            }
            @Override
            protected void onPostExecute(Integer isFavorited) {
                faveButton.setImageDrawable(isFavorited == 1 ?
                        getResources().getDrawable(R.drawable.ic_star_white_48dp):
                        getResources().getDrawable( R.drawable.ic_star_border_white_48dp));
            }
        }.execute();

        MovieTrailerAsyncTask moviesAsyncTask  = new MovieTrailerAsyncTask();
        moviesAsyncTask.execute(movieId);


        faveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMovieFavorite isMovieFavorite  = new isMovieFavorite();
                isMovieFavorite.execute();
            }
        });

        readReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle reviewBundle = new Bundle();
                reviewBundle.putInt(Constants.Api.MOVIE_ID, movieId);
                MovieReviewFragment fragment = new MovieReviewFragment();
                fragment.setArguments(reviewBundle);

                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.view_slide_in_up, 0, 0, R.anim.view_slide_out_up)
                        .add(R.id.fragment_movie_detail, fragment)
                        .addToBackStack(null)
                        .commit();

            }
        });
        return movieDetailView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.share_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(mShareIntent);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        shareTrailer();

        return super.onOptionsItemSelected(item);
    }

    private class isMovieFavorite extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            return MovieUtility.isFavorited(getActivity(),movieId);
        }

        @Override
        protected void onPostExecute(Integer isFavorited) {
            if (isFavorited == 1) {
                new AsyncTask<Void, Void, Integer>() {
                    @Override
                    protected Integer doInBackground(Void... params) {
                        return getActivity().getContentResolver().delete(
                                MovieContract.MovieEntry.CONTENT_URI,
                                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                                new String[]{Integer.toString(movieId)}
                        );
                    }
                    @Override
                    protected void onPostExecute(Integer rowsDeleted) {
                        faveButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_border_white_48dp));
                        if (mToast != null) {
                            mToast.cancel();
                        }
                        mToast = Toast.makeText(getActivity(), getString(R.string.movie_removed_from_fave), Toast.LENGTH_SHORT);
                        mToast.show();
                    }
                }.execute();
            }
            else {
                new AsyncTask<Void, Void, Uri>() {
                    @Override
                    protected Uri doInBackground(Void... params) {
                        ContentValues values = new ContentValues();

                        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
                        values.put(MovieContract.MovieEntry.COLUMN_TITLE, movieTitle);
                        values.put(MovieContract.MovieEntry.COLUMN_POSTER, posterPath);
                        values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movieOverview);
                        values.put(MovieContract.MovieEntry.COLUMN_RATING, movieRating);
                        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movieReleaseDate);
                        Log.d("ContentValues", "dd" + values);
                        return getActivity().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI,
                                values);
                    }
                    @Override
                    protected void onPostExecute(Uri returnUri) {
                        faveButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_white_48dp));
                        if (mToast != null) {
                            mToast.cancel();
                        }
                        mToast = Toast.makeText(getActivity(), getString(R.string.movie_added_to_fave), Toast.LENGTH_SHORT);
                        mToast.show();

                    }
                }.execute();
            }
            if (getActivity().findViewById(R.id.movie_detail_container_two_pane) != null) {
                movieListActivity.initializeAsyncTask();
            }
        }
    }


    private class  MovieTrailerAsyncTask extends AsyncTask<Integer, Void, ArrayList<MovieTrailers>>{

        @Override
        protected ArrayList<MovieTrailers> doInBackground(Integer... params) {

            client = new OkHttpClient();

            url = new Uri.Builder()
                    .scheme(Constants.Api.SCHEME)
                    .authority(Constants.Api.BASE_API_URL)
                    .appendPath(Constants.Api.API_VERSION)
                    .appendPath("movie")
                    .appendPath(params[0].toString())
                    .appendPath(Constants.Api.API_TRAILERS)
                    .appendQueryParameter(Constants.Api.API_KEY_PARAM, Constants.Api.API_KEY)
                    .build();

            Log.d("URL", "url: " + url);
            Request request = new Request.Builder()
                    .url(url.toString())
                    .build();
            String responseString;
            JSONObject responseJSON = null;
            movieTrailersArrayList = new ArrayList<>();

            try {
                response = client.newCall(request).execute();
                Log.d("OKHTTP", "response: " + response);
                if (response.isSuccessful())
                    try {
                        responseString = response.body().string();
                        responseJSON = new JSONObject(responseString);

                        JSONArray responseArray = responseJSON.getJSONArray("results");
                        for (int i = 0; i < responseArray.length(); i++) {
                            JSONObject trailersObject = responseArray.getJSONObject(i);
                            Log.d("URL","JSONObject: " + trailersObject);

                            MovieTrailers trailers = new MovieTrailers();

                            trailers.setId(trailersObject.getString(Constants.Api.TRAILER_ID));
                            trailers.setKey(trailersObject.getString(Constants.Api.TRAILER_KEY));
                            trailers.setName(trailersObject.getString(Constants.Api.TRAILER_NAME));
                            trailers.setSite(trailersObject.getString(Constants.Api.TRAILER_SITE));
                            movieTrailersArrayList.add(trailers);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Log.d("OKHTTP", "Catch: " + "no connection");
                e.printStackTrace();
            }

            Log.d("URL","data: " + movieTrailersArrayList);
            return movieTrailersArrayList;
        }

        @Override
        protected void onPostExecute(final ArrayList<MovieTrailers> movieTrailers) {

            if(response != null) {
                firstTrailer = movieTrailers.get(0);

                trailerAdapter = new MoviesTrailersGridViewAdapter(LayoutInflater.from(getActivity()), movieTrailers);
                movieTrailersGridView.setAdapter(trailerAdapter);
                movieTrailersGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MovieTrailers trailer = movieTrailers.get(position);
                        Log.d("TRAILER", "clicked: " + trailer.getKey());

                        Uri youtubeURI;

                        youtubeURI = new Uri.Builder()
                                .scheme(Constants.Api.SCHEME)
                                .authority(Constants.Api.BASE_YOUTUBE)
                                .appendPath("watch")
                                .appendQueryParameter("v", trailer.getKey())
                                .build();

                        Intent intent = new Intent(Intent.ACTION_VIEW, youtubeURI);
                        startActivity(intent);

                    }
                });
            }
        }
    }

    private void shareTrailer() {
        mShareIntent = new Intent();
        mShareIntent.setAction(android.content.Intent.ACTION_SEND);
        mShareIntent.setType("text/plain");

        if(response !=null){
            Uri shareFirstTrailer;
            shareFirstTrailer = new Uri.Builder()
                    .scheme(Constants.Api.SCHEME)
                    .authority(Constants.Api.BASE_YOUTUBE)
                    .appendPath("watch")
                    .appendQueryParameter("v", firstTrailer.getKey())
                    .build();
            mShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Check out this trailer for " +movieTitle +"!!: " + shareFirstTrailer.toString());
        } else{
            mShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Not connected right now but check out the trailer for " +movieTitle +"!!");
        }

        startActivity(Intent.createChooser(mShareIntent, "Share via"));
    }
}