package com.example.animols

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "https://api.api-ninjas.com/v1/"
    private const val API_KEY = "TqjPfFU5bebD8oXF/IN11g==9xH5De32YQTHhFnN"

    // Attach API key to all requests
    private val client = OkHttpClient.Builder().addInterceptor { chain ->
        val request = chain.request().newBuilder()
            .addHeader("X-Api-Key", API_KEY)
            .build()
        chain.proceed(request)
    }.build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // Attach client with API key
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
