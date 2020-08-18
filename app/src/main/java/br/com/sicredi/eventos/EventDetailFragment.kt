package br.com.sicredi.eventos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.com.sicredi.eventos.api.EventApiService
import br.com.sicredi.eventos.glide.GlideApp
import br.com.sicredi.eventos.model.Event
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_event_detail.*
import kotlinx.android.synthetic.main.event_detail.view.*
import java.text.SimpleDateFormat
import java.util.*

class EventDetailFragment : Fragment() {

    private var eventId : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_EVENT_ID)) {
                eventId = it.getString(ARG_EVENT_ID)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.event_detail, container, false)
        loadContents(rootView)
        return rootView
    }

    fun loadContents(rootView : View){
        eventId?.let {
            val foregroundInterface = object : ForegroundInterface<Event> {
                override fun preStartBackgroundExecute() {
                    activity?.pb_loading_event_details?.show()
                    activity?.event_detail_container?.visibility = View.INVISIBLE
                    activity?.bt_checkin_form?.visibility = View.INVISIBLE
                }

                override fun onSuccessBackgroundExecute(result: Event?) {
                    result?.let { event ->
                        rootView.event_detail_text.text = event.description
                        rootView.event_detail_title.text = event.title
                        activity!!.let { activity ->
                            GlideApp.with(rootView.context)
                                .applyDefaultRequestOptions(RequestOptions().placeholder(R.drawable.glide_placeholder).error(R.drawable.glide_placeholder))
                                .load(event.image)
                                .into(activity.event_image)
                            event.date?.let { date ->
                                (activity as EventDetailActivity).setFloatClickListener(date)
                                val data = Date(date)
                                val format = SimpleDateFormat("dd/MM/yyyy HH:mm")
                                rootView.event_detail_date.text = format.format(data) + "h"
                            }
                            val onClickListener : View.OnClickListener
                            onClickListener = View.OnClickListener { v ->
                                val intent = Intent(v.context, CheckinActivity::class.java).apply {
                                    putExtra(ARG_EVENT_ID, event.id)
                                    putExtra(ARG_EVENT_TITLE, event.title)
                                }
                                v.context.startActivity(intent)
                            }
                            activity.bt_checkin_form.setOnClickListener(onClickListener)
                        }
                    }
                }

                override fun onFailureBackgroundExecute(throwable: Throwable?) {
                    throwable?.printStackTrace()
                    Snackbar.make(rootView, getString(R.string.servidor_indisponivel), Snackbar.LENGTH_LONG).show()
                }

                override fun onFinishBackgroundExecute() {
                    activity?.pb_loading_event_details?.hide()
                    activity?.event_detail_container?.visibility = View.VISIBLE
                    activity?.bt_checkin_form?.visibility = View.VISIBLE

                }
            }
            val eventApiService = EventApiService()
            eventApiService.getEvent(it, foregroundInterface)
        }
    }

    companion object {
        const val ARG_EVENT_ID = "event_id"
        const val ARG_EVENT_TITLE = "event_title"

    }
}