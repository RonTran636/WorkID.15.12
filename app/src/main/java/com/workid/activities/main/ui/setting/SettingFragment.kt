package com.workid.activities.main.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.workid.R
import com.workid.activities.login.LoginActivity
import com.workid.databinding.FragmentSettingBinding
import com.workid.utils.Common
import com.workid.utils.Constant
import timber.log.Timber

class SettingFragment : Fragment() {

    private lateinit var settingViewModel: SettingViewModel
    private lateinit var binding: FragmentSettingBinding
    private val userInfoRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference(Constant.USER)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        settingViewModel =
            ViewModelProvider(this).get(SettingViewModel::class.java)
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        val root = binding.root
        val textView: TextView = root.findViewById(R.id.text_notifications)
        settingViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })
        settingViewModel.fcmToken.observe(viewLifecycleOwner, {
            binding.fcmToken.text = it
        })
        Timber.tag("SettingFragment").d("onCreateView: Token: ${Common.currentAccount!!.fcmToken}")
        binding.workId.text = Common.currentAccount?.workId
        Timber.tag("SettingFragment")
            .d("onCreateView: current word id: ${Common.currentAccount?.workId}")
        Timber.tag("SettingFragment")
            .d("onCreateView: Current customer_id: ${Common.currentAccount?.customerId}")
        binding.signOut.setOnClickListener {
            userInfoRef.child(FirebaseAuth.getInstance().currentUser!!.uid).child(Constant.TOKEN)
                .removeValue()
            LoginManager.getInstance().logOut()
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }

        return root
    }
}