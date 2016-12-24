package com.makinap.tineo.neotrack.data;

/**
 * Created by tineo on 19/09/16.
 */


public class DataDef {
    interface ColumnsTrack{
        String KEY_TRACK_ID = "_id";
        String KEY_TRACK_TIENDA_ID = "idTienda";
        String KEY_TRACK_OBS = "obs";

        String KEY_TRACK_LAT = "lat";
        String KEY_TRACK_LNG = "lng";
        String KEY_TRACK_NUM = "num";
        String KEY_TRACK_USR = "user";
        String KEY_TRACK_DTIME = "dtime";
    }

    interface ColumnsTienda{
        String KEY_TIENDA_ID = "_id";
        String KEY_TIENDA_CODE = "code";
        String KEY_TIENDA_NAME = "name";
        String KEY_TIENDA_STATE = "state";
    }

    interface ColumnsConfig{
        String KEY_CONFIG_ID = "_id";
        String KEY_CONFIG_TAG = "tag";
        String KEY_CONFIG_VALUE = "value";
    }

    public static class Track implements ColumnsTrack{}
    public static class  Tienda implements ColumnsTienda{}
    public static class  Config implements ColumnsConfig{}


}
