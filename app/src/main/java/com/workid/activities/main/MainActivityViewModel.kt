package com.workid.activities.main

import androidx.lifecycle.ViewModel
import com.workid.retrofit.ServiceCentral
import com.workid.utils.Common
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableCompletableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class MainActivityViewModel: ViewModel() {
    fun updateFcmToken(token: String)
    {   val myService = ServiceCentral()
        val disposable = CompositeDisposable()
        disposable.add(
            myService.updateFcmToken(token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        Timber.tag("updateFcmToken").d("onComplete: Token updated")
                        Common.currentAccount!!.fcmToken = token
                    }

                    override fun onError(e: Throwable?) {
                        Timber.tag("updateFcmToken").e("onError: $e")
                    }
                })
        )
    }
}