package com.workid.activities.userDetails

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.workid.R
import com.workid.activities.base.BaseActivity
import com.workid.activities.call.OutGoingInvitationActivity
import com.workid.callback.CallListener
import com.workid.databinding.ActivityUserDetailBinding
import com.workid.models.AccountModel
import com.workid.utils.Common
import com.workid.utils.getProgressDrawable
import com.workid.utils.loadImage

class UserDetailActivity : BaseActivity(), CallListener {

    private lateinit var binding : ActivityUserDetailBinding
    private val progressDrawable by lazy { getProgressDrawable(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_user_detail)
        //Binding view
        val motionLayout = binding.collapsingToolBar
        //Retrieve user from Intent
        if (!Common.invitedAccount!!.photoUrl.isNullOrEmpty()){
            binding.userAvatar.loadImage(Common.invitedAccount!!.photoUrl,progressDrawable)
        }else{
            binding.userAvatar.setImageResource(R.drawable.ic_avatar_default)
            motionLayout.setTransitionDuration(0)
        }

        binding.userName.text = Common.invitedAccount!!.customerName
        binding.userLastSeen.text = Common.invitedAccount!!.customerEmail
        binding.userAvatar.clipToOutline = true
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.show()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun initiateMeeting(account: AccountModel, type: String) {
        if (account.fcmToken == null || account.fcmToken!!.trim().isEmpty()) {
            Log.d("Contact", "initiateVideoMeeting: ${account.customerName} not available")
        } else {
            Common.invitedAccount = account
            Toast.makeText(this,getString(R.string.action_call),Toast.LENGTH_SHORT).show()
            val intent = Intent(this, OutGoingInvitationActivity::class.java)
            intent.putExtra("type", type)
            startActivity(intent)
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.user_detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_call -> {
                initiateMeeting(Common.invitedAccount!!,"audio")
                return true
            }
            R.id.action_video_call ->{
                initiateMeeting(Common.invitedAccount!!,"video")
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}