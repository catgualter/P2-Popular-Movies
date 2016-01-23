package com.anagualter.popularmovies2.model;


import android.os.Parcel;
import android.os.Parcelable;


public class Movies implements Parcelable {
    int id;
    String backdropPath;
    String posterPath;
    String title;
    String overview;
    String releaseDate;
    double voteAverage;

    public Movies() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    private Movies(Parcel in) {
        id = in.readInt();
        backdropPath = in.readString();
        posterPath = in.readString();
        title = in.readString();
        overview = in.readString();
        releaseDate = in.readString();
        voteAverage =  in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(backdropPath);
        dest.writeString(posterPath);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeDouble(voteAverage);
    }

    public static final Creator<Movies> CREATOR = new Creator<Movies>() {
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }

        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };
}
