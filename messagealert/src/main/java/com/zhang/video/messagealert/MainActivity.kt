//package com.zhang.video.messagealert
//
//import android.app.WallpaperManager
//import android.graphics.*
//import android.graphics.drawable.BitmapDrawable
//import android.os.Build
//import android.support.v7.app.AppCompatActivity
//import android.os.Bundle
//import android.support.annotation.RequiresApi
//import android.util.Log
//import android.view.View
//import kotlinx.android.synthetic.main.activity_main.*
//import android.os.Environment
//import android.widget.Toast
//import android.widget.ImageButton
//import io.github.mthli.knife.KnifeText
//import java.io.File
//import java.io.FileOutputStream
//import java.util.regex.Pattern
//
//
//class MainActivity : AppCompatActivity() {
//
//
//    private val BOLD = "<b>Bold</b><br><br>"
//    private val ITALIT = "<i>Italic</i><br><br>"
//    private val UNDERLINE = "<u>Underline</u><br><br>"
//    private val STRIKETHROUGH = "<s>Strikethrough</s><br><br>" // <s> or <strike> or <del>
//    private val BULLET = "<ul><li>asdfg</li></ul>"
//    private val QUOTE = "<blockquote>Quote</blockquote>"
//    private val LINK = "<a href=\"https://github.com/mthli/Knife\">Link</a><br><br>"
//    private val EXAMPLE = BOLD + ITALIT + UNDERLINE + STRIKETHROUGH + BULLET + QUOTE + LINK
//    var width:Int = 0
//    var height:Int = 0
//    @RequiresApi(Build.VERSION_CODES.N)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        knife.fromHtml(EXAMPLE);
//        knife.setSelection(knife.getEditableText().length);
//        setupBold()
//        setupItalic()
//        setupUnderline()
//        setupStrikethrough()
//        setupBullet()
//        setupQuote()
//        setupLink()
//        setupClear()
//
//
//        button2.setOnClickListener(View.OnClickListener {
//            val view  = this.window.decorView
//            view.isDrawingCacheEnabled = true
//            view.measure(
//                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//            view.layout(0, 0, view.measuredWidth,
//                    view.measuredHeight);
//            view.buildDrawingCache()
//            var bitmap = view.drawingCache
//            if (bitmap!= null) {
//                val location = IntArray(2)
//                knife.getLocationOnScreen(location)
//                Log.e("knife的宽高：","${knife.width};${knife.height}")
//                knife.width = this.resources.displayMetrics.widthPixels
//                Log.e("屏幕的宽高：","${this.resources.displayMetrics.widthPixels};${this.resources.displayMetrics.heightPixels}")
//                bitmap = Bitmap.createBitmap(bitmap, location[0], location[1], knife.width, knife.height)
//                var file = File(Environment.getExternalStorageDirectory(), "ccc.jpg")
//                var out = FileOutputStream(file)
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
//                out.flush()
//                out.close()
//            }
//            var wall = WallpaperManager.getInstance(this)
//            var c = ((wall.drawable)as BitmapDrawable).bitmap
//            width = c.width
//            height = c.height
//            var bm = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_4444)
//            var canvas = Canvas(bm)
//            canvas.drawColor(Color.WHITE)
//            var paint = Paint()
//            paint.color = Color.BLACK
//            paint.textSize = 40f
//            canvas.drawBitmap(bitmap, Rect(0,0,bitmap.width,bitmap.height),Rect(0,50,bitmap.width,50+bitmap.height),paint)
//
////            Log.e("打印结果",knife.text.toString())
////            paintText(canvas,100f,paint)
////            if (textView2.text.length < height/120){
////                Log.e("现在是120","${height/120}")
////                paintText(canvas,100f,paint)
////            } else if (textView2.text.length < height/100){
////                Log.e("现在是100","${height/100}")
////                paintText(canvas,80f,paint)
////            }else if (textView2.text.length < height/80){
////                Log.e("现在是80","${height/80}")
////                paintText(canvas,60f,paint)
////            }else  {
////                Log.e("现在是60","${height/60}")
////                if (textView2.text.contains("\n")) {
////                    var c = textView2.text.split(Pattern.compile("\n"),textView2.text.length-1)
////                    Log.e("写点啥","${c}")
////                    paint.textSize = 40f
////                    var index = 0
////                    var relax =  height - textView2.text.length * (40f+20)
////                    for (i in c) {
////                        canvas.drawText(i.toString(), width.toFloat(), 50f, paint)
////                        index ++
////                    }
////                }
////
////            }
//            wall.setBitmap(bm)
//        })
//    }
//    fun paintText(canvas:Canvas,textSize:Float,paint:Paint){
//        paint.textSize = textSize
//        var index = 0
////        var relax =  height - textView2.text.length * (textSize+20)
//        for (i in knife.toHtml()) {
//            canvas.drawText(i.toString(), (width - textSize) / 2, (50 / 2).toFloat() +((textSize+10)*index), paint)
//            index ++
//        }
//    }
//
//    private fun setupBold() {
//        val bold = findViewById<View>(R.id.bold) as ImageButton
//
//        bold.setOnClickListener { knife.bold(!knife.contains(KnifeText.FORMAT_BOLD)) }
//
//        bold.setOnLongClickListener {
//            Toast.makeText(this@MainActivity, R.string.toast_bold, Toast.LENGTH_SHORT).show()
//            true
//        }
//    }
//
//    private fun setupItalic() {
//        val italic = findViewById<View>(R.id.italic) as ImageButton
//
//        italic.setOnClickListener { knife.italic(!knife.contains(KnifeText.FORMAT_ITALIC)) }
//
//        italic.setOnLongClickListener {
//            Toast.makeText(this@MainActivity, R.string.toast_italic, Toast.LENGTH_SHORT).show()
//            true
//        }
//    }
//
//    private fun setupUnderline() {
//        val underline = findViewById<View>(R.id.underline) as ImageButton
//
//        underline.setOnClickListener { knife.underline(!knife.contains(KnifeText.FORMAT_UNDERLINED)) }
//
//        underline.setOnLongClickListener {
//            Toast.makeText(this@MainActivity, R.string.toast_underline, Toast.LENGTH_SHORT).show()
//            true
//        }
//    }
//
//    private fun setupStrikethrough() {
////        val strikethrough = findViewById<View>(R.id.strikethrough) as ImageButton
//
//        strikethrough.setOnClickListener { knife.strikethrough(!knife.contains(KnifeText.FORMAT_STRIKETHROUGH)) }
//
//        strikethrough.setOnLongClickListener {
//            Toast.makeText(this@MainActivity, R.string.toast_strikethrough, Toast.LENGTH_SHORT).show()
//            true
//        }
//    }
//
//    private fun setupBullet() {
////        val bullet = findViewById<View>(R.id.bullet) as ImageButton
//
//        bullet.setOnClickListener { knife.bullet(!knife.contains(KnifeText.FORMAT_BULLET)) }
//
//
//        bullet.setOnLongClickListener {
//            Toast.makeText(this@MainActivity, R.string.toast_bullet, Toast.LENGTH_SHORT).show()
//            true
//        }
//    }
//
//    private fun setupQuote() {
////        val quote = findViewById<View>(R.id.quote) as ImageButton
//
//        quote.setOnClickListener { knife.quote(!knife.contains(KnifeText.FORMAT_QUOTE)) }
//
//        quote.setOnLongClickListener {
//            Toast.makeText(this@MainActivity, R.string.toast_quote, Toast.LENGTH_SHORT).show()
//            true
//        }
//    }
//
//    private fun setupLink() {
////        val link = findViewById<View>(R.id.link) as ImageButton
//
////        link.setOnClickListener { showLinkDialog() }
//
//        link.setOnLongClickListener {
//            Toast.makeText(this@MainActivity, R.string.toast_insert_link, Toast.LENGTH_SHORT).show()
//            true
//        }
//    }
//
//    private fun setupClear() {
////        val clear = findViewById<View>(R.id.clear) as ImageButton
//
//        clear.setOnClickListener { knife.clearFormats() }
//
//        clear.setOnLongClickListener {
//            Toast.makeText(this@MainActivity, R.string.toast_format_clear, Toast.LENGTH_SHORT).show()
//            true
//        }
//    }
//
//}
