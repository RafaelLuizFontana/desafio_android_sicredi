package br.com.sicredi.eventos.api

import br.com.sicredi.eventos.model.Checkin
import br.com.sicredi.eventos.model.CheckinResponse
import br.com.sicredi.eventos.model.Event
import retrofit2.Call
import retrofit2.http.*

interface EventApi {
    @Headers("Content-Type: application/json")
    @GET("events")
    fun getEventList(): Call<List<Event>>

    @Headers("Content-Type: application/json")
    @GET("events/{id}")
    fun getEventDetail(@Path("id") id: String) : Call<Event>

    @Headers("Content-Type: application/json")
    @POST("checkin")
    fun checkin(@Body checkin: Checkin): Call<CheckinResponse>

}