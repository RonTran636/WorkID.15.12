package com.workid.utils

class Constant {
    companion object {
        const val TOKEN = "FcmToken"
        const val USER = "Users"
        const val FIRST_RUN = "first run"
        const val CHANNEL_ID = "Calls"
        const val TRIGGER_SEARCH = 6
        const val CHANNEL_NAME = "NOTIFICATIONS"

        //URL
        const val FCM_URL   = "https://fcm.googleapis.com/"
        const val MEET_URL = "https://r.workid.ca//"
        const val BASE_URL = "https://dev.workid.ncore.vn/"

        //Firebase Cloud Message Constants
        const val CONTENT_TYPE = "application/json"
        const val LOCK_SCREEN_KEY = "lockScreenKey"
        const val REMOTE_MSG_CALL_TYPE = "messageType"

        //Request call
        const val REMOTE_MSG_CALL_REQUEST = "RequestCall"
        const val REMOTE_MSG_CALLER_INFO = "data"
        const val REMOTE_MSG_CALLER_ID = "call_id"
        const val REMOTE_MSG_CALLER_NAME = "callerName"
        const val REMOTE_MSG_CALLER_EMAIL = "callerEmail"
        const val REMOTE_MSG_CALLER_PHOTO_URL = "callerPhotoURL"
        const val REMOTE_MSG_CALLER_TOKEN = "callerToken"
        const val REMOTE_MSG_MEETING_TYPE = "meetingType"
        const val REMOTE_MSG_MEETING_ID = "meetingId"

        //Request Response
        const val REMOTE_MSG_CALL_RESPONSE = "ResponseCall"
        const val REMOTE_RESPONSE = "response"
        const val REMOTE_RESPONSE_ACCEPTED = "answered"
        const val REMOTE_RESPONSE_REJECTED = "declined"
        const val REMOTE_RESPONSE_MISSED = "missed"
        const val REMOTE_NOTIFICATION_ACCEPTED = "NOTIFICATION_ACCEPTED"
        const val REMOTE_NOTIFICATION_REJECTED = "NOTIFICATION_REJECTED"

        //Request Add Friend
        const val REMOTE_MSG_ADD_FRIEND = "addFriend"
        const val REMOTE_MSG_FRIEND_REQUEST_SENDER_ID = "senderId"
        const val REMOTE_MSG_FRIEND_REQUEST_RECEIVER_ID = "ReceiverID"

        //JitsiMeetUserInfo
        const val JITSI_DISPLAY_NAME = "displayName"
        const val JITSI_EMAIL = "email"
        const val JITSI_PHOTO_URL = "avatar"

        //Notification
        const val NOTIFICATION_REQUEST_CALL_ID = 1001
        const val NOTIFICATION_REQUEST_CALL_DURATION :Int = 60000
        val NOTIFICATION_VIBRATE_PATTERN :LongArray = longArrayOf(0,500)

        //Server Constants
        const val USER_EXISTED = "Existed"
        const val USER_NON_EXISTED = "Non-existed"

        const val temp = "fS_SkEOVQN-COlHuMlOvhA:APA91bGaff5KIF7HhZMP60Vgr5dka1Gn_0dyODcFYqhmKVJf_PEEDhY6qtRV9zk7LghZurNwm4jAKWnp58WzqLdzUiStVW0AUIeDychHJjbF52lyXDGOH5EpN64Yi1OvX2CiLHaR1vHG"
        const val fcmTemp = "dt_70hqkQcG2ugNSxrIQ-2:APA91bHL3AlMJcyYFtiEnL_sO4fuHS6R9qdmEXQq7Cw8coLAYialr5SCh3h9SurRV87zXJ_CzEK0VROceufT12ED0_MmUx6zjvZpMy53IfJ3JQhYAkISh0A5ICp9d6B68PCLsCw41XRF"
    }
}