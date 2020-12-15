package com.workid.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.workid.R
import com.workid.activities.event.addFriend.AddFriendDialog
import com.workid.databinding.HolderSuggestingContactBinding
import com.workid.models.AccountModel
import com.workid.utils.Common
import com.workid.utils.getProgressDrawable
import com.workid.utils.loadImage
import timber.log.Timber

class SuggestContactAdapter(private var suggestList: ArrayList<AccountModel>) :
    RecyclerView.Adapter<SuggestContactAdapter.ViewHolder>()  {
    fun update(newSuggestList: ArrayList<AccountModel>) {
        suggestList = newSuggestList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.holder_suggesting_contact, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(suggestList[position])
    }

    override fun getItemCount(): Int = suggestList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = HolderSuggestingContactBinding.bind(itemView)
        private val progressDrawable = getProgressDrawable((itemView.context))

        fun setData(account: AccountModel) {
            binding.holderContactAvatarContainer.loadImage(account.photoUrl, progressDrawable)
            binding.holderContactName.text = account.customerName
            binding.holderContactWorkid.text =
                itemView.context.getString(R.string.work_id, account.workId)
            binding.holderActionAdd.setOnClickListener {
                //Show Add friend Popup
                Common.invitedAccount = account
                Timber.tag("ViewHolder").d("setData: account data : $account")
                val activity = itemView.context as AppCompatActivity
                val fragment = AddFriendDialog()
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, fragment).addToBackStack(null).commit()
            }
        }
    }
}