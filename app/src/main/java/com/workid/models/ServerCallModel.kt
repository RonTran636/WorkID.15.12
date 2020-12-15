package com.workid.models

import com.google.gson.annotations.SerializedName

data class ServerCallModel(
    @SerializedName("customer_id")
    var customerId: String,
    @SerializedName("meeting_type")
    var meetingType: String,
    @SerializedName("meeting_id")
    var meetingId: String
)
