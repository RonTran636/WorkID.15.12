package com.workid.activities.event.addFriend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.workid.R
import com.workid.activities.base.BaseBottomSheetDialogFragment
import com.workid.activities.call.OutGoingInvitationActivity
import com.workid.databinding.AddFriendPopupBinding
import com.workid.callback.CallListener
import com.workid.models.AccountModel
import com.workid.models.RequestAddFriendItem
import com.workid.utils.Common
import com.workid.utils.Constant.Companion.REMOTE_MSG_ADD_FRIEND
import com.workid.utils.getProgressDrawable
import com.workid.utils.loadImage

class AddFriendDialog : BaseBottomSheetDialogFragment(), CallListener {

    private lateinit var binding : AddFriendPopupBinding
    private lateinit var viewModel: AddFriendViewModel
    private lateinit var viewRoot: View
    private val navBar = activity?.findViewById<BottomNavigationView>(R.id.nav_view)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.add_friend_popup,container,false)
        viewRoot = binding.root
        viewModel = ViewModelProvider(this).get(AddFriendViewModel::class.java)
        navBar?.visibility = View.GONE

        observeViewModel()

        //Inflate information on layout
        val progressDrawable = getProgressDrawable(viewRoot.context)
        binding.addFriendName.text = Common.invitedAccount!!.customerName
        binding.addFriendWorkId.text = Common.invitedAccount!!.workId
        binding.addFriendAvatar.loadImage(Common.invitedAccount!!.photoUrl,progressDrawable)
        //Handle click event
        binding.backSpace.setOnClickListener {
            dismiss()
        }
        binding.actionAddFriend.setOnClickListener {
            //Send add friend request to server
            val remoteMsgItem = RequestAddFriendItem(REMOTE_MSG_ADD_FRIEND)
            remoteMsgItem.receiverId = Common.invitedAccount!!.customerId
            remoteMsgItem.messageDetail = binding.messageDetail.text.toString()
            viewModel.sendFriendRequest(remoteMsgItem)
            Toast.makeText(viewRoot.context, "Invitation send", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        binding.holderCall.setOnClickListener {
            initiateMeeting(Common.invitedAccount!!, "audio")
        }
        binding.holderVideoCall.setOnClickListener {
            initiateMeeting(Common.invitedAccount!!, "video")
        }
        //Testing
        binding.fcmToken.text = Common.invitedAccount!!.fcmToken
        return viewRoot
    }

    private fun observeViewModel() {
        viewModel.isRequestSent.observe(viewLifecycleOwner,{
            if (it == true){
                //Add friend request was sent, close the dialog
               dismiss()
            }
        })
    }

    override fun dismiss() {
        parentFragmentManager.popBackStack()
    }

    override fun initiateMeeting(account: AccountModel, type: String) {
        if (account.customerId == null) {
            Log.d("TAG", "initiateMeeting: something went wrong: fcm token: ${account.fcmToken}")
            Log.d("TAG", "initiateMeeting: uid: $account.uid")
        } else {
            Common.invitedAccount = account
            Toast.makeText(
                viewRoot.context,
                viewRoot.context.getString(R.string.action_call),
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(viewRoot.context, OutGoingInvitationActivity::class.java)
            intent.putExtra("type", type)
            viewRoot.context.startActivity(intent)
        }
    }
}
