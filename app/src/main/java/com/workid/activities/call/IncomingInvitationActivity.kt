package com.workid.activities.call

import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.workid.R
import com.workid.activities.base.BaseActivity
import com.workid.broadcast.ResponseReceiver
import com.workid.databinding.ActivityIncomingInvititationBinding
import com.workid.models.RequestCallItems
import com.workid.utils.*
import com.workid.utils.Constant.Companion.NOTIFICATION_REQUEST_CALL_ID
import com.workid.utils.Constant.Companion.REMOTE_MSG_CALL_RESPONSE
import com.workid.utils.Constant.Companion.REMOTE_RESPONSE_ACCEPTED
import com.workid.utils.Constant.Companion.REMOTE_RESPONSE_MISSED
import com.workid.utils.Constant.Companion.REMOTE_RESPONSE_REJECTED
import org.jitsi.meet.sdk.JitsiMeetActivity
import timber.log.Timber

class IncomingInvitationActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityIncomingInvititationBinding
    private lateinit var viewModel: InvitationViewModel
    private lateinit var data :RequestCallItems

    private val progressDrawable by lazy{ getProgressDrawable(this)}
    private val handler by lazy { Handler(Looper.myLooper()!!) }
    private val invitationResponseReceiver by lazy { ResponseReceiver() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        turnScreenOnAndKeyguardOff()

        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                setDecorFitsSystemWindows(false)
            } else {
                @Suppress("DEPRECATION")
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            statusBarColor = Color.TRANSPARENT
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_incoming_invititation)
        binding.incomingCallerAccept.setOnClickListener(this)
        binding.incomingCallerReject.setOnClickListener(this)
        viewModel = ViewModelProvider(this).get(InvitationViewModel::class.java)

        //Retrieve Data from intent
        data = IntentUtils.retrieveDataFromIntent(intent)
        Timber.tag("IncomingInvitationActivity").d("onCreate: data : $data")

        //Display UI accordingly
        if (data.meetingType == "video") {
            binding.incomingCallerAccept.setLottieImage(R.raw.caller_incoming_video_call)
        }
        binding.incomingCallerName.text = data.callerName
        binding.incomingMessage.text =
            getString(R.string.incoming_message, data.meetingType, data.callerName)
        binding.outgoingCallerAvatar.loadImage(data.callerPhotoURL, progressDrawable)
        observeViewModel()
    }

    private fun observeViewModel() {
        //Observe call status (Missed call)
        invitationResponseReceiver.responseStatus.observe(this, { responseStatus ->
            if (responseStatus == REMOTE_RESPONSE_MISSED) {
                //Caller cancel call request
                Toast.makeText(this, "Call ended by caller", Toast.LENGTH_LONG).show()
                handler.postDelayed({
                    finish()
                }, 500)
            }
        })
        //Establish connection with Jitsi
        JitsiMeetUtils.establishConnection()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.incomingCallerAccept -> {
                //Accepted call - send confirmation back to the caller
                data.response = REMOTE_RESPONSE_ACCEPTED
                cancelNotification(NOTIFICATION_REQUEST_CALL_ID)
                viewModel.sendResponseRequestCall(data.callId!!, data.response!!, data.meetingId!!)
                //Both parties accepted call , handle connection...
                Toast.makeText(this, "Call connecting", Toast.LENGTH_LONG).show()
                val jitsiConnection = JitsiMeetUtils.startMeeting(data)
                JitsiMeetActivity.launch(this, jitsiConnection)
                finish()
            }
            binding.incomingCallerReject -> {
                //Rejected call - send confirmation back to the caller
                data.response = REMOTE_RESPONSE_REJECTED
                cancelNotification(NOTIFICATION_REQUEST_CALL_ID)
                binding.incomingMessage.text = getString(R.string.rejected_call)
                viewModel.sendResponseRequestCall(data.callId!!, data.response!!, data.meetingId!!)
                handler.postDelayed({
                    finish()
                }, 1000)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
            invitationResponseReceiver,
            IntentFilter(REMOTE_MSG_CALL_RESPONSE)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        turnScreenOffAndKeyguardOn()
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(
            invitationResponseReceiver
        )
    }
}