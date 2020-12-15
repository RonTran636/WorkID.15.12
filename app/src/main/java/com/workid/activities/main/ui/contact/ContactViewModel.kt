package com.workid.activities.main.ui.contact

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.workid.models.AccountModel
import com.workid.models.ContactModel
import com.workid.retrofit.ServiceCentral
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers

class ContactViewModel : ViewModel() {

    private val myService = ServiceCentral()
    private val disposable = CompositeDisposable()
    private val _listFriend = MutableLiveData<ArrayList<AccountModel>>()
    var listFriend : LiveData<ArrayList<AccountModel>> = _listFriend
    fun getCurrentUserFriendList(customerId: String){
        disposable.add(
            myService.getCurrentUserFriendList(customerId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object :DisposableSingleObserver<ContactModel>(){
                    override fun onSuccess(t: ContactModel) {
                       _listFriend.value = t.result
                    }

                    override fun onError(e: Throwable) {
                        TODO("Not yet implemented")
                    }
                })
        )
    }
}