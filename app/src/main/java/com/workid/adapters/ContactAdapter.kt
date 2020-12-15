package com.workid.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.workid.R
import com.workid.activities.call.OutGoingInvitationActivity
import com.workid.activities.userDetails.UserDetailActivity
import com.workid.databinding.HolderContactBinding
import com.workid.callback.CallListener
import com.workid.models.AccountModel
import com.workid.utils.Common
import com.workid.utils.getProgressDrawable
import com.workid.utils.loadImage

@SuppressLint("LogNotTimber")
class ContactAdapter(private var listContact : ArrayList<AccountModel>) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    fun updateData(newContact: ArrayList<AccountModel>){
        listContact = newContact
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.holder_contact, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        Log.d("TAG", "onBindViewHolder: current possition: $position")
        holder.setData(listContact[position])
    }

    override fun getItemCount(): Int = listContact.size

    class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), CallListener {
        private var binding = HolderContactBinding.bind(itemView)
        private val progressDrawable = getProgressDrawable((itemView.context))
        
        fun setData(account: AccountModel) {
            if (account.photoUrl != null && account.photoUrl!!.isNotEmpty()) {
                binding.holderContactAvatarContainer.loadImage(account.photoUrl, progressDrawable)
            }
            binding.holderContactName.text = account.customerName
            binding.holderContactEmail.text = account.customerEmail
            if (Common.invitedUserList.isEmpty()) {
                binding.holderCall.setOnClickListener { initiateMeeting(account, "audio") }
                binding.holderVideoCall.setOnClickListener { initiateMeeting(account, "video") }
            }
            itemView.setOnLongClickListener {
                if (Common.invitedUserList.contains(account)) {
                    //User already contain in the list - remove it
                    unSelectUser(account)
                } else {
                    //User don't have in the list - adding in
                    selectUser(account)
                }
                return@setOnLongClickListener true
            }
            itemView.setOnClickListener {
                if (Common.invitedUserList.isNullOrEmpty()){
                    //InvitedList empty - Move to Detail Page
                    Common.invitedAccount = account
                    val intent = Intent(itemView.context,UserDetailActivity::class.java)
                    itemView.context.startActivity(intent)
               }else{
                    if (Common.invitedUserList.contains(account)){
                        //User already contain in the list - remove it
                       unSelectUser(account)
                    }else{
                        //User don't have in the list - adding in
                        selectUser(account)
                    }
               }
            }
        }

        private fun selectUser(account: AccountModel) {
            Common.invitedUserList.add(account)
            Common.observableUserList.value = Common.invitedUserList
            binding.holderContactAvatarContainer.setImageResource(R.drawable.ic_avatar_checked)
            itemView.resources.getColor(R.color.md_green_300, itemView.context.theme)
            binding.holderCall.visibility = View.INVISIBLE
            binding.holderVideoCall.visibility = View.INVISIBLE
        }

        private fun unSelectUser(account: AccountModel) {
            Common.invitedUserList.remove(account)
            Common.observableUserList.value = Common.invitedUserList
            if (account.photoUrl != null && account.photoUrl!!.isNotEmpty()) {
                binding.holderContactAvatarContainer.loadImage(account.photoUrl, progressDrawable)
            }
            itemView.resources.getColor(R.color.backgroundColor, itemView.context.theme)
            binding.holderCall.visibility = View.VISIBLE
            binding.holderVideoCall.visibility = View.VISIBLE
        }

        override fun initiateMeeting(account: AccountModel, type: String) {
            if (account.fcmToken == null || account.fcmToken!!.trim().isEmpty()) {
                Log.d("Contact", "initiateVideoMeeting: ${account.customerName} not available")
            } else {
                Common.invitedAccount = account
                Toast.makeText(
                    itemView.context,
                    itemView.context.getString(R.string.action_call),
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(itemView.context, OutGoingInvitationActivity::class.java)
                intent.putExtra("type", type)
                itemView.context.startActivity(intent)
            }
        }
    }
}