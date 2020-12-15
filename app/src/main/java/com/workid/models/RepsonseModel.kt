package com.workid.models

import com.google.gson.annotations.SerializedName

data class ResponseModel(
    @SerializedName("call_id")
    val callId: String,
    @SerializedName("meeting_id")
    val meetingId: String
)
