package com.workid.broadcast

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.workid.utils.Constant.Companion.REMOTE_MSG_CALL_RESPONSE


class ResponseReceiver : BroadcastReceiver() {

    private val _responseStatus = MutableLiveData<String>()
    val responseStatus: LiveData<String> = _responseStatus

    override fun onReceive(context: Context, intent: Intent) {
        val keyguardManager =
            context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        when (intent.action) {
            REMOTE_MSG_CALL_RESPONSE -> {
                _responseStatus.value = intent.getStringExtra(REMOTE_MSG_CALL_RESPONSE)
            }
        }
    }
}

