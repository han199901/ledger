package com.han.zhangben.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {
    public DbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS `bill`  (\n" +
                "  `id` INTEGER PRIMARY KEY autoincrement,\n" +
                "  `name` TEXT NOT NULL,\n" +
                "  `car_model` TEXT,\n" +
                "  `from_addr` TEXT,\n" +
                "  `to_addr` TEXT,\n" +
                "  `price` INTEGER NOT NULL,\n" +
                "  `date` INTEGER NOT NULL,\n" +
                "  `status` INTEGER NOT NULL,\n" +
                "  `del` INTEGER NOT NULL" +
                ")";
        db.execSQL(createTable);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
