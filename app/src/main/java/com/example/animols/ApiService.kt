package com.example.animols

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    // Endpoint: https://api.api-ninjas.com/v1/animals?name=lion
    @GET("animals")
    suspend fun getAnimal(
        @Query("name") name: String
    ): List<Animal>
}
