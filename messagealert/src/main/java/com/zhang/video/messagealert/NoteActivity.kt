package com.zhang.video.messagealert

import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.alibaba.fastjson.JSON

import kotlinx.android.synthetic.main.activity_note.*
import kotlinx.android.synthetic.main.dialog_note_item_menu.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class NoteActivity : AppCompatActivity() {

    var adapter:NoteAdapter? = null
    lateinit var notes:ArrayList<HashMap<String,Object>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        setSupportActionBar(toolbar)
        note_list.layoutManager = LinearLayoutManager(this)
        note_list.itemAnimator = DefaultItemAnimator()

        notes = ArrayList<HashMap<String,Object>>()
        adapter = NoteAdapter(this,notes,note_list.parent as ViewGroup)
        note_list.adapter = adapter
        fab.setOnClickListener { view ->
           var intent = Intent(this,RichActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        notes.clear()
        var dbhelper = DataBase(this)
        var db = dbhelper.writableDatabase
        var cursor = db.rawQuery("select * from notes order by id DESC"!!,null)
        while (cursor.moveToNext()) {
            var map = HashMap<String,Object>()
            map["title"] = cursor.getString(cursor.getColumnIndex("title")) as Object
            map["desc"] = cursor.getString(cursor.getColumnIndex("desc")) as Object
            map["time"] = cursor.getLong(cursor.getColumnIndex("time")) as Object
            map["id"] = cursor.getLong(cursor.getColumnIndex("id")) as Object
            notes.add(map)

        }
        cursor.close()
        db.close()
        adapter!!.notifyDataSetChanged()
    }
    class NoteAdapter:RecyclerView.Adapter<NoteAdapter.ViewHolder>{
        var context:Context
        var data:List<Map<String,Object>>
        var rootView:ViewGroup

        constructor(context: Context, data: List<Map<String, Object>>,rootView:ViewGroup) {
            this.context = context
            this.data = data
            this.rootView = rootView
        }


        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            var view = LayoutInflater.from(context).inflate(R.layout.layout_note,parent,false)
            val holder = ViewHolder(view)
            return holder
        }

        override fun getItemCount(): Int {
            return data.size
        }

        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            holder!!.title.text = data[position]["title"].toString()
            var desc = data[position]["desc"].toString()
            var pattern = Pattern.compile("<[^>]*>")
            var match = pattern.matcher(desc)
            while (match.find()){
                desc = desc.replace(match.group(),"")
            }

            holder!!.desc.text = desc
            var date = Date()
            date.time = (data[position]["time"] as Long)
            var format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

            holder!!.time.text =format.format(date)
            holder.getView().setOnClickListener {
                var intent = Intent(context,RichActivity::class.java)
                intent.putExtra("detail",JSON.toJSONString(data[position]))
                var option = ActivityOptionsCompat.makeScaleUpAnimation(it,it.x.toInt(),it.y.toInt(),it.width,it.height)
                context.startActivity(intent,option.toBundle())

            }
//            holder.getView().setOnLongClickListener {
////                var vi = LayoutInflater.from(context).inflate(R.layout.dialog_note_menu,null,false)
////                if (it.top > 10){
//////                    vi.y+vi.height-10
////                }
//
//            }
            holder.getView().setOnLongClickListener { v ->
                var vi = LayoutInflater.from(context).inflate(R.layout.dialog_note_item_menu,null,false)
//                var lp = ViewGroup.LayoutParams()
//                vi.setBackgroundColor(Color.LTGRAY)
                Log.e("子控件的数量前","${rootView.childCount}")
                if (v!!.bottom>50){
                    Toast.makeText(context,"长按了1",Toast.LENGTH_SHORT).show()
                    rootView.addView(vi,RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT))
                    vi.delete_bottom.scrollTo(v.width/2,v.y.toInt())
                    Log.e("子控件的数量后","${rootView.childCount}")
                    vi.setOnClickListener {
                        //                            if (rootView
                        rootView.removeView(vi)
                    }
                } else {
                    Toast.makeText(context,"长按了2",Toast.LENGTH_SHORT).show()
                    rootView.addView(vi,0)
                }
                true
            }
        }

        class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
            val title = itemView!!.findViewById<TextView>(R.id.note_title)!!
            val desc = itemView!!.findViewById<TextView>(R.id.note_desc)!!
            val time = itemView!!.findViewById<TextView>(R.id.note_time)!!

            fun getView():View {
                return itemView
            }
        }
    }

}
