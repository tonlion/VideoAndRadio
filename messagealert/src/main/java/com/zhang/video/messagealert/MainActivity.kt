package com.zhang.video.messagealert

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.io.OutputStream
import java.util.regex.Pattern
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    var width:Int = 0
    var height:Int = 0
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        button2.setOnClickListener(View.OnClickListener {
            var wall = WallpaperManager.getInstance(this)
            var c = ((wall.drawable)as BitmapDrawable).bitmap
            width = c.width
            height = c.height
            var bm = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_4444)
            var canvas = Canvas(bm)
            canvas.drawColor(Color.WHITE)
            var paint = Paint()
            paint.color = Color.BLACK
            if (textView2.text.length < height/120){
                Log.e("现在是120","${height/120}")
                paintText(canvas,100f,paint)
            } else if (textView2.text.length < height/100){
                Log.e("现在是100","${height/100}")
                paintText(canvas,80f,paint)
            }else if (textView2.text.length < height/80){
                Log.e("现在是80","${height/80}")
                paintText(canvas,60f,paint)
            }else  {
                Log.e("现在是60","${height/60}")
                if (textView2.text.contains("\n")) {
                    var c = textView2.text.split(Pattern.compile("\n"),textView2.text.length-1)
                    Log.e("写点啥","${c}")
                    paint.textSize = 40f
                    var index = 0
                    var relax =  height - textView2.text.length * (40f+20)
                    for (i in c) {
                        canvas.drawText(i.toString(), width.toFloat(), 50f, paint)
                        index ++
                    }
                }

            }
            wall.setBitmap(bm)
        })
    }
    fun paintText(canvas:Canvas,textSize:Float,paint:Paint){
        paint.textSize = textSize
        var index = 0
        var relax =  height - textView2.text.length * (textSize+20)
        for (i in textView2.text) {
            canvas.drawText(i.toString(), (width - textSize) / 2, (relax / 2).toFloat() +((textSize+10)*index), paint)
            index ++
        }
    }
}
