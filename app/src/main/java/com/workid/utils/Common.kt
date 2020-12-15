package com.workid.utils

import androidx.lifecycle.MutableLiveData
import com.workid.models.AccountModel

object Common {
    var currentAccount : AccountModel? = null
    var invitedAccount : AccountModel? = null
    var invitedUserList = mutableListOf<AccountModel>()
    var observableUserList = MutableLiveData<MutableList<AccountModel>>()
    var isForeground :Boolean = false
}