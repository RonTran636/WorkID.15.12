package com.workid.activities.main

import android.content.SharedPreferences
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.workid.R
import com.workid.activities.base.BaseActivity
import com.workid.utils.Common
import com.workid.utils.Constant
import timber.log.Timber

class MainActivity : BaseActivity() {

    private lateinit var dataSave: SharedPreferences
    private lateinit var viewModel: MainActivityViewModel
    private val gson = Gson()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        //Save user information into shared preference
        if (Common.currentAccount != null) {
            dataSave = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
            val json = gson.toJson(Common.currentAccount)
            val editor: SharedPreferences.Editor = dataSave.edit()
            editor.putString(Constant.USER, json)
            editor.apply()
        }
        //Setup Bottom Navigation Bar
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController
        navView.setupWithNavController(navController)

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            Timber.tag("MainActivity").d("onCreate: token from Firebase: $it")
             viewModel.updateFcmToken(it)
        }
        Timber.tag("MainActivity")
            .d("onCreate: server token: ${Common.currentAccount?.serverToken}")
    }
}