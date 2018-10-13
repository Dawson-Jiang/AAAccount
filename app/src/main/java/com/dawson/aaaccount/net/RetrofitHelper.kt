package com.dawson.aaaccount.net

import com.dawson.aaaccount.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Dawson on 2018/3/29.
 */
object RetrofitHelper {
      private const val BASE_URL = "http://192.168.1.8:8080/"

    private fun init(): Retrofit {
        val client = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            client.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        }

        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client.build())
                .baseUrl(BASE_URL)
                .build()
    }

    private val retrofit: Retrofit = init()

    fun <T> getService(clazz: Class<T>) = retrofit.create(clazz)!!
}