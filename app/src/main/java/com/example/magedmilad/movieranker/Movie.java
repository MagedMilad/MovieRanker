package com.example.magedmilad.movieranker;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by magedmilad on 12/3/15.
 */
public class Movie implements Parcelable {

    String original_title, overview, release_date, poster_path;
    double popularity,vote_average;
    int vote_count , id;
    ArrayList<Trailer> trailers;
    ArrayList<Review> reviews;


    public Movie(int id ,String original_title, String overview, String release_date, String poster_path, double popularity, double vote_average, int vote_count) {
        this.id= id;
        this.original_title = original_title;
        this.overview = overview;
        this.release_date = release_date;
        this.poster_path = poster_path;
        this.popularity = popularity;
        this.vote_average = vote_average;
        this.vote_count = vote_count;
        trailers = new ArrayList<Trailer>();
        reviews = new ArrayList<Review>();
    }

    protected Movie(Parcel in) {
        original_title = in.readString();
        overview = in.readString();
        release_date = in.readString();
        poster_path = in.readString();
        popularity = in.readDouble();
        vote_average = in.readDouble();
        vote_count = in.readInt();
        id = in.readInt();
        trailers = new ArrayList<Trailer>();
        reviews = new ArrayList<Review>();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public double getPopularity() {
        return popularity;
    }

    public double getVote_average() {
        return vote_average;
    }

    public int getVote_count() {
        return vote_count;
    }

    public int getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(original_title);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(poster_path);
        dest.writeDouble(popularity);
        dest.writeDouble(vote_average);
        dest.writeInt(vote_count);
        dest.writeInt(id);
    }



}







