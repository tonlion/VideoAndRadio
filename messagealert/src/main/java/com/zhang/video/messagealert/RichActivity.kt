package com.zhang.video.messagealert

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.WallpaperManager
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import jp.wasabeef.richeditor.RichEditor
import kotlinx.android.synthetic.main.activity_rich.*
import java.io.File
import java.io.FileOutputStream

class RichActivity : AppCompatActivity(){

    private lateinit var mEditor: RichEditor
    var width:Int = 0
    var height:Int = 0
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rich)
        setSupportActionBar(bar)
        bar.title = ""
        startService(Intent(this,ScreenService::class.java))
        mEditor = findViewById<View>(R.id.editor) as RichEditor
        mEditor.setEditorFontSize(20)
        mEditor.setEditorFontColor(Color.BLACK)
        mEditor.setPadding(10, 10, 10, 10)
        mEditor.setPlaceholder("Insert text here...")
        submit.setOnClickListener {
//            this.findViewById<EditText>(R.id.title).focusable = View.FOCUSABLE
            this.findViewById<EditText>(R.id.title).isFocusableInTouchMode = true
            this.findViewById<EditText>(R.id.title).requestFocus()
            val view  = this.window.decorView
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache()
            var bitmap = view.drawingCache
            if (bitmap != null) {
                val location = IntArray(2)
                mEditor.getLocationOnScreen(location)
                bitmap = Bitmap.createBitmap(bitmap, location[0], location[1], mEditor.width, mEditor.height)
                if(bitmap.height>this.resources.displayMetrics.heightPixels-100){
                    Toast.makeText(this,"当前文本过多，影响显示",Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            } else {
                return@setOnClickListener;
            }
            var wall = WallpaperManager.getInstance(this)
            var c = ((wall.drawable)as BitmapDrawable).bitmap
            width = c.width
            height = c.height
            var bm = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_4444)
            var canvas = Canvas(bm)
            canvas.drawColor(Color.WHITE)
            var paint = Paint()
            canvas.drawBitmap(bitmap, Rect(0,0,bitmap.width,bitmap.height),Rect(0,(this.resources.displayMetrics.heightPixels-bitmap.height)/2,bitmap.width,(this.resources.displayMetrics.heightPixels+bitmap.height)/2),paint)
            bitmap = null
            wall.setBitmap(bm)

            var file = File(Environment.getExternalStorageDirectory(),"temp.jpg")
            var out = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.JPEG,100,out)
            out.flush()
            out.close()
            bm = null
        }
        clock.setOnClickListener {
            var dialog = Dialog(this,android.R.style.Theme_Material_Light_Dialog)
            dialog.setTitle("测试")
            dialog.show()
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