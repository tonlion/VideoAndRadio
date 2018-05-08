package com.zhang.video.messagealert

import android.app.KeyguardManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.WindowManager

import kotlinx.android.synthetic.main.activity_lock_screen.*

class LockScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)
        setSupportActionBar(toolbar)
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val key = event!!.keyCode
        when (key) {
            KeyEvent.KEYCODE_BACK -> {
                return true
            }
            KeyEvent.KEYCODE_MENU -> {
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val action = event!!.getAction()
        val nx = event!!.getX()
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mStartX = nx
                onAnimationEnd()
                handleMoveView(nx)
            }
            MotionEvent.ACTION_MOVE -> handleMoveView(nx)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> doTriggerEvent(nx)
        }
        return true
    }

}
