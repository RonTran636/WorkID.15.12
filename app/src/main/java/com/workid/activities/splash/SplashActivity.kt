package com.workid.activities.splash

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.workid.R
import com.workid.activities.base.BaseActivity
import com.workid.activities.login.LoginActivity
import com.workid.activities.main.MainActivity
import com.workid.activities.onboarding.OnBoardingActivity
import com.workid.models.AccountModel
import com.workid.utils.Common
import com.workid.utils.Constant
import com.workid.utils.Constant.Companion.FIRST_RUN
import timber.log.Timber
import kotlin.properties.Delegates

class SplashActivity : BaseActivity() {

    private lateinit var viewModel: SplashViewModel
    private lateinit var dataSave: SharedPreferences

    private var isFirstRun by Delegates.notNull<Boolean>()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val handler by lazy { Handler(Looper.myLooper()!!) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.plant(Timber.DebugTree())
        window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                setDecorFitsSystemWindows(false)
            } else {
                @Suppress("DEPRECATION")
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            statusBarColor = Color.TRANSPARENT
        }
        setContentView(R.layout.activity_splash)
        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)

        //Detect first run - show OnBoarding Activity accordingly
        dataSave = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
        isFirstRun = dataSave.getBoolean(FIRST_RUN, true)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.allowedToProceed.observe(this, {
            if (it == true) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        handler.postDelayed({ checkExistUser() }, 500)
    }

    private fun checkExistUser() {
        //Detect if user logged in or not
        if (auth.currentUser != null) {
            //User Logged in - Retrieve data from shared preference
            dataSave = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
            val gson = Gson()
            val json = dataSave.getString(Constant.USER, "")
            Common.currentAccount = gson.fromJson(json, AccountModel::class.java)
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            Timber.tag("SplashActivity")
                .d("checkExistUser: Called, fcm_token is: ${Common.currentAccount?.fcmToken}")
        } else {
            //Handle whether user will see OnBoarding Activity or not
            if (isFirstRun) {
                startActivity(Intent(this, OnBoardingActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}