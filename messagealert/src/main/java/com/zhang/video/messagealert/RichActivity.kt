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
import android.text.Editable
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

    var alertType = -1
    @RequiresApi(Build.VERSION_CODES.N)

    private val click = View.OnClickListener { v ->
        when (v!!.id){
            R.id.notification-> {
//                Toast.makeText(this@RichActivity, "通知", Toast.LENGTH_LONG).show()
                dialog.dismiss()
                getTime {
                    time_display.text = it
                    time_display.visibility = View.VISIBLE
                    alertType = 0
                }
            }
            R.id.screenoff->{
                alertType = 1
//                Toast.makeText(this@RichActivity,"锁屏",Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
            R.id.wallpaper->{
                alertType = 2
//                Toast.makeText(this@RichActivity,"更换壁纸",Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
            R.id.alert->{

//                Toast.makeText(this@RichActivity,"闹铃提醒",Toast.LENGTH_LONG).show()
                dialog.dismiss()
                getTime {
                    time_display.text = it
                    time_display.visibility = View.VISIBLE
                    alertType = 3
                }
            }
        }
    }
    fun getTime(deal: (String) -> Unit){
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

        var intent = getIntent()
        if (intent.getStringExtra("detail")!=null) {
            var json = JSON.parseObject(intent.getStringExtra("detail"))
            mEditor.html = json["desc"].toString()
            title_main.setText(json["title"].toString())
        }
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
                db.execSQL("insert into notes(title,desc,time,alerttime, alerttype) values(?,?,?,?,?)"!!, arrayOf(title_main.text,mEditor.html,Date().time,alerttime,alertType))
                var cursor = db.rawQuery("SELECT last_insert_rowid()",null)
                cursor.moveToFirst()
                var id = cursor.getInt(0)
                if(alertType == 0 || alertType == 3){
                    var intent = Intent(this,AlermService::class.java)
                    intent.putExtra("id",id)
                    intent.putExtra("type",alertType)
                    intent.putExtra("time",alerttime)
                    startService(intent)
                }
//                id +=1
                cursor.close()
            } catch (e:Exception){
                Log.e("出错","${e.message}")
            }finally {
                db.close()
            }
            if (alertType == 0 ) {
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


        findViewById<View>(R.id.action_undo).setOnClickListener { mEditor.undo() }

        findViewById<View>(R.id.action_redo).setOnClickListener { mEditor.redo() }

        findViewById<View>(R.id.action_bold).setOnClickListener { mEditor.setBold() }

        findViewById<View>(R.id.action_italic).setOnClickListener { mEditor.setItalic() }

        findViewById<View>(R.id.action_subscript).setOnClickListener { mEditor.setSubscript() }

        findViewById<View>(R.id.action_superscript).setOnClickListener { mEditor.setSuperscript() }

        findViewById<View>(R.id.action_strikethrough).setOnClickListener { mEditor.setStrikeThrough() }

        findViewById<View>(R.id.action_underline).setOnClickListener { mEditor.setUnderline() }

        findViewById<View>(R.id.action_heading1).setOnClickListener { mEditor.setHeading(1) }

        findViewById<View>(R.id.action_heading2).setOnClickListener { mEditor.setHeading(2) }

        findViewById<View>(R.id.action_heading3).setOnClickListener { mEditor.setHeading(3) }

        findViewById<View>(R.id.action_heading4).setOnClickListener { mEditor.setHeading(4) }

        findViewById<View>(R.id.action_heading5).setOnClickListener { mEditor.setHeading(5) }

        findViewById<View>(R.id.action_heading6).setOnClickListener { mEditor.setHeading(6) }

        findViewById<View>(R.id.action_txt_color).setOnClickListener(object : View.OnClickListener {
            private var isChanged: Boolean = false

            override fun onClick(v: View) {
                mEditor.setTextColor(if (isChanged) Color.BLACK else Color.RED)
                isChanged = !isChanged
            }
        })

        findViewById<View>(R.id.action_bg_color).setOnClickListener(object : View.OnClickListener {
            private var isChanged: Boolean = false

            override fun onClick(v: View) {
                mEditor.setTextBackgroundColor(if (isChanged) Color.TRANSPARENT else Color.YELLOW)
                isChanged = !isChanged
            }
        })

        findViewById<View>(R.id.action_indent).setOnClickListener { mEditor.setIndent() }

        findViewById<View>(R.id.action_outdent).setOnClickListener { mEditor.setOutdent() }

        findViewById<View>(R.id.action_align_left).setOnClickListener { mEditor.setAlignLeft() }

        findViewById<View>(R.id.action_align_center).setOnClickListener { mEditor.setAlignCenter() }

        findViewById<View>(R.id.action_align_right).setOnClickListener { mEditor.setAlignRight() }

        findViewById<View>(R.id.action_blockquote).setOnClickListener { mEditor.setBlockquote() }

        findViewById<View>(R.id.action_insert_bullets).setOnClickListener { mEditor.setBullets() }

        findViewById<View>(R.id.action_insert_numbers).setOnClickListener { mEditor.setNumbers() }

        findViewById<View>(R.id.action_insert_image).setOnClickListener {
            mEditor.insertImage("http://www.1honeywan.com/dachshund/image/7.21/7.21_3_thumb.JPG",
                    "dachshund")
        }

        findViewById<View>(R.id.action_insert_link).setOnClickListener { mEditor.insertLink("https://github.com/wasabeef", "wasabeef") }
        findViewById<View>(R.id.action_insert_checkbox).setOnClickListener { mEditor.insertTodo() }
    }
}