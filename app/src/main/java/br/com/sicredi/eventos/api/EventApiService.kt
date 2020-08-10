package br.com.sicredi.eventos.api

import br.com.sicredi.eventos.model.Checkin
import br.com.sicredi.eventos.model.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventApiService {
    fun getEventList(onResult: (List<Event>?) -> Unit){
        val retrofit = ServiceBuilder.buildService(EventApi::class.java)
        retrofit.getEventList().enqueue(
            object : Callback<List<Event>> {
                override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                    onResult(null)
                }

                override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                    val events = response.body()
                    onResult(events)
                }
            }
        )
    }

    fun getEvent(eventId: String, onResult: (Event?) -> Unit){
        val retrofit = ServiceBuilder.buildService(EventApi::class.java)
        retrofit.getEventDetail(eventId).enqueue(
            object : Callback<Event> {
                override fun onFailure(call: Call<Event>, t: Throwable) {
                    onResult(null)
                }

                override fun onResponse(call: Call<Event>, response: Response<Event>) {
                    val event = response.body()
                    onResult(event)
                }
            }
        )
    }

    fun checkin(checkin: Checkin, onResult: (String?) -> Unit){
        val retrofit = ServiceBuilder.buildService(EventApi::class.java)
        retrofit.checkin(checkin).enqueue(
            object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    onResult(null)
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val code = response.code()
                    if (code != 200){
                        onResult(null)
                    } else {
                        val checkinDone = response.body()
                        onResult(checkinDone)
                    }
                }

            }
        )
    }
}