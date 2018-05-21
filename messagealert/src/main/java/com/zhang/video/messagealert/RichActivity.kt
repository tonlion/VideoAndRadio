package com.zhang.video.messagealert

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.alibaba.fastjson.JSON
import jp.wasabeef.richeditor.RichEditor
import kotlinx.android.synthetic.main.activity_rich.*
import kotlinx.android.synthetic.main.dialog_clock.view.*
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class RichActivity : AppCompatActivity(){

    private lateinit var mEditor: RichEditor
    var width:Int = 0
    var height:Int = 0
    lateinit var dialog:Dialog

    var id = -1

    var alertType = -1
    @RequiresApi(Build.VERSION_CODES.N)

    private val click = View.OnClickListener { v ->
        when (v!!.id){
            R.id.notification-> {
                dialog.dismiss()
                getTime {
                    time_display.text = it
                    time_content.visibility = View.VISIBLE
                    alertType = 0
                }
            }
            R.id.screenoff->{
                alertType = 1
                dialog.dismiss()
            }
            R.id.wallpaper->{
                alertType = 2
                dialog.dismiss()
            }
            R.id.alert->{
                dialog.dismiss()
                getTime {
                    time_display.text = it
                    time_content.visibility = View.VISIBLE
                    alertType = 3
                }
            }
        }
    }
    private fun getTime(deal: (String) -> Unit){
        var time:StringBuffer = StringBuffer()
        var calendar = Calendar.getInstance()
        var dialog1 = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            time.append("${year}-${if((month+1)>10) month+1 else "0${month+1}"}-${if(dayOfMonth>10) dayOfMonth else "0${dayOfMonth}"}")
            var dialog2 = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                time.append(" ${if(hourOfDay>10) hourOfDay else "0${hourOfDay}"}:${if (minute>10) minute else "0${minute}"}:00")
                deal(time.toString())

            },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE)
                    ,true).show()
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)
                ,calendar.get(Calendar.DAY_OF_MONTH)).show()
    }
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rich)
        setSupportActionBar(bar)

        mEditor = findViewById<View>(R.id.editor) as RichEditor
        mEditor.setEditorFontSize(16)
        mEditor.setEditorFontColor(Color.BLACK)
        mEditor.setPadding(10, 10, 10, 10)
        mEditor.setPlaceholder("内容")

//        var intent = intentnt
        if (intent.getStringExtra("detail")!=null) {
            var json = JSON.parseObject(intent.getStringExtra("detail"))
            mEditor.html = json["desc"].toString()
            title_main.setText(json["title"].toString())
            id = json["id"].toString().toInt()
        }
        time_close.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                alertType = -1
                time_display.text = "0"
                time_content.visibility = View.GONE
            }
        })
        submit.setOnClickListener {
            if (mEditor.html == "") {
                return@setOnClickListener
            }
            var helper = DataBase(this)
            var db = helper.writableDatabase
            var alerttime = ""
            if (time_display.text.length>3){
                var format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                alerttime = "${format.parse(time_display.text.toString()).time}"
            }
            try {
                if (id>-1) {
                    db.execSQL("update notes set title=?,desc=?,time=?,alerttime=?,alerttype=? where id=?"!!, arrayOf(title_main.text,mEditor.html,Date().time,alerttime,alertType,id))
                } else {
                    db.execSQL("insert into notes(title,desc,time,alerttime, alerttype) values(?,?,?,?,?)"!!, arrayOf(title_main.text, mEditor.html, Date().time, alerttime, alertType))
                    var cursor = db.rawQuery("SELECT last_insert_rowid()", null)
                    cursor.moveToFirst()
                    var id = cursor.getInt(0)
                    if (alertType == 0 || alertType == 3) {
                        var intent = Intent(this, AlermService::class.java)
                        intent.putExtra("id", id)
                        intent.putExtra("type", alertType)
                        intent.putExtra("time", alerttime)
                        startService(intent)
                    }
                    cursor.close()
                }
            } catch (e:Exception){
                Log.e("出错","${e.message}")
            }finally {
                db.close()
            }
            if (alertType != 0 ) {
                this.findViewById<EditText>(R.id.title_main).isFocusableInTouchMode = true
                this.findViewById<EditText>(R.id.title_main).requestFocus()
                val view = this.window.decorView
                view.isDrawingCacheEnabled = true
                view.buildDrawingCache()
                var bitmap = view.drawingCache
                if (bitmap != null) {
                    val location = IntArray(2)
                    mEditor.getLocationOnScreen(location)
                    bitmap = Bitmap.createBitmap(bitmap, location[0], location[1], mEditor.width, mEditor.height)
                    if (bitmap.height > this.resources.displayMetrics.heightPixels - 100) {
                        Toast.makeText(this, "当前文本过多，影响显示", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                } else {
                    return@setOnClickListener;
                }
                var wall = WallpaperManager.getInstance(this)
                var c = ((wall.drawable) as BitmapDrawable).bitmap
                width = c.width
                height = c.height
                var bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
                var canvas = Canvas(bm)
                canvas.drawColor(Color.WHITE)
                var paint = Paint()
                canvas.drawBitmap(bitmap, Rect(0, 0, bitmap.width, bitmap.height), Rect(0, (this.resources.displayMetrics.heightPixels - bitmap.height) / 2, bitmap.width, (this.resources.displayMetrics.heightPixels + bitmap.height) / 2), paint)
                bitmap = null
                if (alertType == 2)
                    wall.setBitmap(bm)

                var file = File(Environment.getExternalStorageDirectory(), "temp.jpg")
                var out = FileOutputStream(file)
                bm.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
                bm = null
                if (alertType == 1)
                    startService(Intent(this, ScreenService::class.java))
            }
            finish()
        }
        clock.setOnClickListener {
            dialog = Dialog(this,android.R.style.Theme_Material_Light_Dialog)
            val view = layoutInflater.inflate(R.layout.dialog_clock,null)
            dialog.addContentView(view, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT))
            view.notification.setOnClickListener(click)
            view.alert.setOnClickListener(click)
            view.screenoff.setOnClickListener(click)
            view.wallpaper.setOnClickListener(click)
            dialog.setTitle("提醒")
            dialog.show()
        }
        back.setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.action_bold).setOnClickListener { mEditor.setBold() }

        findViewById<View>(R.id.action_italic).setOnClickListener { mEditor.setItalic() }

        findViewById<View>(R.id.action_strikethrough).setOnClickListener { mEditor.setStrikeThrough() }

        findViewById<View>(R.id.action_underline).setOnClickListener { mEditor.setUnderline() }

        findViewById<View>(R.id.action_insert_bullets).setOnClickListener { mEditor.setBullets() }

        findViewById<View>(R.id.action_insert_numbers).setOnClickListener { mEditor.setNumbers() }
    }
}