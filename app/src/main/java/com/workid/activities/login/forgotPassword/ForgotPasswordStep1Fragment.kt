package com.workid.activities.login.forgotPassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.actionCodeSettings
import com.workid.R
import com.workid.activities.base.BaseBottomSheetDialogFragment
import com.workid.databinding.DialogForgotPasswordStep1Binding
import timber.log.Timber

class ForgotPasswordStep1Fragment : BaseBottomSheetDialogFragment() {

    private lateinit var binding : DialogForgotPasswordStep1Binding
    private lateinit var viewRoot:View
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.dialog_forgot_password_step1,container, false)
        viewRoot = binding.root

        //Handle Click event:
        binding.backSpace.setOnClickListener {
            dismiss()
        }
        binding.actionSendVerification.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val actionCodeSettings = actionCodeSettings{
                url = "https://www.workid-f0203.firebaseapp.com/finishSignUp?cartId=1234" //TODO: Change URL
                // This must be true
                handleCodeInApp = true
                setAndroidPackageName(
                    "com.workid",
                    true, /* installIfNotAvailable */
                    "24" /* minimumVersion */)
            }
            auth.sendSignInLinkToEmail(email,actionCodeSettings)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(viewRoot.context,"Email Send",Toast.LENGTH_SHORT).show()
                        viewRoot.findNavController().navigate(R.id.action_signUpDialog_to_forgotPasswordStep2Fragment)
                    }else{
                        Timber.tag("ForgotPasswordStep1Fragment").e(it.exception)
                    }
                }
        }
        return viewRoot
    }

}