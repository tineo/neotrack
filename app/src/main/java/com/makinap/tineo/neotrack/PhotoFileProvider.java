package com.makinap.tineo.neotrack;

import android.net.Uri;
import android.support.v4.content.FileProvider;

import java.util.HashMap;

/**
 * Created by tineo on 17/09/16.
 */
public class PhotoFileProvider extends FileProvider {

    public static final Uri CONTENT_URI = Uri.parse("content://com.makinap.tineo/");

    private static final HashMap<String, String> MIME_TYPES = new HashMap<String, String>();

    static {

        MIME_TYPES.put(".jpg", "image/jpeg");

        MIME_TYPES.put(".jpeg", "image/jpeg");

    }

    /*public static Uri getUriForFile(Context context, String authority, File file) {
        //final PathStrategy strategy = getPathStrategy(context, authority);
        //return strategy.getUriForFile(file);
    }*/


}
