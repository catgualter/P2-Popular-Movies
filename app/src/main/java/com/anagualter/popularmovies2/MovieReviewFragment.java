package com.anagualter.popularmovies2;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.anagualter.popularmovies2.model.MovieReviews;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class MovieReviewFragment extends Fragment {

    
    private View movieReviewView;
    private int movieId;
    private ListView movieReviewsListView;
    public MovieReviewFragment() {
        // Required empty public constructor
    }


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieId = getArguments().getInt(Constants.Api.MOVIE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        movieReviewView = inflater.inflate(R.layout.fragment_fragment_movie_reviews, container, false);
        movieReviewsListView = (ListView) movieReviewView.findViewById(R.id.movieReviewList);
        TextView closeReviews = (TextView) movieReviewView.findViewById(R.id.closeReviews);

        closeReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        MovieReviewAsyncTask movieReviewAsyncTask= new MovieReviewAsyncTask();
        movieReviewAsyncTask.execute(movieId);

        return movieReviewView;
    }


    private class MovieReviewAsyncTask extends AsyncTask<Integer, Void, ArrayList<MovieReviews>> {

        @Override
        protected ArrayList<MovieReviews> doInBackground(Integer... params) {
            OkHttpClient client = new OkHttpClient();
            Uri url;
            url = new Uri.Builder()
                    .scheme(Constants.Api.SCHEME)
                    .authority(Constants.Api.BASE_API_URL)
                    .appendPath(Constants.Api.API_VERSION)
                    .appendPath("movie")
                    .appendPath(params[0].toString())
                    .appendPath(Constants.Api.API_REVIEWS)
                    .appendQueryParameter(Constants.Api.API_KEY_PARAM, Constants.Api.API_KEY)
                    .build();
            Request request = new Request.Builder()
                    .url(url.toString())
                    .build();
            String responseString;
            JSONObject responseJSON = null;

            ArrayList<MovieReviews> movieReviewsArrayList = new ArrayList<>();

            try {
                Response response = client.newCall(request).execute();
                Log.d("OKHTTP", "response: " + response);
                if (response.isSuccessful())
                    try {
                        responseString = response.body().string();
                        responseJSON = new JSONObject(responseString);



                        JSONArray responseArray = responseJSON.getJSONArray("results");
                        for (int i = 0; i < responseArray.length(); i++) {
                            JSONObject reviewsObject = responseArray.getJSONObject(i);
                            Log.d("URL","JSONObject: " + reviewsObject);

                            MovieReviews reviews = new MovieReviews();

                            reviews.setId(reviewsObject.getString(Constants.Api.REVIEW_ID));
                            reviews.setAuthor(reviewsObject.getString(Constants.Api.REVIEW_AUTHOR));
                            reviews.setContent(reviewsObject.getString(Constants.Api.REVIEW_CONTENT));
                            reviews.setUrl(reviewsObject.getString(Constants.Api.REVIEW_URL));
                            movieReviewsArrayList.add(reviews);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            } catch (IOException e) {
                Log.d("OKHTTP", "Catch: " + "no connection");
                e.printStackTrace();
            }

            return movieReviewsArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieReviews> movieReviews) {
            MovieReviewsListViewAdapter  reviewAdapter = new MovieReviewsListViewAdapter(LayoutInflater.from(getActivity()), movieReviews);
            movieReviewsListView.setAdapter(reviewAdapter);

        }

    }

}
