package com.makinap.tineo.neotrack.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.makinap.tineo.neotrack.data.DataDef.ColumnsTrack;
import com.makinap.tineo.neotrack.data.DataDef.ColumnsTienda;
import com.makinap.tineo.neotrack.data.DataDef.ColumnsConfig;

/**
 * Created by tineo on 14/09/16.
 */
public class TrackingDatabaseHelper extends SQLiteOpenHelper{

    private static final String TAG = "SQL";
    private static TrackingDatabaseHelper sInstance;

    // Database Info
    private static final String DATABASE_NAME = "trackDatabase8";
    private static final int DATABASE_VERSION = 1;


    interface Tables {
        String TABLE_TRACK = "track";
        String TABLE_TIENDA = "tienda";
        String TABLE_CONFIG = "config";
    }


    public TrackingDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized TrackingDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new TrackingDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }


    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(String.format("CREATE TABLE %s ( " +
                " %s INTEGER PRIMARY KEY AUTOINCREMENT," + // Define a primary key
                " %s TEXT," +
                " %s TEXT," +
                " %s DECIMAL(8,6)," +
                " %s DECIMAL(9,6)," +
                " %s TEXT," +
                " %s TEXT," +
                " %s TEXT" +
                ")",
                Tables.TABLE_TRACK,
                ColumnsTrack.KEY_TRACK_ID,
                ColumnsTrack.KEY_TRACK_TIENDA_ID,
                ColumnsTrack.KEY_TRACK_OBS,
                ColumnsTrack.KEY_TRACK_LAT,
                ColumnsTrack.KEY_TRACK_LNG,
                ColumnsTrack.KEY_TRACK_NUM,
                ColumnsTrack.KEY_TRACK_USR,
                ColumnsTrack.KEY_TRACK_DTIME
                ));


        sqLiteDatabase.execSQL(String.format("CREATE TABLE %s " +
                "(" +
                " %s INTEGER PRIMARY KEY AUTOINCREMENT," +
                " %s TEXT," +
                " %s TEXT," +
                " %s TEXT" +
                ")",
                Tables.TABLE_TIENDA,
                ColumnsTienda.KEY_TIENDA_ID,
                ColumnsTienda.KEY_TIENDA_CODE,
                ColumnsTienda.KEY_TIENDA_NAME,
                ColumnsTienda.KEY_TIENDA_STATE
                ));

        sqLiteDatabase.execSQL(String.format("CREATE TABLE %s " +
                        "(" +
                        " %s INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " %s TEXT," +
                        " %s TEXT" +
                        ")",
                Tables.TABLE_CONFIG,
                ColumnsConfig.KEY_CONFIG_ID,
                ColumnsConfig.KEY_CONFIG_TAG,
                ColumnsConfig.KEY_CONFIG_VALUE
        ));


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_TRACK);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Tables.TABLE_TIENDA);
            onCreate(sqLiteDatabase);
        }
    }




}
