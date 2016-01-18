package com.google.xkc.mytheater;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by xkc on 1/17/16.
 */
public class DBManager {
    private SQLiteDatabase db;

    private final String LOG_TAG = this.getClass().getSimpleName();

    public DBManager(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getReadableDatabase();
    }

    public void addOneMovie(AMovie movie) {
        db.beginTransaction();

        String add = "insert into favorite(id,poster_path,original_title," +
                "overview,release_date,vote_average) values(?,?,?,?,?,?)";

        try {
            db.execSQL(add, new Object[]{
                    movie.getId(),
                    movie.getPoster_path(),
                    movie.getOriginal_title(),
                    movie.getOverview(),
                    movie.getRelease_date(),
                    movie.getVote_average()});
            db.setTransactionSuccessful();
            Log.i(LOG_TAG, "add one movie is successful");
        } catch (Exception e) {
            Log.e(LOG_TAG, "add one movie is failed: " + e.getMessage());
        } finally {
            db.endTransaction();
        }

    }

    //if this movie item is marked as favorite
    public boolean isExist(AMovie movie) {
        String sql = "select * from favorite where id=?";
        Cursor cursor = db.rawQuery(sql, new String[]{movie.getId()});
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public void deleteOneMovie(AMovie movie) {
        Log.i(LOG_TAG, "deleteOneMovie() is called");
        String delete = "delete from favorite where id=?";
        db.execSQL(delete, new String[]{movie.getId()});
    }


    public Cursor queryAllFavorites() {
        String sql = "select * from favorite";
        return db.rawQuery(sql, null);
    }


}
