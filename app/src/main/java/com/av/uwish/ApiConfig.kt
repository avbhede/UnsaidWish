package com.av.uwish


import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiConfig {
    @Multipart
    @POST("upload_video_global.php")
    fun uploadVideoToServer(
        @Part video:MultipartBody.Part

    ):Call<ServerResponse>

    @Multipart
    @POST("upload_video_wish.php")
    fun uploadWishToServer(
        @Part video:MultipartBody.Part

    ):Call<ServerResponse>
}
