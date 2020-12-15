package com.workid.callback

import com.workid.models.AccountModel

interface CallListener {
    fun initiateMeeting(account: AccountModel, type: String)
}