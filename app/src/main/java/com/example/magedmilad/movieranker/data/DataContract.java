package com.example.magedmilad.movieranker.data;

import android.provider.BaseColumns;


public class DataContract {


    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_MOVIE_NAME = "movie_name";
        public static final String COLUMN_OVERVIEW_NAME = "overview";
        public static final String COLUMN_RELEASE_DATE_NAME = "release_date";
        public static final String COLUMN_POSTER_PATH_NAME = "poster_path";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_AVERAGE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";

    }


}