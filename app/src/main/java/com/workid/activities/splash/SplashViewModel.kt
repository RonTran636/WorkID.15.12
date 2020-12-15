package com.workid.activities.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.workid.models.ServerUserModel
import com.workid.retrofit.ServiceCentral
import com.workid.utils.Common
import com.workid.utils.Constant
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class SplashViewModel : ViewModel() {

    private val _allowedToProceed = MutableLiveData<Boolean>()
    private val myService = ServiceCentral()
    private val disposable = CompositeDisposable()
    val allowedToProceed: LiveData<Boolean> = _allowedToProceed

    fun checkUserFromDatabase(uid: String) {
        val checkingUser: Single<ServerUserModel> = myService.checkUserExistedOnDatabase(uid)
        disposable.add(
            checkingUser
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ServerUserModel>() {
                    override fun onSuccess(t: ServerUserModel) {
                        if (t.message == Constant.USER_EXISTED) {
                            Common.currentAccount = t.result
                            _allowedToProceed.value = true
                        }
                    }

                    override fun onError(e: Throwable) {
                        _allowedToProceed.value = false
                    }
                })
        )

    }
}