package com.av.uwish.Retrofit


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient private constructor() {// public static final String Base_URL="http://bikeservice.esy.es/public/api/";

  val api: Api

    get() = retrofit!!.create(Api::class.java!!)

  init {

    retrofit = Retrofit.Builder()
            .baseUrl(Base_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
  }

  companion object {

    val Base_URL ="http://admin.unsaidwish.com/api/"

    var mInstance: RetrofitClient? = null

    var retrofit: Retrofit? = null

    val instance: RetrofitClient
      @Synchronized get() {


        if (mInstance == null) {
          mInstance = RetrofitClient()
        }
        return mInstance as RetrofitClient
      }
  }
}
