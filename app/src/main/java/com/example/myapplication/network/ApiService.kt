package com.example.myapplication.network

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("get?test=123")
    fun getData(): Call<ApiResponse>
}

data class ApiResponse(
    val url: String? = null
)
