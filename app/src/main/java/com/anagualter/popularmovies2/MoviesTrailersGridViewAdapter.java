package com.anagualter.popularmovies2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.anagualter.popularmovies2.model.MovieTrailers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesTrailersGridViewAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context mContext;
    private ArrayList<MovieTrailers> trailerList;

    public MoviesTrailersGridViewAdapter(LayoutInflater inflater, ArrayList<MovieTrailers> trailerList) {
        this.inflater= inflater;
        this.trailerList = trailerList;
    }
    @Override
    public int getCount() {
        return trailerList.size();
    }

    @Override
    public Object getItem(int position) {
        return trailerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        ImageView trailerImage;
        TextView trailerName;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MovieTrailers trailer = trailerList.get(position);
        ViewHolder holder;

        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.trailer_item, null);

            holder = new ViewHolder();
            holder.trailerImage = (ImageView) convertView.findViewById(R.id.trailer_image);
            holder.trailerName = (TextView) convertView.findViewById(R.id.trailer_name);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        Picasso.with(parent.getContext())
                .load("http://img.youtube.com/vi/"+trailer.getKey()+"/hqdefault.jpg")
                .placeholder(R.color.primary)
                .noFade()
                .into(holder.trailerImage);

        holder.trailerName.setText(trailer.getName());

        return convertView;
    }

}
