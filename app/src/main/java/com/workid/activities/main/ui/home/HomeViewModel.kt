package com.workid.activities.main.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.workid.models.AccountModel
import com.workid.models.ContactModel
import com.workid.retrofit.ServiceCentral
import com.workid.utils.Constant
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class HomeViewModel : ViewModel() {

    private val myService = ServiceCentral()
    private val disposable = CompositeDisposable()
    private val listContactViewModel = ArrayList<AccountModel>()
    private val _listSuggestContact = MutableLiveData<ArrayList<AccountModel>>()
    var listSuggestContact : LiveData<ArrayList<AccountModel>> = _listSuggestContact

    //Loading all data from Server
    fun loadRecommendContact(auth:FirebaseAuth) {
        disposable.add(
            myService.getRecommendContact(auth.uid!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<ContactModel>() {
                    override fun onSuccess(t: ContactModel) {
                        _listSuggestContact.value = t.result
                    }

                    override fun onError(e: Throwable) {
                        Timber.tag("onError").e(e)
                    }
                })
        )
    }
    //End of loading all data from Firebase Database


    //Start loading all data from Firebase Database
    fun loadContactFromFirebase(auth: FirebaseAuth) {
        //Loading all data from Firebase Database
        val rootRef = FirebaseDatabase.getInstance().getReference(Constant.USER)
        rootRef.child("name").orderByValue()
        rootRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user = snapshot.getValue(AccountModel::class.java)
                if (snapshot.key != auth.currentUser!!.uid) {
                    listContactViewModel.add(user!!)
                    _listSuggestContact.value = listContactViewModel
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    //End of loading all data from Firebase Database

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}

    /**
     * Temporary disable this method
    fun fetchData(){
    Log.d("TAG", "fetchData: Called")
    disposable.add(
    myService.getContact()
    .subscribeOn(Schedulers.newThread())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribeWith(object : DisposableSingleObserver<ContactModel>() {
    override fun onSuccess(t: ContactModel) {
    Log.d("TAG", "onSuccess: data is $t")
    employeeList.value = t
    }

    override fun onError(e: Throwable) {
    Log.e("TAG", "onError: ${e.message}" )
    }
    })
    )
     */