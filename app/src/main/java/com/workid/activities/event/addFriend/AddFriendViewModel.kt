package com.workid.activities.event.addFriend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.workid.models.RequestAddFriendItem
import com.workid.retrofit.ServiceCentral
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableCompletableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class AddFriendViewModel : ViewModel() {
    private val _isRequestSent = MutableLiveData<Boolean>()
    val isRequestSent : LiveData<Boolean> = _isRequestSent
    private val disposable = CompositeDisposable()
    private val myService = ServiceCentral()

    fun sendFriendRequest(remoteMsg: RequestAddFriendItem) {
        disposable.add(
                myService.sendFriendRequestToServer(remoteMsg)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableCompletableObserver() {
                            override fun onComplete() {
                                Timber.tag("AddFriendViewModel").d("onComplete: Send friend request to server")
                                _isRequestSent.value = true
                            }

                            override fun onError(e: Throwable) {
                                Timber.tag("AddFriendViewModel").e("onError: ")
                            }
                        })
        )
    }
}