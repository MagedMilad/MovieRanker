package com.example.magedmilad.movieranker.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.example.magedmilad.movieranker.Movie;
import com.example.magedmilad.movieranker.Trailer;
import com.example.magedmilad.movieranker.data.DataContract.*;

public class DataDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    static final String DATABASE_NAME = "movie.db";

    public DataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY," +
                MovieEntry.COLUMN_MOVIE_NAME + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW_NAME + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE_NAME + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_PATH_NAME + " TEXT NOT NULL," +
                MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MovieEntry.COLUMN_AVERAGE + " REAL NOT NULL, " +
                MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    public void insert(Movie m) {
        ContentValues MovieValues = new ContentValues();
        MovieValues.put(MovieEntry._ID, m.getId());
        MovieValues.put(MovieEntry.COLUMN_MOVIE_NAME, m.getOriginal_title());
        MovieValues.put(MovieEntry.COLUMN_OVERVIEW_NAME, m.getOverview());
        MovieValues.put(MovieEntry.COLUMN_RELEASE_DATE_NAME, m.getRelease_date());
        MovieValues.put(MovieEntry.COLUMN_POSTER_PATH_NAME, m.getPoster_path());
        MovieValues.put(MovieEntry.COLUMN_POPULARITY, m.getPopularity());
        MovieValues.put(MovieEntry.COLUMN_AVERAGE, m.getVote_average());
        MovieValues.put(MovieEntry.COLUMN_VOTE_COUNT, m.getVote_count());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(MovieEntry.TABLE_NAME, null, MovieValues);
        db.close();
    }

    public boolean is_favorite(int id) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(MovieEntry.TABLE_NAME, null, MovieEntry._ID + " = ?", new String[]{Integer.toString(id)}, null, null, null);
        boolean ret = c.moveToFirst();
        db.close();
        return ret;
    }

    public void delete(int id){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(MovieEntry.TABLE_NAME, MovieEntry._ID + " = ?", new String[]{Integer.toString(id)});
        db.close();
        return ;
    }

    public Movie[] get_favorite() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(MovieEntry.TABLE_NAME, null, null, null, null, null, null);
        c.moveToFirst();
        Movie[] m = new Movie[c.getCount()];
        for (int i = 0; i < c.getCount(); i++, c.moveToNext()) {
            m[i] = new Movie(c.getInt(c.getColumnIndex(MovieEntry._ID)),
                    c.getString(c.getColumnIndex(MovieEntry.COLUMN_MOVIE_NAME)),
                    c.getString(c.getColumnIndex(MovieEntry.COLUMN_OVERVIEW_NAME)),
                    c.getString(c.getColumnIndex(MovieEntry.COLUMN_RELEASE_DATE_NAME)),
                    c.getString(c.getColumnIndex(MovieEntry.COLUMN_POSTER_PATH_NAME)),
                    c.getDouble(c.getColumnIndex(MovieEntry.COLUMN_POPULARITY)),
                    c.getDouble(c.getColumnIndex(MovieEntry.COLUMN_AVERAGE)),
                    c.getInt(c.getColumnIndex(MovieEntry.COLUMN_VOTE_COUNT)));
        }
        db.close();
        return m;
    }
}















