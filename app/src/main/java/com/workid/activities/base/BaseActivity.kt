

package com.workid.activities.base

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.workid.R
import com.workid.utils.Common

@Suppress("DEPRECATION")
open class BaseActivity:AppCompatActivity(),LifecycleObserver {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Handle Fullscreen Mode
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                setDecorFitsSystemWindows(true)
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            }else{
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            statusBarColor = Color.TRANSPARENT
        }
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun finish() {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        super.finish()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        Common.isForeground = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        Common.isForeground = true
    }
}