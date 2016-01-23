package com.anagualter.popularmovies2;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.anagualter.popularmovies2.model.MovieReviews;
import java.util.ArrayList;




public class MovieReviewsListViewAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<MovieReviews> reviewList;

    public MovieReviewsListViewAdapter(LayoutInflater inflater, ArrayList<MovieReviews> reviewList) {
        this.inflater= inflater;
        this.reviewList = reviewList;
    }

    @Override
    public int getCount() {
        return  reviewList.size();
    }

    @Override
    public Object getItem(int position) {
        return reviewList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    class ViewHolder {
        TextView review_author;
        TextView review_content;
        TextView review_url;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieReviews movieReviews = reviewList.get(position);

        ViewHolder holder;
        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.review_item, null);

            holder = new ViewHolder();
            holder.review_author = (TextView) convertView.findViewById(R.id.review_author);
            holder.review_content = (TextView) convertView.findViewById(R.id.review_content);
            holder.review_url = (TextView) convertView.findViewById(R.id.review_url);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.review_author.setText(movieReviews.getAuthor());
        holder.review_content.setText(movieReviews.getContent());
        holder.review_url.setText(movieReviews.getUrl());

        return convertView;
    }
}
