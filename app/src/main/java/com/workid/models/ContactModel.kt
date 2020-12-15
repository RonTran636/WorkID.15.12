package com.workid.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ContactModel(
    val result: ArrayList<AccountModel>
)
