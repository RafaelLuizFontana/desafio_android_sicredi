package br.com.sicredi.eventos

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import br.com.sicredi.eventos.api.EventApiService
import br.com.sicredi.eventos.model.Event
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [EventDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class EventListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = title

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        if (findViewById<NestedScrollView>(R.id.event_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        setupRecyclerView(findViewById(R.id.event_list))
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        var list = listOf<Event>()
        val activity = this
        val foregroundInterface = object : ForegroundInterface<List<Event>>{
            override fun preStartBackgroundExecute() {
                Log.d("GetEventList", "Tentativa de contactar o servidor")
            }

            override fun onSuccessBackgroundExecute(result: List<Event>?) {
                Log.d("GetEventList", "Sucesso")
                if (result != null) list = result
            }

            override fun onFailureBackgroundExecute(throwable: Throwable?) {
                Log.e("GetEventList", "Falha: " + throwable?.message)
            }

            override fun onFinishBackgroundExecute() {
                Log.d("GetEventList", "Fim")
                recyclerView.adapter = SimpleItemRecyclerViewAdapter(activity, list, twoPane)
            }
        }
        val eventApiService = EventApiService()
        eventApiService.getEventList(foregroundInterface)
    }

    class SimpleItemRecyclerViewAdapter(
        private val parentActivity: EventListActivity,
        private val values: List<Event>,
        private val twoPane: Boolean
    ) :
        RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val onClickListener: View.OnClickListener

        init {
            onClickListener = View.OnClickListener { v ->
                val item = v.tag as Event
                if (twoPane) {
                    val fragment = EventDetailFragment().apply {
                        arguments = Bundle().apply {
                            putString(EventDetailFragment.ARG_EVENT_ID, item.id)
                        }
                    }
                    parentActivity.supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.event_detail_container, fragment)
                        .commit()
                } else {
                    val intent = Intent(v.context, EventDetailActivity::class.java).apply {
                        putExtra(EventDetailFragment.ARG_EVENT_ID, item.id)
                            .putExtra(EventDetailFragment.ARG_EVENT_TITLE, item.title)
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.event_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = values[position]
            holder.idView.text = item.id
            holder.contentView.text = item.title

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val idView: TextView = view.findViewById(R.id.id_text)
            val contentView: TextView = view.findViewById(R.id.content)
        }
    }
}