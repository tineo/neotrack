package com.makinap.tineo.neotrack.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.widget.Toast;

import com.makinap.tineo.neotrack.QueueActivity;
import com.makinap.tineo.neotrack.R;
import com.makinap.tineo.neotrack.TrackService;
import com.makinap.tineo.neotrack.model.Photo;
import com.makinap.tineo.neotrack.model.Track;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by tineo on 24/10/16.
 */

public class QueueService extends IntentService {

    public static final String ACTION_PROGRESO =
            "com.makinap.tineo.intent.action.PROGRESO";
    public static final String ACTION_FIN =
            "com.makinap.tineo.intent.action.FIN";

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    private  RealmRecyclerView mNotes;

    private Realm mRealm;
    private RealmConfiguration mRealmConfig;

    public QueueService() {
        super("QueueService");

    }



    @Override
    protected void onHandleIntent(Intent intent)
    {


        mRealmConfig = new RealmConfiguration
                .Builder(this)
                .deleteRealmIfMigrationNeeded() // FIXME: 24/10/16
                .build();
        Realm.setDefaultConfiguration(mRealmConfig);
        mRealm = Realm.getDefaultInstance();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        RealmResults<Track> notes = mRealm.where(Track.class).findAllSorted("dtime", Sort.ASCENDING);



        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.barcode_small_icon)
                .setContentTitle("Enviando lectura")
                .setContentText("Empeazando a enviar")
                .setAutoCancel(true);
        notificationBuilder.setProgress(100,0,true);
        /*notificationBuilder.setContentText("Enviando lectura 1/"+total);
        notificationManager.notify(0, notificationBuilder.build());
        */



        for(int i = 0; i < notes.size();){

            if(isNetworkAvailable()) {
                Log.e("isNetworkAvailable","true");

                notificationBuilder
                        .setContentText(
                                "Enviando: " + mRealm.where(Track.class).
                        findAllSorted("dtime", Sort.ASCENDING).size() + " restantes");
                notificationManager.notify(0, notificationBuilder.build());


                /*ProgressBar pb = (ProgressBar) mNotes.getRecycleView().getChildAt(0).findViewById(R.id.pb);
                pb.setIndeterminate(true);
                pb.setVisibility(View.VISIBLE);
                mNotes.getRecycleView()
                        .getChildAt(0)
                        .findViewById(R.id.holder_container)
                        .setClickable(false);*/




                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(QueueActivity.ProgressReceiver.ACTION_RESP);
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                broadcastIntent.putExtra(ACTION_FIN, notes.get(i).getId());
                sendBroadcast(broadcastIntent);


                //SystemClock.sleep(20000);






                Retrofit retrofit = new Retrofit.Builder()
                        //.baseUrl("https://neotrack.herokuapp.com")
                        .baseUrl("http://lg.neoprojects.com.pe/")
                        //.baseUrl("http://192.168.1.38:8000/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();



                TrackService service = retrofit.create(TrackService.class);



                //Call<Track> trackCall = service.sendData2(codigo,direccion,obs,lat,lng,num,usr);


                HashMap<String, RequestBody> map = new HashMap<>();
                // create part for file (photo, video, ...)


                // create a map of data to pass along

                RequestBody rb_guid = createPartFromString(String.valueOf(notes.get(i).getId()));
                RequestBody rb_codigo = createPartFromString(String.valueOf(notes.get(i).getCode()));
                RequestBody rb_tienda = createPartFromString(String.valueOf(notes.get(i).getIdTienda()));
                RequestBody rb_obs = createPartFromString(String.valueOf(notes.get(i).getObs()));
                RequestBody rb_lat = createPartFromString(String.valueOf(notes.get(i).getLat()));
                RequestBody rb_lng = createPartFromString(String.valueOf(notes.get(i).getLng()));
                RequestBody rb_num = createPartFromString(String.valueOf(notes.get(i).getNum()));
                RequestBody rb_usr = createPartFromString(String.valueOf(notes.get(i).getUser()));
                RequestBody rb_flag = createPartFromString(String.valueOf(notes.get(i).getFlag()));

                map.put("codigo", rb_codigo);
                map.put("tienda", rb_tienda);
                map.put("obs", rb_obs);
                map.put("lat", rb_lat);
                map.put("lng", rb_lng);
                map.put("num", rb_num);
                map.put("usr", rb_usr);




                final RealmResults<Track> finalNotes = notes;
                final int finalI = i;
                final String id = notes.get(i).getId();

                RealmResults<Photo> fotos = mRealm.where(Photo.class).equalTo("trackId", id).findAll();
                Log.e("Track"," -> "+id);
                for (Photo foto: fotos ) {
                    Log.e("Photo"," -> "+foto.getId());
                    Log.e("Photo URI"," -> "+foto.getUri());
                }
                ArrayList<MultipartBody.Part> parts =  new ArrayList<>();
                int ix = 1;
                for(Photo foto : fotos){
                    MultipartBody.Part filePart = prepareFilePart("photo"+ix, foto.getUri());
                    Log.e("photo"+ix,foto.getUri());
                    parts.add(filePart);
                    ix++;
                }
                Call<Track> trackCall =  null;
                Log.e("FOTOS for Parts : ","->"+fotos.size());
                switch (fotos.size()){
                    case 1: trackCall = service.sendData(rb_guid,rb_codigo,rb_tienda,rb_obs,rb_lat,rb_lng,rb_num,rb_usr,rb_flag,parts.get(0)); break;
                    case 2: trackCall = service.sendData(rb_guid,rb_codigo,rb_tienda,rb_obs,rb_lat,rb_lng,rb_num,rb_usr,rb_flag,parts.get(0),parts.get(1)); break;
                    case 3: trackCall = service.sendData(rb_guid,rb_codigo,rb_tienda,rb_obs,rb_lat,rb_lng,rb_num,rb_usr,rb_flag,parts.get(0),parts.get(1),parts.get(2)); break;
                    case 4: trackCall = service.sendData(rb_guid,rb_codigo,rb_tienda,rb_obs,rb_lat,rb_lng,rb_num,rb_usr,rb_flag,parts.get(0),parts.get(1),parts.get(2),parts.get(3));break;
                }


                trackCall.enqueue(new Callback<Track>() {
                    @Override
                    public void onResponse(Call<Track> call, Response<Track> response) {

                        Track track = response.body();
                        if (response.body() != null) {
                            //Toast.makeText(getApplicationContext(), "re:" + track.getObs(), Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "El codigo <" + track.getCode() +"> se ha enviado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "C: " + response.code(), Toast.LENGTH_SHORT).show();
                        }

                        if (response.code() != 500) {

                            deleteById(id);
                            //mRealm.beginTransaction();
                            //finalNotes.deleteFromRealm(finalI);
                            //mRealm.commitTransaction();
                            //Toast.makeText(getApplicationContext(), "Registro correcto", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error: ocurrio un problema en el registro", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(getApplicationContext(), "codigo de error"+response.code(), Toast.LENGTH_SHORT).show();
                    }



                    @Override
                    public void onFailure(Call<Track> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Sin poder cominucarse", Toast.LENGTH_SHORT).show();

                    }


                });

                //SystemClock.sleep(3000);
                notes = mRealm.where(Track.class).findAllSorted("dtime", Sort.ASCENDING);

                i++;
            }else {
                Log.e("isNetworkAvailable","false");
                notificationBuilder.setContentText("Esperando conexion ... ");
                notificationManager.notify(0, notificationBuilder.build());
                SystemClock.sleep(5000);
            }

        }

        //notificationManager.notify(0, notificationBuilder.build());

        notificationBuilder.setProgress(100,100,false);
        notificationBuilder.setContentText("Se envio lectura correctamente");
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void deleteById(String id) {

        mRealmConfig = new RealmConfiguration
                .Builder(this)
                .deleteRealmIfMigrationNeeded() // FIXME: 24/10/16
                .build();
        Realm.setDefaultConfiguration(mRealmConfig);
        mRealm = Realm.getDefaultInstance();

        mRealm.beginTransaction();
        mRealm.where(Track.class).equalTo("id", id).findAll().deleteAllFromRealm();
        mRealm.commitTransaction();

    }


    @Override
    public void onDestroy() {
        Log.e("QueueService","onDestroy");
        notificationManager.cancelAll();
        //stopForeground(true);


        super.onDestroy();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                MediaType.parse(MULTIPART_FORM_DATA), descriptionString);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, String fileUri) {
        //File file = FileUtils.getFile(this, fileUri);
        //File file = FileUtils.getFile(fileUri);



        Uri uri = Uri.parse(fileUri);
        File _fileimage = null;
        try {
            File path = new File(getBaseContext().getFilesDir(), ".");
            if (!path.exists()) path.mkdirs();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String filename = "IMG_" + timeStamp + partName + ".jpg";
            _fileimage = new File(path, "_"+filename);
            InputStream inputStream = getContentResolver().openInputStream(uri);
            OutputStream outputStream = new FileOutputStream(_fileimage);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        //String ivImage_full ="";

        // 为file建立RequestBody实例
        RequestBody requestFile =
                RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), _fileimage);

        // MultipartBody.Part借助文件名完成最终的上传
        return MultipartBody.Part.createFormData(partName, _fileimage.getName(), requestFile);
    }

    @NonNull
    private MultipartBody.Part prepareFilePartFromBytes(String partName, byte[] bytes) {
        //File file = FileUtils.getFile(this, fileUri);
        //File file = FileUtils.getFile(fileUri);

        //String ivImage_full ="";
        File path = new File(getBaseContext().getFilesDir(), ".");
        if (!path.exists()) path.mkdirs();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = "IMG_" + timeStamp + ".jpg";
        File fileimage = new File(path, filename);

        File photo=new File(Environment.getExternalStorageDirectory(), filename);

        if (photo.exists()) {
            photo.delete();
        }

        try {
            FileOutputStream fos=new FileOutputStream(photo.getPath());

            fos.write(bytes);
            fos.close();
        }
        catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }
        /*try {
            FileUtils.writeByteArrayToFile(fileimage, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        // 为file建立RequestBody实例
        RequestBody requestFile =
                RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), photo);

        // MultipartBody.Part借助文件名完成最终的上传
        return MultipartBody.Part.createFormData(partName, fileimage.getName(), requestFile);
    }

    @NonNull
    private MultipartBody.Part prepareFilePartFromUri(String partName, Uri uri) {


        File auxFile = new File(uri.getPath());


        // 为file建立RequestBody实例
        RequestBody requestFile =
                RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), auxFile);

        // MultipartBody.Part借助文件名完成最终的上传
        return MultipartBody.Part.createFormData(partName, auxFile.getName(), requestFile);
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };

        CursorLoader loader = new CursorLoader(getApplicationContext(), uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }





}