package com.ronyut.flightmobileapp.API

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
    private var baseUrl: String = "";

    fun init(url: String): ServiceBuilder {
        baseUrl = url
        return this
    }

    val webservice: PostApi by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .build().create(PostApi::class.java)
    }
}