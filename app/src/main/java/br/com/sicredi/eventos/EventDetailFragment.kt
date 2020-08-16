package br.com.sicredi.eventos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import br.com.sicredi.eventos.api.EventApiService
import br.com.sicredi.eventos.glide.GlideApp
import br.com.sicredi.eventos.model.Event
import com.google.android.material.appbar.CollapsingToolbarLayout
import kotlinx.android.synthetic.main.activity_event_detail.*

/**
 * A fragment representing a single Event detail screen.
 * This fragment is either contained in a [EventListActivity]
 * in two-pane mode (on tablets) or a [EventDetailActivity]
 * on handsets.
 */
class EventDetailFragment : Fragment() {

    private var eventId : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_EVENT_ID)) {
                eventId = it.getString(ARG_EVENT_ID)
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.

                //activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title =
                 //   item?.content
            }
            if (it.containsKey(ARG_EVENT_TITLE)) {
                activity?.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout)?.title =
                    it.getString(ARG_EVENT_TITLE)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.event_detail, container, false)

        // Show the dummy content as text in a TextView.

        //item?.let {
        //    rootView.findViewById<TextView>(R.id.event_detail).text = it.details
        //}

        loadContents(rootView)
        return rootView
    }

    fun loadContents(rootView : View){
        eventId?.let {
            val foregroundInterface = object : ForegroundInterface<Event> {
                override fun preStartBackgroundExecute() {}

                override fun onSuccessBackgroundExecute(result: Event?) {
                    result?.let {event ->
                        rootView.findViewById<TextView>(R.id.event_detail).text = event.description
                        activity!!.let {activity ->
                            GlideApp.with(rootView.context)
                                .load("https://designcomcafe.com.br/wp-content/uploads/2019/12/tripe-para-fotografia-de-paisagem-blog-design-com-cafe.jpg")
                                .into(activity.event_image)
                        }
                    }
                }

                override fun onFailureBackgroundExecute(throwable: Throwable?) {}

                override fun onFinishBackgroundExecute() {}
            }
            val eventApiService = EventApiService()
            eventApiService.getEvent(it, foregroundInterface)
        }
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_EVENT_ID = "item_id"
        const val ARG_EVENT_TITLE = "item_title"
        const val ARG_EVENT_DESCRIPTION = "item_description"
    }
}