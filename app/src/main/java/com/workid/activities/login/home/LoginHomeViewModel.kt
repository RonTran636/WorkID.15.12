package com.workid.activities.login.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.messaging.FirebaseMessaging
import com.workid.models.AccountModel
import com.workid.models.ServerUserModel
import com.workid.retrofit.ServiceCentral
import com.workid.utils.Common
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class LoginHomeViewModel: ViewModel() {

    private val myService = ServiceCentral()
    private val disposable = CompositeDisposable()
    private val _loginSuccessDetails = MutableLiveData<Boolean>()

    val loginSuccessDetails: LiveData<Boolean> = _loginSuccessDetails
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private fun registerUserToDatabase(accountModel: AccountModel) {
        disposable.add(
            myService.registerUserToDatabase(accountModel)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ServerUserModel>() {
                    override fun onSuccess(t: ServerUserModel) {
                        Common.currentAccount = t.result
                        _loginSuccessDetails.value = true
                    }

                    override fun onError(e: Throwable) {
                        Timber.tag("onError").e(e)
                        _loginSuccessDetails.value = false
                    }
                })
        )
    }

    fun loginDatabase(auth :FirebaseAuth) {
        val user = AccountModel()
        user.customerName = auth.currentUser!!.displayName
        user.customerEmail = auth.currentUser!!.email
        user.customerPhoneNumber = auth.currentUser!!.phoneNumber
        user.photoUrl = auth.currentUser!!.photoUrl.toString()
        user.uid = auth.currentUser!!.uid
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            user.fcmToken = it
        }
        registerUserToDatabase(user)
    }

    private fun loginDatabase(auth: FirebaseAuth, userName: String) {
        val user = AccountModel()
        user.customerName = userName
        user.customerEmail = auth.currentUser!!.email
        user.uid = auth.currentUser!!.uid
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            user.fcmToken = it
        }
        registerUserToDatabase(user)
    }

    fun createNewAccount(email: String, password: String, username: String) {
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loginDatabase(auth, username)
                }
            }
            .addOnFailureListener {
                when (it) {
                    is FirebaseAuthInvalidCredentialsException -> {
                        _errorMessage.postValue("Invalid email, please try again")
                    }
                    is FirebaseAuthUserCollisionException -> {
                        _errorMessage.postValue("Email is existed,please use another email")
                    }
                    else -> Timber.tag("createNewAccount").e(it)
                }
            }
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}