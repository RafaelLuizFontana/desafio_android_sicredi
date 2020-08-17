package br.com.sicredi.eventos

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

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

    fun setFloatClickListener(time : Long) {
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            val intent = Intent(Intent.ACTION_SEND).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, "teste")
                putExtra(CalendarContract.Events.EVENT_LOCATION, "local teste")
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, time)
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