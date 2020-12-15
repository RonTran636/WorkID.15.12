package com.workid.models

data class RequestAddFriendToFcmModel(
    var data : RequestAddFriendItem?,
    var to: String?,
    val priority: String = "NORMAL",
)

data class RequestAddFriendItem(
    var messageType: String,
    var receiverId: String?=null,
    var messageDetail: String?=null
)
