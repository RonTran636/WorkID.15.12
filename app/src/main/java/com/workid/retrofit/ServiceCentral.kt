package com.workid.retrofit

import com.workid.models.*
import com.workid.utils.Constant.Companion.BASE_URL
import com.workid.utils.Constant.Companion.FCM_URL
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("PrivatePropertyName")
class ServiceCentral {

    private val clientOkHttpClient = OkHttpClient().newBuilder()
    private fun invoke(url: String) : APICentral {
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(clientOkHttpClient.build())
            .build()
            .create(APICentral::class.java)
    }
    //POST Data to server:
    fun sendRemoteMessage(body: RequestAddFriendToFcmModel):Completable{
        return invoke(FCM_URL).sendRemoteMessage(body)
    }

    fun registerUserToDatabase(body: AccountModel): Single<ServerUserModel> {
        return invoke(BASE_URL).registerUserToDatabase(body)
    }

    fun sendFriendRequestToServer(remoteMessage: RequestAddFriendItem): Completable {
        return invoke(BASE_URL).sendFriendRequestToServer(
            remoteMessage.receiverId!!,
            remoteMessage.messageDetail!!
        )
    }

    fun sendRequestCall(
        customerId: String,
        meetingType: String,
        meetingId: String
    ): Single<ResponseModel> {
        return invoke(BASE_URL).sendRequestCall(ServerCallModel(customerId, meetingType, meetingId))
    }

    fun sendResponseRequestCall(
        callId: String,
        responseAction: String,
        meetingId: String,
    ): Completable {
        return invoke(BASE_URL).sendResponseRequestCall(callId, responseAction, meetingId)
    }

    //GET Data from Server:
    fun checkUserExistedOnDatabase(uid: String): Single<ServerUserModel> {
        return invoke(BASE_URL).checkUserExistedOnDatabase(uid)
    }

    fun searchUserByEmailOrUid(keyword: String): Observable<ContactModel> {
        return invoke(BASE_URL).searchUserByEmailOrUid(keyword)
    }

    fun getRecommendContact(customerID: String): Single<ContactModel> {
        return invoke(BASE_URL).getRecommendContact(customerID)
    }

    fun getCurrentUserFriendList(customerId: String): Single<ContactModel> {
        return invoke(BASE_URL).getCurrentUserFriendList(customerId)
    }

    fun updateFcmToken(token: String?): Completable {
        return invoke(BASE_URL).updateFcmToken(token)
    }
}
