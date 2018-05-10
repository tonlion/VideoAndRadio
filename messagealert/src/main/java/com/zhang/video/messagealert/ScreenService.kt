package com.zhang.video.messagealert

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder

class ScreenService : Service() {

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        var filter = IntentFilter()
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(c,filter)
    }
    val c = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1!!.action.equals(Intent.ACTION_SCREEN_OFF)) {
                val mLockIntent = Intent(p0, LockScreenActivity::class.java)
                mLockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                startActivity(mLockIntent)
            }
        }

    }
}
