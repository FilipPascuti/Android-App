package com.ilazar.myapp.todo.data.remote

import com.ilazar.myapp.core.Api
import com.ilazar.myapp.todo.data.Item
import retrofit2.http.*

object ItemApi {
    interface Service {
        @GET("/api/song")
        suspend fun find(): List<Item>

        @GET("/api/song/{id}")
        suspend fun read(@Path("id") itemId: String): Item;

        @Headers("Content-Type: application/json")
        @POST("/api/song")
        suspend fun create(@Body item: Item): Item

        @Headers("Content-Type: application/json")
        @PUT("/api/song/{id}")
        suspend fun update(@Path("id") itemId: String, @Body item: Item): Item
    }

    val service: Service = Api.retrofit.create(Service::class.java)
}