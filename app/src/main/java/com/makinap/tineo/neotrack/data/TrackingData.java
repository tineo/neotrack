package com.makinap.tineo.neotrack.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.makinap.tineo.neotrack.data.DataDef.ColumnsConfig;
import com.makinap.tineo.neotrack.data.DataDef.ColumnsTienda;
import com.makinap.tineo.neotrack.data.TrackingDatabaseHelper.Tables;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tineo on 18/09/16.
 */
public class TrackingData {

    private static TrackingDatabaseHelper db;
    private static TrackingData instancia = new TrackingData();
    private Context context;

    public TrackingData(){}

    public static TrackingData getInstancia(Context context){

        if(db == null){

            db = new TrackingDatabaseHelper(context);
        }
        return instancia;
    }

    // Tiendas

    public Cursor getTiendas(){
        SQLiteDatabase readableDatabase = db.getReadableDatabase();

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        builder.setTables(Tables.TABLE_TIENDA);

        return builder.query(readableDatabase,
                new String[]{
                        ColumnsTienda.KEY_TIENDA_ID,
                        ColumnsTienda.KEY_TIENDA_NAME,
                        ColumnsTienda.KEY_TIENDA_STATE
                },
                null, null, null, null, null);
    }

    public long insertTienda(Tienda tienda) {
        SQLiteDatabase writableDatabase = db.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(ColumnsTienda.KEY_TIENDA_CODE, tienda.getIdTienda()); //set NULL for AI
        valores.put(ColumnsTienda.KEY_TIENDA_NAME, tienda.getName());
        valores.put(ColumnsTienda.KEY_TIENDA_STATE, tienda.getState());

        //writableDatabase.insertOrThrow(Tables.TABLE_TIENDA, null, valores);

        return writableDatabase.insertOrThrow(Tables.TABLE_TIENDA, null, valores);
    }

    public boolean truncateTienda(){

        SQLiteDatabase writableDatabase = db.getWritableDatabase();
        int resultado = writableDatabase.delete(Tables.TABLE_TIENDA, null, null);

        return resultado > 0;
    }

    public boolean deleteTienda(long idTienda) {
        SQLiteDatabase writableDatabase = db.getWritableDatabase();

        String whereClause = String.format("%s=?", ColumnsTienda.KEY_TIENDA_ID);
        String[] whereArgs = {Long.toString(idTienda)};

        int resultado = writableDatabase.delete(Tables.TABLE_TIENDA, whereClause, whereArgs);

        return resultado > 0;
    }


    // Config

    public Config getConfigbyTag( String tagValue ){
        SQLiteDatabase readableDatabase = db.getReadableDatabase();

        String sql = String.format("SELECT * FROM %s WHERE %s=?",
                Tables.TABLE_CONFIG, DataDef.ColumnsConfig.KEY_CONFIG_TAG);

        String[] selectionArgs = {tagValue};

        //return
        Cursor cursor =  readableDatabase.rawQuery(sql, selectionArgs);
        cursor.moveToLast();
        Log.e("Config:::", "config: "+cursor.getCount());
        try
        {
            int idIndex = cursor.getColumnIndexOrThrow(ColumnsConfig.KEY_CONFIG_ID);
            int tagIndex = cursor.getColumnIndexOrThrow(ColumnsConfig.KEY_CONFIG_TAG);
            int valueIndex = cursor.getColumnIndexOrThrow(ColumnsConfig.KEY_CONFIG_VALUE);

            int id = cursor.getInt(idIndex);
            String tag = cursor.getString(tagIndex);
            String value = cursor.getString(valueIndex);
            Log.d("Config",tag +" : "+value);
            return new Config(id, tag, value);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public int insertConfig(Config config) {
        SQLiteDatabase writableDatabase = db.getWritableDatabase();

        ContentValues valores = new ContentValues();
        //valores.put(ColumnsConfig.KEY_CONFIG_ID, (byte[]) null); //set NULL for AI
        valores.put(ColumnsConfig.KEY_CONFIG_TAG, config.getTag());
        valores.put(ColumnsConfig.KEY_CONFIG_VALUE, config.getValue());

       // writableDatabase.insertOrThrow(Tables.TABLE_CONFIG, null, valores);

        return (int) writableDatabase.insertOrThrow(Tables.TABLE_CONFIG, null, valores);
    }
    public boolean updateConfig(Config config) {
        SQLiteDatabase writableDatabase = db.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(ColumnsConfig.KEY_CONFIG_TAG, config.getTag());
        valores.put(ColumnsConfig.KEY_CONFIG_VALUE, config.getValue());

        String selection = String.format("%s=?",
                ColumnsConfig.KEY_CONFIG_TAG);
        final String[] whereArgs = { config.getTag() };

        int resultado = writableDatabase.update(Tables.TABLE_CONFIG, valores, selection, whereArgs);

        return resultado > 0;
    }


    public Cursor getCursor(CharSequence str) {
        SQLiteDatabase readableDatabase = db.getReadableDatabase();
        String select = "" + ColumnsTienda.KEY_TIENDA_NAME + " LIKE ? ";
        String[]  selectArgs = { "%" + str + "%"};
        String[] contactsProjection = new String[] {
                ColumnsTienda.KEY_TIENDA_CODE,
                ColumnsTienda.KEY_TIENDA_NAME,
                ColumnsTienda.KEY_TIENDA_STATE,  };

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        builder.setTables(Tables.TABLE_TIENDA);

        //return readableDatabase.query(Tables.TABLE_TIENDA, contactsProjection, select, selectArgs, null);
        return builder.query(readableDatabase,
                new String[]{
                        ColumnsTienda.KEY_TIENDA_ID,
                        ColumnsTienda.KEY_TIENDA_CODE,
                        ColumnsTienda.KEY_TIENDA_NAME,
                        ColumnsTienda.KEY_TIENDA_STATE
                },
                select, selectArgs, null, null, null);
    }

    public List<Track> getTracks() {

        SQLiteDatabase readableDatabase = db.getReadableDatabase();

        List<Track> list = new ArrayList<Track>();

        Cursor cursor = readableDatabase.rawQuery("SELECT * FROM sports", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {

            Track track = new Track();
            //track.set

            //s.setId(cursor.getString(0));
            //s.setSport(cursor.getString(1));
            /*
            private Long id;
    private Long code;
    private Integer idTienda;
    private String obs;
    private Double lat;
    private Double lng;
    private String num;
    private String user;
    private String dtime;
            * */

            list.add(track);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }







}
