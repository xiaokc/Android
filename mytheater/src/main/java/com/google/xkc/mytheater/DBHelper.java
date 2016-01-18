package com.google.xkc.mytheater;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by xkc on 1/17/16.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String NAME = "movie.db";
    private static final int VERSION = 1;
    private static final String CREATE_FAVORITE = "create table " +
            "favorite(_id integer primary key autoincrement," +
            "id varchar(30)," +
            "poster_path varchar(100)," +
            "original_title varchar(100)," +
            "overview varchar(3000)," +
            "release_date varchar(20)," +
            "vote_average varchar(10))";

    public DBHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FAVORITE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
