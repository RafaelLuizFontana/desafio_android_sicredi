package br.com.sicredi.eventos

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import br.com.sicredi.eventos.api.EventApiService
import br.com.sicredi.eventos.glide.GlideApp
import br.com.sicredi.eventos.model.Event
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_event_list.*
import kotlinx.android.synthetic.main.event_list_content.*

class EventListActivity : AppCompatActivity() {

    private var twoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = title

        if (findViewById<NestedScrollView>(R.id.event_detail_container) != null) {
            twoPane = true
        }

        setupRecyclerView(findViewById(R.id.event_list))
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        var list = listOf<Event>()
        val activity = this
        val foregroundInterface = object : ForegroundInterface<List<Event>>{
            override fun preStartBackgroundExecute() {
                frameLayout.visibility = View.INVISIBLE
                pb_loading_event.show()
            }

            override fun onSuccessBackgroundExecute(result: List<Event>?) {
                if (result != null) list = result
            }

            override fun onFailureBackgroundExecute(throwable: Throwable?) {
                throwable?.printStackTrace()
                Snackbar.make(recyclerView.rootView, getString(R.string.servidor_indisponivel), Snackbar.LENGTH_LONG).show()
            }

            override fun onFinishBackgroundExecute() {
                pb_loading_event.hide()
                frameLayout.visibility = View.VISIBLE
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
            GlideApp.with(holder.imageView.context)
                .load(item.image)
                .into(holder.imageView)
            holder.contentView.text = item.title

            with(holder.itemView) {
                tag = item
                setOnClickListener(onClickListener)
            }
        }

        override fun getItemCount() = values.size

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView: ImageView = view.findViewById(R.id.iv_event_background)
            val contentView: TextView = view.findViewById(R.id.content)
        }
    }
}