package com.example.scaner;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME ="inventoryDB";
    public static final String TABLE_INVENTORY= "inventory";

    public static final String KEY_ID = "_id";
    public static final String KEY_CABINET_SCAN = "cabinetScan";
    public static final String KEY_ITEM_SCAN = "itemScan";
    public static final String KEY_COUNT = "count";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_INVENTORY + "(" + KEY_ID
                + " integer primary key, " + KEY_CABINET_SCAN + " text, "
                + KEY_ITEM_SCAN + " text, "
                + KEY_COUNT + " integer" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_INVENTORY);
        onCreate(db);
    }
}
