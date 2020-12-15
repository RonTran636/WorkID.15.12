package com.workid.activities.login.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.workid.R
import com.workid.activities.main.MainActivity
import com.workid.databinding.FragmentLoginHomeBinding
import com.workid.utils.hideKeyboard
import com.workid.utils.isValidEmail
import com.workid.utils.isValidPassword
import timber.log.Timber

class LoginHomeFragment : Fragment() {

    companion object {
        private const val RC_SIGN_IN = 101
    }
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var viewModel: LoginHomeViewModel
    private lateinit var binding : FragmentLoginHomeBinding
    private lateinit var viewRoot:View

    private var isEmailAndPasswordLegit = false
    private val auth: FirebaseAuth = Firebase.auth
    private val callbackManager: CallbackManager = CallbackManager.Factory.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_login_home,container,false)
        viewModel = ViewModelProvider(this).get(LoginHomeViewModel::class.java)
        viewRoot = binding.root

        binding.buttonLogin.setOnClickListener {
            it.hideKeyboard()
            signInWithEmailAndPassword()
        }
        binding.buttonCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginHomeFragment_to_fragmentSignUp)
        }
        binding.forgotPassword.setOnClickListener{
            findNavController().navigate(R.id.action_loginHomeFragment_to_forgotPasswordFragment)
        }
        binding.signInGoogle.setOnClickListener { signInWithGoogle() }
        binding.signInFacebook.setOnClickListener { signInWithFacebook() }
        return viewRoot
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this.requireContext(), gso)

        verifyInput()
        observeViewModel()
    }

    // [Handle received credential]
    private fun handleAccessToken(credential: AuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Timber.tag("handleAccessToken").d("Called")
                    viewModel.loginDatabase(auth)
                } else {
                    //Sign in fails, display a message to the user.`
                    Toast.makeText(requireContext(), "Authentication failed.", Toast.LENGTH_SHORT)
                        .show()
                    Timber.tag("handleAccessToken").e(task.exception)
                }
            }
    }

    // [START auth_with_email_and_password]
    private fun signInWithEmailAndPassword() {
        val userEmail = binding.etEmail.text.toString()
        val userPassword = binding.etPassword.text.toString()
        if (isEmailAndPasswordLegit) {
            val credential = EmailAuthProvider.getCredential(userEmail, userPassword)
            handleAccessToken(credential)
        }
    }
    // [START auth_with_facebook]
    private fun signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))
        LoginManager.getInstance().registerCallback(callbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                handleAccessToken(credential)
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException?) {
            }
        })
    }
    // [START auth_with_google]
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun verifyInput() {
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isValidEmail()) {
                    isEmailAndPasswordLegit = false
                    binding.etEmailLayout.error = getString(R.string.invalid_user_email)
                } else {
                    isEmailAndPasswordLegit = true
                    binding.etEmailLayout.error = null
                }
            }
        })
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (!s.toString().isValidPassword()) {
                    isEmailAndPasswordLegit = false
                    binding.etPasswordLayout.error = getString(R.string.invalid_password)
                } else {
                    isEmailAndPasswordLegit = true
                    binding.etPasswordLayout.error = null
                }
            }
        })
    }

    private fun observeViewModel() {
        viewModel.loginSuccessDetails.observe(viewLifecycleOwner, {
//            Timber.tag("observeViewModel").d("current user server token: ${Common.currentAccount!!.serverToken}")
            if (it == true) {
                startActivity(Intent(requireContext(), MainActivity::class.java))
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        //Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                handleAccessToken(credential)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Timber.tag("onActivityResult").e(e)
            }
        }
    }
}