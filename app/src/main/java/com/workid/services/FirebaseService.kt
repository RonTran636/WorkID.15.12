package com.workid.services

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.workid.retrofit.ServiceCentral
import com.workid.utils.*
import com.workid.utils.Constant.Companion.NOTIFICATION_REQUEST_CALL_ID
import com.workid.utils.Constant.Companion.REMOTE_MSG_ADD_FRIEND
import com.workid.utils.Constant.Companion.REMOTE_MSG_CALL_REQUEST
import com.workid.utils.Constant.Companion.REMOTE_MSG_CALL_RESPONSE
import com.workid.utils.Constant.Companion.REMOTE_MSG_CALL_TYPE
import com.workid.utils.Constant.Companion.REMOTE_RESPONSE_REJECTED
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableCompletableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class FirebaseService : FirebaseMessagingService() {
    private val auth = FirebaseAuth.getInstance()
    private val myService = ServiceCentral()
    private val disposable = CompositeDisposable()
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if (auth.currentUser != null) {
            Common.currentAccount!!.fcmToken = token
            //Update fcm token on server's database
            updateFcmToken(token)
        }
    }

    private fun updateFcmToken(token: String?) {
        disposable.add(
            myService.updateFcmToken(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        Timber.tag("updateFcmToken").d("onComplete: Token updated")
                        Common.currentAccount!!.fcmToken = token
                    }

                    override fun onError(e: Throwable?) {
                        Timber.tag("updateFcmToken").e("onError: $e")
                    }
                })
        )
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (message.data.isNotEmpty()) {
            when (message.data[REMOTE_MSG_CALL_TYPE]) {
                REMOTE_MSG_CALL_REQUEST -> {
                    //Call Request - Retrieve caller data from Service
                    val data = IntentUtils.retrieveDataFromFCM(message)
                    Timber.tag("REMOTE_MSG_CALL_REQUEST").d("onMessageReceived: data:$data")
                    if (Common.isForeground) {
                        showIncomingInvitationActivity(data)
                    } else {
                        showNotificationWithFullScreenIntent(data)
                    }
                }
                REMOTE_MSG_CALL_RESPONSE -> {
                    //Response call Request - Retrieve confirm code
                    val data = IntentUtils.retrieveDataFromFCM(message)
                    Timber.tag("REMOTE_MSG_CALL_RESPONSE").d("onMessageReceived: data: $data")
                    val intent = Intent(REMOTE_MSG_CALL_RESPONSE)
                    intent.putExtra(REMOTE_MSG_CALL_RESPONSE, data.response)
                    if (data.response == REMOTE_RESPONSE_REJECTED) {
                        cancelNotification(NOTIFICATION_REQUEST_CALL_ID)
                    }
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                }
                REMOTE_MSG_ADD_FRIEND -> {
                    //Add friend Request
//                    val data = getFriendRequestInfo(message)
                    //TODO:Handle request friend received

                }
            }
        }
    }
    /*
    private fun getFriendRequestInfo(message: RemoteMessage): RequestAddFriendItem{
        return RequestAddFriendItem(
            message.data[REMOTE_MSG_CALL_TYPE]!!,
            message.data[REMOTE_MSG_FRIEND_REQUEST_SENDER_ID],
            message.data[REMOTE_MSG_FRIEND_REQUEST_RECEIVER_ID]
        )
    }*/

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }
}