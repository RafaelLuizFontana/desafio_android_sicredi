package br.com.sicredi.eventos

import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import br.com.sicredi.eventos.api.EventApiService
import br.com.sicredi.eventos.model.Checkin
import br.com.sicredi.eventos.model.CheckinResponse
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_checkin.*
import kotlinx.android.synthetic.main.check_in.*

class CheckinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkin)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_checkin)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.title = title
        var eventId = ""
        if (savedInstanceState == null) {
            eventId = intent.getStringExtra(EventDetailFragment.ARG_EVENT_ID)
            val eventTitle = intent.getStringExtra(EventDetailFragment.ARG_EVENT_TITLE)
            tv_event_title.text = eventTitle
        }

        val nameClickListener : View.OnClickListener
        nameClickListener = View.OnClickListener { v->
            tl_nome.error = null
        }
        et_nome.setOnClickListener(nameClickListener)

        val emailClickListner : View.OnClickListener
        emailClickListner = View.OnClickListener { v->
            tl_email.error = null
        }
        et_email.setOnClickListener(emailClickListner)

        val btClickListener : View.OnClickListener
        btClickListener = View.OnClickListener { v->
            val name = et_nome?.text.toString()
            val email = et_email?.text.toString()
            if (!eventId.isEmpty()){
                if (!name.isEmpty()){
                    if (!email.isEmpty()){
                        if (email.contains('@')){
                            val checkin = Checkin(eventId, name, email)
                            val foregroundInterface = object : ForegroundInterface<CheckinResponse>{
                                override fun preStartBackgroundExecute() {
                                    pb_checkin.show()
                                    et_nome.isEnabled = false
                                    et_email.isEnabled = false
                                    bt_checkin.isEnabled = false

                                }

                                override fun onSuccessBackgroundExecute(result: CheckinResponse?) {
                                    val toast = Toast.makeText(v.context, getText(R.string.checkin_success), Toast.LENGTH_LONG)
                                    toast.setGravity(Gravity.CENTER, 0, 20)
                                    toast.show()
                                    finish()
                                }

                                override fun onFailureBackgroundExecute(throwable: Throwable?) {
                                    throwable?.printStackTrace()
                                    Snackbar.make(v.rootView, getString(R.string.servidor_indisponivel), Snackbar.LENGTH_LONG).show()
                                    et_nome.isEnabled = true
                                    et_email.isEnabled = true
                                    bt_checkin.isEnabled = true
                                }

                                override fun onFinishBackgroundExecute() {
                                    pb_checkin.hide()
                                }
                            }
                            val eventApiService = EventApiService()
                            eventApiService.checkin(checkin, foregroundInterface)
                        } else {
                            et_email.error = getString(R.string.error_email)
                        }

                    } else {
                        et_email.error = getString(R.string.error_required)
                    }
                } else {
                    et_nome.error = getString(R.string.error_required)
                }
            } else {
                val toast = Toast.makeText(v.context, getText(R.string.checkin_invalid), Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 20)
                toast.show()
                finish()
            }
        }
        bt_checkin.setOnClickListener(btClickListener)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}