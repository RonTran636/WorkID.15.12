package com.workid.activities.login.signup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.workid.R
import com.workid.activities.base.BaseBottomSheetDialogFragment
import com.workid.activities.login.home.LoginHomeViewModel
import com.workid.activities.main.MainActivity
import com.workid.databinding.FragmentSignupBinding
import com.workid.utils.hideKeyboard
import com.workid.utils.isValidPassword
import java.util.*


class FragmentSignUp : BaseBottomSheetDialogFragment() {

    private lateinit var binding: FragmentSignupBinding
    private lateinit var viewModel: LoginHomeViewModel
    private lateinit var viewRoot :View

    private lateinit var username: String
    private lateinit var password: String
    private lateinit var confirmPassword: String
    private lateinit var email: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_signup,container,false)
        viewModel = ViewModelProvider(this).get(LoginHomeViewModel::class.java)
        viewRoot = binding.root

        verifyInput()
        observeViewModel()

        binding.backSpace.setOnClickListener {
            dismiss()
        }
        binding.buttonCreateAccount.setOnClickListener {
            it.hideKeyboard()
            username = binding.etUserName.text.toString().trim()
            email = binding.etEmail.text.toString().trim().decapitalize(Locale.ROOT)
            password = binding.etPassword.text.toString().trim()
            confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (password.isValidPassword() && (password == confirmPassword)) {
                viewModel.createNewAccount(email, password, username)
            }else if (password!=confirmPassword){
                binding.etConfirmPasswordLayout.error = viewRoot.context.getString(R.string.password_not_match)
            } else {
                binding.etConfirmPassword.error = viewRoot.context.getString(R.string.invalid_password)
                binding.passwordRules.visibility = View.VISIBLE
            }
        }
        return viewRoot
    }

    private fun observeViewModel() {
        viewModel.loginSuccessDetails.observe(viewLifecycleOwner,{
            if (it==true){
                startActivity(Intent(context, MainActivity::class.java))
            }
        })
        viewModel.errorMessage.observe(viewLifecycleOwner,{
            binding.etEmailLayout.error = it
        })
    }

    private fun verifyInput() {
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.etPasswordLayout.error = null
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.etConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.etConfirmPasswordLayout.error = null
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.etEmailLayout.error = null
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }
}