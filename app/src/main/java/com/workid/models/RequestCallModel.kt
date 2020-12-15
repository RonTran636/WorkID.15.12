package com.workid.models

import com.google.gson.annotations.SerializedName
import com.workid.utils.Constant

data class RequestCallModel(
    var data : RequestCallItems?,
    var to: String?,
    val direct_boot_ok: Boolean = true,
    val priority: String = "HIGH",
    val time_to_alive: Long = Constant.NOTIFICATION_REQUEST_CALL_DURATION.toLong()
)

data class RequestCallItems(
    //Request call model
    var messageType: String,
    @SerializedName("call_id")
    var callId: String? = null,
    var callerName: String? = null,
    var callerEmail: String? = null,
    var callerPhotoURL: String? = null,
    var meetingType: String? = null,
    var meetingId: String? = null,
    //Request response model
    var response: String? = null
)