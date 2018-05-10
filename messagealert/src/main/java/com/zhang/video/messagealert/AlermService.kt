package com.zhang.video.messagealert

import android.app.AlarmManager
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder

class AlermService : Service() {

    val GRAY_SERVICE_ID = 0X1212
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        //守护进程
//        Daemon


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var time = intent!!.getLongExtra("time",0)
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var pintent = PendingIntent.getBroadcast(this,2, Intent(this,AlarmReceiver::class.java),0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {


            am.setExact(AlarmManager.RTC_WAKEUP,time,pintent)
        } else {
            am.set(AlarmManager.RTC_WAKEUP,time,pintent)
        }
        return super.onStartCommand(intent, flags, startId)
    }
    //包活
    fun saveLife(){
        if (Build.VERSION.SDK_INT < 18) {
            //API < 18 ，此方法能有效隐藏Notification上的图标
            startForeground(GRAY_SERVICE_ID, Notification());
        } else {
            var innerIntent =  Intent(this, AlermService::class.java)
            startService(innerIntent);
            startForeground(GRAY_SERVICE_ID,  Notification());
        }
        var alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    class AlarmReceiver : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            var notification  = Notification()
        }

    }
}
