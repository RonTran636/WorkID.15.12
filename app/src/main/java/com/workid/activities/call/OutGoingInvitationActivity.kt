package com.workid.activities.call

import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.workid.R
import com.workid.activities.base.BaseActivity
import com.workid.broadcast.ResponseReceiver
import com.workid.databinding.ActivityOutgoingInvitationBinding
import com.workid.utils.*
import com.workid.utils.Constant.Companion.NOTIFICATION_REQUEST_CALL_DURATION
import com.workid.utils.Constant.Companion.REMOTE_MSG_CALL_RESPONSE
import com.workid.utils.Constant.Companion.REMOTE_RESPONSE_ACCEPTED
import com.workid.utils.Constant.Companion.REMOTE_RESPONSE_REJECTED
import org.jitsi.meet.sdk.JitsiMeetActivity
import timber.log.Timber
import java.util.*


class OutGoingInvitationActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityOutgoingInvitationBinding
    private lateinit var viewModel: InvitationViewModel
    private lateinit var meetingId: String
    private val receiverCallId = Common.invitedAccount!!.customerId!!
    private var callId = ""
    private val progressDrawable by lazy { getProgressDrawable(this) }
    private val handler by lazy { Handler(Looper.myLooper()!!) }
    private val invitationResponseReceiver by lazy { ResponseReceiver() }
    private val meetingType by lazy { intent.getStringExtra("type")!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag("OutGoingInvitationActivity").d(": invited customer id: $receiverCallId")
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

        binding = DataBindingUtil.setContentView(this, R.layout.activity_outgoing_invitation)
        binding.outgoingEnd.setOnClickListener(this)
        binding.outgoingMicro.setOnClickListener(this)
        binding.outgoingSpeaker.setOnClickListener(this)

        viewModel = ViewModelProvider(this).get(InvitationViewModel::class.java)

        //Setting up call request
        meetingId =
            UUID.randomUUID().toString().toUpperCase(Locale.getDefault()).substring(0, 8)
        if (Common.invitedUserList.size < 2) {
            //Single Request
            //Display UI accordingly
            binding.outgoingCallerName.text = Common.invitedAccount!!.customerName
            binding.outgoingMessage.text =
                getString(R.string.outgoing_message, Common.invitedAccount!!.customerName)
            binding.outgoingCallerAvatar.loadImage(
                Common.invitedAccount!!.photoUrl,
                progressDrawable
            )
            //Send call request
            Timber.tag("OutGoingInvitationActivity")
                .d("onCreate: server token of ${Common.currentAccount!!.customerId} is ${Common.currentAccount!!.serverToken}")
            viewModel.sendRequestCall(receiverCallId, meetingType, meetingId)
        } else {
            //Multiple request
            //Display UI accordingly
            binding.outgoingCallerName.text = getString(R.string.group_calling)
            binding.outgoingMessage.text = getString(R.string.outgoing_group_call)
            //Send call request
            for (user in Common.invitedUserList){
                viewModel.sendRequestCall(user.customerId!!, meetingType, meetingId)
            }
        }
        observeViewModel()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.outgoingEnd -> {
                binding.outgoingMessage.text = getString(R.string.message_call_end)
                val response = Constant.REMOTE_RESPONSE_MISSED
                viewModel.sendResponseRequestCall(callId, response, meetingId)
                //Send response missed call to the receiver
                handler.postDelayed({
                    finish()
                }, 1000)
            }
        }
    }

    private fun observeViewModel() {
        //Observe call status (Accepted / Rejected)
        invitationResponseReceiver.responseStatus.observe(this, { responseStatus ->
            when (responseStatus) {
                REMOTE_RESPONSE_ACCEPTED -> {
                    //Both parties accepted call - establish connection with Jitsi
                    JitsiMeetUtils.establishConnection()
                    //Handle connection...
                    val jitsiConnection =
                        JitsiMeetUtils.startMeeting(Common.invitedAccount!!, meetingType, meetingId)
                    JitsiMeetActivity.launch(this, jitsiConnection)
                    finish()
                }
                REMOTE_RESPONSE_REJECTED -> {
                    //Call rejected by receiver
                    handler.postDelayed({
                        binding.outgoingMessage.text = getString(R.string.call_rejected)
                    }, 1000)
                    finish()
                }
            }
        })
        viewModel.callId.observe(this, {
            callId = it
        })
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
            invitationResponseReceiver,
            IntentFilter(REMOTE_MSG_CALL_RESPONSE)
        )
        //Handle event when user won't pick up the call
        //Auto cancel request call
        handler.postDelayed({
            binding.outgoingMessage.text = getString(R.string.user_not_available)
            finish()
        }, NOTIFICATION_REQUEST_CALL_DURATION.toLong())
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(
            invitationResponseReceiver
        )
    }
}