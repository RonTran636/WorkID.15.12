package com.workid.activities.onboarding

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.workid.R
import com.workid.activities.base.BaseActivity
import com.workid.activities.login.LoginActivity
import com.workid.adapters.SliderAdapter
import com.workid.databinding.ActivityOnboardingBinding
import com.workid.utils.Constant.Companion.FIRST_RUN

class OnBoardingActivity : BaseActivity(),View.OnClickListener {

    private lateinit var viewPagerAdapter: SliderAdapter
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var completedOnBoarding : SharedPreferences
    private val onBoardSize= 3  // Number of onBoarding images
    private val dots = arrayOfNulls<ImageView>(onBoardSize)
    private val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_onboarding)
        completedOnBoarding = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
        viewPagerAdapter = SliderAdapter(this)
        binding.onBoardViewPager.adapter = viewPagerAdapter
        //Initialize onBoarding image's counter
        for (i in 0 until onBoardSize){
            dots[i] = ImageView(this)
        }
        addDot(0)
        layoutParams.setMargins(0,0,4,0)

        binding.onBoardViewPager.addOnPageChangeListener(listener)
        binding.buttonExplore.setOnClickListener(this)
        binding.buttonSkip.setOnClickListener(this)
    }

    private val listener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

        }

        override fun onPageSelected(position: Int) {
            addDot(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
        }
    }

    private fun addDot(position: Int) {
        binding.layoutDots.removeAllViews()
        for (i in 0 until onBoardSize){
            dots[i]!!.setNonSelectedDot()
            binding.layoutDots.addView(dots[i],layoutParams)
        }
        dots[position]!!.setSelectedDot()
    }

    private fun ImageView.setNonSelectedDot() {
        this.setImageDrawable(
            ContextCompat.getDrawable(
                this@OnBoardingActivity,
                R.drawable.non_selected_dot
            )
        )
    }

    private fun ImageView.setSelectedDot() {
        this.setImageDrawable(
            ContextCompat.getDrawable(
                this@OnBoardingActivity,
                R.drawable.selected_dot
            )
        )
    }

    private fun moveToHomeActivity(){
        val editor : SharedPreferences.Editor = completedOnBoarding.edit()
        editor.putBoolean(FIRST_RUN,false)
        editor.apply()
        startActivity(Intent(this,LoginActivity::class.java))
        finish()
    }

    override fun onClick(v: View) {
        when (v) {
            binding.buttonSkip -> moveToHomeActivity()
            binding.buttonExplore -> {
                if (binding.onBoardViewPager.currentItem < onBoardSize - 1) {
                    binding.onBoardViewPager.currentItem++
                } else {
                    moveToHomeActivity()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (binding.onBoardViewPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            binding.onBoardViewPager.currentItem--
        }
    }

    /**
    Request for permission - Don't need it here

    private fun askPermission() {
    TedRx2Permission.with(this)
    .setRationaleMessage(R.string.rationale_message)
    .setDeniedCloseButtonText(android.R.string.cancel)
    .setPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    .setDeniedMessage(R.string.message_permission_denied)
    .request()
    .subscribe({ tedPermissionResult ->
    if (tedPermissionResult.isGranted) {
    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
    handler.postDelayed({
    //TODO: Change destination Activity
    startActivity(Intent(this@OnBoardingActivity, LoginActivity::class.java))
    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }, 200)
    } else {
    Toast.makeText(
    this, "Permission Denied\n" + tedPermissionResult.deniedPermissions.toString(),
    Toast.LENGTH_SHORT
    ).show()
    }
    }, { })
    }
     */
}