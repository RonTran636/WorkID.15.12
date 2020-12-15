package com.workid.activities.main.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.workid.utils.Common

class SettingViewModel : ViewModel() {

    private lateinit var auth : FirebaseAuth
    private val _text = MutableLiveData<String>().apply {
        value = Common.currentAccount!!.customerName
    }
    val text: LiveData<String> = _text
    private val _fcmToken = MutableLiveData<String>().apply {
        value = Common.currentAccount!!.fcmToken
    }
    val fcmToken :LiveData<String> = _fcmToken
}