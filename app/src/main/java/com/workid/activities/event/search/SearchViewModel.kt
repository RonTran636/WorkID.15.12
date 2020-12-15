package com.workid.activities.event.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay3.PublishRelay
import com.workid.models.AccountModel
import com.workid.retrofit.ServiceCentral
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SearchViewModel : ViewModel() {

    private val autoCompletePublishSubject = PublishRelay.create<String>()
    private val myService = ServiceCentral()
    private val disposable = CompositeDisposable()
    private val _searchResult = MutableLiveData<ArrayList<AccountModel>>()
    var searchResult: LiveData<ArrayList<AccountModel>> = _searchResult
    private val _isEmptyList = MutableLiveData<Boolean>()
    val isEmptyList: LiveData<Boolean> = _isEmptyList

    //Called on every character change made to the search `EditText`
    fun onInputStateChanged(query: String) {
        autoCompletePublishSubject.accept(query.trim())
    }

    /**
     * Called only once when the `ViewModel` is being created
     * Initialises the autocomplete publish subject
     */
    fun configureAutoComplete() {
        disposable.add(
            autoCompletePublishSubject
                .debounce(1000, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .switchMap { myService.searchUserByEmailOrUid(it) }
                .subscribe({
                    _searchResult.postValue(it.result)
                    if (it.result.size>0){
                        _isEmptyList.postValue(false)
                    }else {
                        _isEmptyList.postValue(true)
                    }
                }, {
                    Log.d("TAG", "configureAutoComplete: error: ${it.message}")
                })
        )
    }

}