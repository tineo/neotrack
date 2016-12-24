package com.makinap.tineo.neotrack.net;

/**
 * Created by tineo on 25/10/16.
 */

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;

public interface RetrofitInterface {

    @GET("download/Node-Android-Chat.zip")
    @Streaming
    Call<ResponseBody> downloadFile();
}