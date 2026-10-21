package com.example.lab09p

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApiService {
    @GET("products?limit=100")
    suspend fun getProducts(): ProductsResponse

    @GET("products/search")
    suspend fun searchProducts(@Query("q") texto: String): ProductsResponse

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): ProductModel
}