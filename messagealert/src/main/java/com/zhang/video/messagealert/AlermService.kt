package com.zhang.video.messagealert

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.util.Log
import com.zhang.video.messagealert.AlermService.list.NOTIFICATION

class AlermService : Service() {

    val GRAY_SERVICE_ID = 0X1212
    object list{
        val NOTIFICATION = "com.zhang.alarm.notification"
    }
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        //守护进程
//        Daemon


        var filter = IntentFilter()
        filter.addAction(NOTIFICATION)
        registerReceiver(AlarmReceiver(), filter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var time = intent!!.getStringExtra("time").toLong()
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var int = Intent(this,AlarmReceiver::class.java)
        int.putExtra("id",intent!!.getIntExtra("id",-1))
        int.putExtra("type",intent!!.getIntExtra("type",-1))
        int.action = NOTIFICATION
        var pintent = PendingIntent.getBroadcast(this,2, int,0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            Log.e("这是高版本","显示吧")
            am.setExact(AlarmManager.RTC_WAKEUP,time,pintent)
        } else {
            Log.e("这是低版本","显示吧")
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
        @SuppressLint("NewApi")
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.e("出来吧","小同志")
            if (intent!!.action == NOTIFICATION) {
                if (intent!!.getIntExtra("id", -1) == 0) {
                    var id = intent.getIntExtra("id", -1)
                    var helper = DataBase(context!!)
                    var db = helper.readableDatabase
                    var cursor = db.rawQuery("select title,desc from notes where id = ?"!!, arrayOf("${id}"))
                    cursor.moveToFirst()
                    var nm = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    var channel = nm.createNotificationChannel(NotificationChannel("200", "noti", NotificationManager.IMPORTANCE_HIGH))
                    var nb = Notification.Builder(context, "200").setContentTitle(cursor.getString(0)).setContentText(cursor.getString(1)).build()
                    nm.notify(1, nb)
                    cursor.close()
                    db.close()
                } else {
                    var int = Intent(context, AlarmActivity::class.java)
                    int.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                    context!!.startActivity(int)
                }
            }
        }

    }
}
