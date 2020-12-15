package com.workid.models

import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName

@IgnoreExtraProperties
data class AccountModel(
    @SerializedName("id")
    var customerId: String? = null,

    @SerializedName("uid")
    var uid: String? = null,

    @SerializedName("workid")
    var workId: String? = null,

    @SerializedName("photo_url")
    var photoUrl: String? = null,

    @SerializedName("fcm_token")
    var fcmToken: String? = null,

    @SerializedName("last_login")
    var lastSeen: String? = null,

    @SerializedName("customer_name")
    var customerName: String? = null,

    @SerializedName("customer_email")
    var customerEmail: String? = null,

    @SerializedName("customer_phone")
    var customerPhoneNumber: String? = null,

    @SerializedName("is_active")
    var isActive: Boolean? = null,

    @SerializedName("token")
    var serverToken: String? = null
)