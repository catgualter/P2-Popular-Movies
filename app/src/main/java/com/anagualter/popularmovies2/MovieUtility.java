package com.anagualter.popularmovies2;

import android.content.Context;
import android.database.Cursor;

import com.anagualter.popularmovies2.data.MovieContract;

public class MovieUtility {

    public static int isFavorited(Context context, int id) {
        int numRows = 0;
        Cursor cursor = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                new String[] { Integer.toString(id) },
                null
        );
        if(cursor != null ){
            numRows = cursor.getCount();
            cursor.close();
        }
        return numRows;
    }
}
