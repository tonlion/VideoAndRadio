package com.zhang.video.voice

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.TextView
import com.zhang.video.voice.`interface`.RequestService
import com.zhang.video.voice.service.RadioService
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    var handler: Handler? = null
    lateinit var adapter:FMAdapter
    lateinit var data :MutableList<MutableMap<String,String>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        data = ArrayList()
        adapter = FMAdapter(this,data)
        fm_List.adapter = adapter
        fm_List.onItemClickListener = AdapterView.OnItemClickListener { p0, p1, p2, p3 ->
            val intent = Intent(this@MainActivity,RadioService::class.java)
            intent.putExtra("channelid",data[p2]["channel_key"])
            startService(intent)
        }
        var builder = Retrofit.Builder().baseUrl("http://api.kufm.cn/").addConverterFactory(GsonConverterFactory.create())
        var retrofit = builder.client(OkHttpClient()).build()
        var client = retrofit.create(RequestService::class.java)
        val call = client.getChannel()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {}

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                var body = response?.body()
                var body2 = JSONObject(body?.string())
                var program_info_list = body2.get("program_info_list") as JSONArray
                for (i in 0..program_info_list.length()-1){
                    var map = HashMap<String,String>()
//                    map.put("category_key",(program_info_list[i] as JSONObject)["category_key"] as String)
                    map.put("channel_image_url",(program_info_list[i] as JSONObject)["channel_image_url"] as String)
                    map.put("channel_key",("${(program_info_list[i] as JSONObject)["channel_key"]}"))
                    map.put("channel_name","${(program_info_list[i] as JSONObject)["channel_name"]}")
                    map.put("hot_channel","${(program_info_list[i] as JSONObject)["hot_channel"]}")
                    map.put("hz",(program_info_list[i] as JSONObject)["hz"] as String)
                    map.put("listener_count","${(program_info_list[i] as JSONObject)["listener_count"]}")
                    map.put("program_key","${(program_info_list[i] as JSONObject)["program_key"]}")
                    map.put("program_name",(program_info_list[i] as JSONObject)["program_name"] as String)
                    map.put("type","${(program_info_list[i] as JSONObject)["type"]}")
                    data.add(map)
                    runOnUiThread { adapter.notifyDataSetChanged() }
                }
            }

        })
    }

    class FMAdapter(activity: Activity, data: MutableList<MutableMap<String, String>>) : BaseAdapter() {
        var data :MutableList<MutableMap<String, String>> = data
        var activity:Activity = activity

        override fun getItem(p0: Int): Any {
            return data[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return data.count()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var holder:ViewHolder
            var view:View
            if (convertView == null) {
                holder = ViewHolder()
                view = LayoutInflater.from(activity).inflate(R.layout.video_item,null)
                holder.tv = view.findViewById(R.id.video_name)
                view.tag = holder
            } else {
                view = convertView
                holder = view.tag as ViewHolder
            }
            holder.tv.text  = data[position]["channel_name"]
            return view
        }
        class ViewHolder {
            lateinit var tv:TextView
        }
    }
}
