package com.workid.broadcast

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.workid.models.RequestCallItems
import com.workid.models.RequestCallModel
import com.workid.retrofit.ServiceCentral
import com.workid.utils.Constant
import com.workid.utils.Constant.Companion.REMOTE_NOTIFICATION_ACCEPTED
import com.workid.utils.Constant.Companion.REMOTE_NOTIFICATION_REJECTED
import com.workid.utils.IntentUtils
import com.workid.utils.JitsiMeetUtils
import com.workid.utils.cancelNotification
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableCompletableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import timber.log.Timber

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val keyguardManager =
            context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        val userData = IntentUtils.retrieveDataFromIntent(intent)

        when (intent.action) {
            REMOTE_NOTIFICATION_ACCEPTED -> {
                context.cancelNotification(Constant.NOTIFICATION_REQUEST_CALL_ID)
                performClickAction(context, userData, Constant.REMOTE_RESPONSE_ACCEPTED)
            }
            REMOTE_NOTIFICATION_REJECTED -> {
                context.cancelNotification(Constant.NOTIFICATION_REQUEST_CALL_ID)
                performClickAction(context, userData, Constant.REMOTE_RESPONSE_REJECTED)
            }
        }
    }

    private fun performClickAction(context: Context, userData: RequestCallItems, response: String) {
        //Send confirmation to caller - Whether accept or decline
        sendResponseRequestCall(userData.callId!!, response, userData.meetingId!!)
        if (response == Constant.REMOTE_RESPONSE_ACCEPTED) {
            //Accepted call - Establish connection
            JitsiMeetUtils.establishConnection()
            val jitsiConnection = JitsiMeetUtils.startMeeting(userData)
            launchJitsiMeetInBroadcast(context, jitsiConnection)
        }
    }

    private fun sendResponseRequestCall(
        callId: String,
        responseAction: String,
        meetingId: String
    ) {
        val myService = ServiceCentral()
        val disposable = CompositeDisposable()
        disposable.add(
            myService.sendResponseRequestCall(callId, responseAction, meetingId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        Timber.tag("onComplete").d("onComplete")
                    }

                    override fun onError(e: Throwable) {
                        Timber.tag("onError").e(e)
                    }
                })
        )
    }

    private fun launchJitsiMeetInBroadcast(context: Context, options: JitsiMeetConferenceOptions) {
        val intent = Intent(context, JitsiMeetActivity::class.java)
        intent.action = "org.jitsi.meet.CONFERENCE"
        intent.putExtra("JitsiMeetConferenceOptions", options)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

}