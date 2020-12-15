package com.workid.activities.call

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.workid.models.ResponseModel
import com.workid.retrofit.ServiceCentral
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableCompletableObserver
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class InvitationViewModel : ViewModel() {
    private val _callId = MutableLiveData<String>()
    val callId: LiveData<String> = _callId
    private val myService = ServiceCentral()
    private val disposable = CompositeDisposable()

    fun sendRequestCall(receiverId: String, meetingType: String, meetingId: String) {
        disposable.add(
            myService.sendRequestCall(receiverId, meetingType, meetingId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ResponseModel>() {
                    override fun onSuccess(t: ResponseModel) {
                        _callId.value = t.callId
                        Timber.tag("InvitationViewModel").d("onSuccess: _callId data: ${t.callId}")
                        Timber.tag("InvitationViewModel")
                            .d("onSuccess: _callId meetingId ${t.meetingId}")
                    }

                    override fun onError(error: Throwable) {
                        Timber.tag("sendRequestCall").e(error)
                    }
                })
        )
    }

    fun sendResponseRequestCall(
        callId: String,
        responseAction: String,
        meetingId: String
    ) {
        disposable.add(
            myService.sendResponseRequestCall(callId, responseAction, meetingId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        Timber.tag("sendResponseRequestCall").d("onComplete")
                    }

                    override fun onError(error: Throwable) {
                        Timber.tag("sendResponseRequestCall").e(error)
                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}