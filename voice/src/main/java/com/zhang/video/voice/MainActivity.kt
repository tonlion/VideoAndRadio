package com.zhang.video.voice

import android.app.Activity
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.zhang.video.voice.`interface`.RequestService
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        fm_List.adapter = FMAdapter(this,null)

val thread = thread {
    Runnable {
        var builder = Retrofit.Builder().baseUrl("http://api.kufm.cn/").addConverterFactory(GsonConverterFactory.create());
        var retrofit = builder.client(OkHttpClient()).build()
        var client = retrofit.create(RequestService::class.java)
        val call = client.getChannel(1,1)
        var c = call.execute().body()
        print(c.toString())
    }
}
        thread.start()
//        Thread { Runnable {
//            var builder = Retrofit.Builder().baseUrl("http://api.kufm.cn/").addConverterFactory(GsonConverterFactory.create());
//            var retrofit = builder.client(OkHttpClient()).build()
//            var client = retrofit.create(RequestService::class.java)
//            val call = client.getChannel(1,1)
//            var c = call.execute().body()
//            print(c.toString())
//        } }.start()
    }

    class FMAdapter(activity:Activity,data:Map<String,String>?) : BaseAdapter() {

        override fun getItem(p0: Int): Any {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getItemId(p0: Int): Long {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getCount(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }
}
