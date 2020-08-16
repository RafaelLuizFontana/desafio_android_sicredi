package br.com.sicredi.eventos.api

import br.com.sicredi.eventos.ForegroundInterface
import br.com.sicredi.eventos.model.Checkin
import br.com.sicredi.eventos.model.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventApiService {
    fun getEventList(foregroundInterface: ForegroundInterface<List<Event>>?){
        foregroundInterface?.preStartBackgroundExecute()
        val retrofit = ServiceBuilder.buildService(EventApi::class.java)
        retrofit.getEventList().enqueue(
            object : Callback<List<Event>> {
                override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                    foregroundInterface?.onFailureBackgroundExecute(t)
                    foregroundInterface?.onFinishBackgroundExecute()
                }

                override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                    foregroundInterface?.onSuccessBackgroundExecute(response.body())
                    foregroundInterface?.onFinishBackgroundExecute()
                }
            }
        )
    }

    fun getEvent(eventId : String, foregroundInterface: ForegroundInterface<Event>?){
        val retrofit = ServiceBuilder.buildService(EventApi::class.java)
        retrofit.getEventDetail(eventId).enqueue(
            object : Callback<Event> {
                override fun onFailure(call: Call<Event>, t: Throwable) {
                    foregroundInterface?.onFailureBackgroundExecute(t)
                    foregroundInterface?.onFinishBackgroundExecute()
                }

                override fun onResponse(call: Call<Event>, response: Response<Event>) {
                    foregroundInterface?.onSuccessBackgroundExecute(response.body())
                    foregroundInterface?.onFinishBackgroundExecute()
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