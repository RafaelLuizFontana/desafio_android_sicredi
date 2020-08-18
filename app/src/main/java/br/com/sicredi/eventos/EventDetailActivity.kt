package br.com.sicredi.eventos

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.provider.CalendarContract
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import br.com.sicredi.eventos.model.Event
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_event_detail.*
import java.text.SimpleDateFormat
import java.util.*

class EventDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)
        setSupportActionBar(findViewById(R.id.detail_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (savedInstanceState == null) {
            val fragment = EventDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(
                        EventDetailFragment.ARG_EVENT_ID,
                        intent.getStringExtra(EventDetailFragment.ARG_EVENT_ID)
                    )
                }
            }

            supportFragmentManager.beginTransaction()
                .add(R.id.event_detail_container, fragment)
                .commit()
        }
    }

    fun setShareButtonClickListener(event : Event) {
        bt_share.setOnClickListener { view ->
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, event.title)
                event.latitude?.let { lat->
                    event.longitude?.let{ lon->
                        val geocoder : Geocoder
                        geocoder = Geocoder(applicationContext, Locale.getDefault())
                        putExtra(CalendarContract.Events.EVENT_LOCATION, geocoder.getFromLocation(lat, lon, 1).get(0).getAddressLine(0))
                    }
                }
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.date)
            }
            if (intent.resolveActivity(packageManager) != null){
                startActivity(intent)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                navigateUpTo(Intent(this, EventListActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}