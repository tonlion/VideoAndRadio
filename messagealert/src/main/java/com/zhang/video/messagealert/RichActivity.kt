package com.zhang.video.messagealert

import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import jp.wasabeef.richeditor.RichEditor
import kotlinx.android.synthetic.main.activity_rich.*

class RichActivity : AppCompatActivity(){

    private lateinit var mEditor: RichEditor
    var width:Int = 0
    var height:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rich)
        mEditor = findViewById<View>(R.id.editor) as RichEditor
        mEditor.setEditorHeight(200)
        mEditor.setEditorFontSize(16)
        mEditor.setEditorFontColor(Color.BLACK)
        mEditor.setPadding(10, 10, 10, 10)
        mEditor.setPlaceholder("Insert text here...")
        btn.setOnClickListener {
            val view  = this.window.decorView
            view.isDrawingCacheEnabled = true
//            view.measure(
//                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//            view.layout(0, 0, view.measuredWidth,
//                    view.measuredHeight);
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
                Log.e("正常显示：","我就是没有其他信息")
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
            paint.color = Color.BLACK
            paint.textSize = 40f

            canvas.drawBitmap(bitmap, Rect(0,0,bitmap.width,bitmap.height),Rect(0,(this.resources.displayMetrics.heightPixels-bitmap.height)/2,bitmap.width,(this.resources.displayMetrics.heightPixels+bitmap.height)/2),paint)
            bitmap = null
            wall.setBitmap(bm)
        }
        val c = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                if (intent.action.equals(Intent.ACTION_SCREEN_OFF)) {
                    val mLockIntent = Intent(p0, LockScreenActivity::class.java)
                    mLockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                    startActivity(mLockIntent)
                }
            }

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