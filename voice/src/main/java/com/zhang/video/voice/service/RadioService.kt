package com.zhang.video.voice.service

import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.support.annotation.RequiresApi
import android.util.Log
import kotlin.math.log

class RadioService : Service(),MediaPlayer.OnBufferingUpdateListener,MediaPlayer.OnPreparedListener{
    override fun onBufferingUpdate(p0: MediaPlayer?, p1: Int) {}

    override fun onPrepared(p0: MediaPlayer?) {
        Log.e("开始","已经开始了")
        media.start()
    }

    lateinit var media:MediaPlayer
    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate() {
        super.onCreate()

        Log.e("开始","oncreateservice这是")
        media = MediaPlayer()
        var attribute = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
        media.setAudioAttributes(attribute)
        media.setOnBufferingUpdateListener(this)
        media.setOnPreparedListener(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.e("开始","startservice这是")
        val key = intent!!.extras["channel_key"]
        try {

            media.reset()
            media.setDataSource("http://api.kufm.cn/program/channel_date_list3/2352")
            media.prepareAsync()
        } catch(e:Exception) {
            Log.e("异常问题",e.message)
        }
//        media.start()

        return super.onStartCommand(intent, flags, startId)
    }
}