package com.example.martinschwans.intenteservice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.loading.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.ThreadMode
import org.greenrobot.eventbus.Subscribe
import android.os.BatteryManager







class MainActivity : AppCompatActivity() {

    var receiver = ResponseReceiver()
    var receiverUSBReceiver = PowerConnectionReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val intent = Intent(this, MinhaIntentService::class.java)
        intent.putExtra(MinhaIntentService.PARAM_ENTRADA, "Agora é: ")
        startService(intent)


        registrarReceiver()
    }

    public override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    public override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: Status) {

        when(event) {
            Status.SUCCESS-> {
                containerLoading.visibility = View.GONE
                Toast.makeText(this, "Sucesso", Toast.LENGTH_LONG).show()
            }
            Status.ERROR-> {
                containerLoading.visibility = View.GONE
            }
            Status.LOADING-> {
                containerLoading.visibility = View.VISIBLE
            }

        }

    }

    private fun registrarReceiver() {
        val filter = IntentFilter(MinhaIntentService.ACTION)
        filter.addCategory(Intent.CATEGORY_DEFAULT)


        val filter2 = IntentFilter()
        filter2.addAction(Intent.ACTION_POWER_DISCONNECTED)
        filter2.addAction(Intent.ACTION_POWER_CONNECTED)
        filter2.addCategory(Intent.CATEGORY_DEFAULT)
        registerReceiver(receiverUSBReceiver, filter2)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    inner class ResponseReceiver: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            tv_resultado.text = intent?.getStringExtra(MinhaIntentService.PARAM_SAIDA)
        }
    }

    inner class PowerConnectionReceiver : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {

            /*val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

            val chargePlug = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
            val acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC*/

            when (intent?.action){
                Intent.ACTION_POWER_CONNECTED -> {
                    Toast.makeText(context, "Conectado", Toast.LENGTH_LONG).show()
                }
                Intent.ACTION_POWER_DISCONNECTED -> {
                    Toast.makeText(context, "Desconectado", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
