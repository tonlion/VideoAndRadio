package com.zhang.video.messagealert

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager

import kotlinx.android.synthetic.main.activity_lock_screen.*
import kotlinx.android.synthetic.main.content_lock_screen.*


class LockScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //取消标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //取消状态栏
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        setContentView(R.layout.activity_lock_screen)
//        actionBar.hide()
//        setSupportActionBar(toolbar)

        iv.setImageBitmap(BitmapFactory.decodeFile("${Environment.getExternalStorageDirectory()}/temp.jpg"))
        fab.setOnClickListener { view ->
            finish()
        }
    }

    override fun onBackPressed() {

    }
}
