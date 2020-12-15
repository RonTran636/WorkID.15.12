package com.workid.utils

import android.content.Intent
import com.google.firebase.messaging.RemoteMessage
import com.workid.models.RequestCallItems

object IntentUtils {

    fun retrieveDataFromFCM(message: RemoteMessage): RequestCallItems {
        return RequestCallItems(
            message.data[Constant.REMOTE_MSG_CALL_TYPE]!!,
            message.data[Constant.REMOTE_MSG_CALLER_ID],
            message.data[Constant.REMOTE_MSG_CALLER_NAME],
            message.data[Constant.REMOTE_MSG_CALLER_EMAIL],
            message.data[Constant.REMOTE_MSG_CALLER_PHOTO_URL],
            message.data[Constant.REMOTE_MSG_MEETING_TYPE],
            message.data[Constant.REMOTE_MSG_MEETING_ID],
            message.data[Constant.REMOTE_RESPONSE]
        )
    }

    fun retrieveDataFromIntent(intent: Intent): RequestCallItems {
        return RequestCallItems(
            intent.getStringExtra(Constant.REMOTE_MSG_CALL_TYPE)!!,
            intent.getStringExtra(Constant.REMOTE_MSG_CALLER_ID),
            intent.getStringExtra(Constant.REMOTE_MSG_CALLER_NAME),
            intent.getStringExtra(Constant.REMOTE_MSG_CALLER_EMAIL),
            intent.getStringExtra(Constant.REMOTE_MSG_CALLER_PHOTO_URL),
            intent.getStringExtra(Constant.REMOTE_MSG_MEETING_TYPE),
            intent.getStringExtra(Constant.REMOTE_MSG_MEETING_ID),
            intent.getStringExtra(Constant.REMOTE_RESPONSE)
        )
    }

    fun Intent.addDataIntoIntent(data: RequestCallItems) {
        this.putExtra(Constant.REMOTE_MSG_CALL_TYPE, data.messageType)
        this.putExtra(Constant.REMOTE_MSG_CALLER_ID, data.callId)
        this.putExtra(Constant.REMOTE_MSG_CALLER_NAME, data.callerName)
        this.putExtra(Constant.REMOTE_MSG_CALLER_PHOTO_URL, data.callerPhotoURL)
        this.putExtra(Constant.REMOTE_MSG_MEETING_TYPE, data.meetingType)
        this.putExtra(Constant.REMOTE_MSG_MEETING_ID, data.meetingId)
    }
}