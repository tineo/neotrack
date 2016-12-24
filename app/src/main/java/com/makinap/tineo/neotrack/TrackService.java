package com.makinap.tineo.neotrack;

import com.makinap.tineo.neotrack.data.Tienda;
import com.makinap.tineo.neotrack.model.Track;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by tineo on 14/09/16.
 */

public interface TrackService {
    //@GET("users/{user}/repos")
    //Call<List<Track>> listRepos(@Path("user") String user);

    @GET("json")
    Call<Track> getTrack();


    @Multipart
    @POST("api/tracking")
    Call<Track> sendData(
            @Part("guid") RequestBody guid,
            @Part("codigo") RequestBody codigo,
            @Part("tienda") RequestBody tienda,
            @Part("obs") RequestBody obs,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng,
            @Part("num") RequestBody num,
            @Part("usr") RequestBody usr,
            @Part("flag") RequestBody flag,
            @Part MultipartBody.Part file );

    @Multipart
    @POST("api/tracking")
    Call<Track> sendData(
            @Part("guid") RequestBody guid,
            @Part("codigo") RequestBody codigo,
            @Part("tienda") RequestBody tienda,
            @Part("obs") RequestBody obs,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng,
            @Part("num") RequestBody num,
            @Part("usr") RequestBody usr,
            @Part("flag") RequestBody flag,
            @Part MultipartBody.Part file,
            @Part MultipartBody.Part file2);

    @Multipart
    @POST("api/tracking")
    Call<Track> sendData(
            @Part("guid") RequestBody guid,
            @Part("codigo") RequestBody codigo,
            @Part("tienda") RequestBody tienda,
            @Part("obs") RequestBody obs,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng,
            @Part("num") RequestBody num,
            @Part("usr") RequestBody usr,
            @Part("flag") RequestBody flag,
            @Part MultipartBody.Part file ,
            @Part MultipartBody.Part file2 ,
            @Part MultipartBody.Part file3 );

    @Multipart
    @POST("api/tracking")
    Call<Track> sendData(
            @Part("guid") RequestBody guid,
            @Part("codigo") RequestBody codigo,
            @Part("tienda") RequestBody tienda,
            @Part("obs") RequestBody obs,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng,
            @Part("num") RequestBody num,
            @Part("usr") RequestBody usr,
            @Part("flag") RequestBody flag,
            @Part MultipartBody.Part file ,
            @Part MultipartBody.Part file2 ,
            @Part MultipartBody.Part file3 ,
            @Part MultipartBody.Part file4 );

    @Multipart
    @POST("api/tracking")
    Call<Track> sendData00(
            @Part("codigo") RequestBody codigo,
            @Part("tienda") RequestBody tienda,
            @Part("obs") RequestBody obs,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng,
            @Part("num") RequestBody num,
            @Part("usr") RequestBody usr );

    @FormUrlEncoded
    @POST("api/tracking")
    Call<Track> sendData2(@Field("codigo") String codigo,
                           @Field("tienda") String tienda,
                           @Field("obs") String obs,
                           @Field("lat") String lat,
                           @Field("lng") String lng,
                           @Field("num") String num,
                           @Field("usr") String usr
                           );
    @GET("api/tienda")
    Call<List<Tienda>> listTiendas(@Query("version") String version);
}