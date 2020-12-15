package com.workid.models

import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName

@IgnoreExtraProperties
data class CustomerModel(
    @SerializedName("id")
    var customerId :String?=null,

    @SerializedName("name")
    var customerName: String?=null,

    @SerializedName("email")
    var customerEmail: String?=null,

    @SerializedName("is_active")
    var userBanned: Boolean?=null,

    @SerializedName("phone")
    var customerPhoneNumber : String?=null
)
