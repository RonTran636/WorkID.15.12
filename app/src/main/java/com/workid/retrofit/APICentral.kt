package com.workid.retrofit

import com.workid.models.*
import com.workid.utils.Common
import com.workid.utils.Constant.Companion.CONTENT_TYPE
import com.workid.utils.Key
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

interface APICentral {

    @Headers("Authorization: key=${Key.FCM_SERVER_KEY}", "Content-Type:${CONTENT_TYPE}")
    @POST("fcm/send")
    fun sendRemoteMessage(
        @Body bodies: RequestAddFriendToFcmModel
    ) : Completable

    //Register User
    @POST("/api/workid/auth/register")
    fun registerUserToDatabase(@Body body: AccountModel): Single<ServerUserModel>

    //Adding friend to friend list
    @POST("/api/workid/add-friend")
    fun sendFriendRequestToServer(
        @Query("customer_id") receiverId: String,
        @Query("message") message:String,
        @Query("api_key") apiKey: String = Key.WORK_ID_API_KEY,
        @Header("Authorization") token: String = "Bearer " + Common.currentAccount!!.serverToken!!
    ):Completable

    //Load suggest friend list at Home Activity
    @GET("api/workid/random-friend?")
    fun getRecommendContact(
        @Query("customer_id") userId: String,
        @Query("api_key") apiKey: String = Key.WORK_ID_API_KEY,
        @Header("Authorization") token: String = "Bearer " + Common.currentAccount!!.serverToken!!
    ): Single<ContactModel>

    //Search a specified user by workId, email or by name
    @GET("/api/workid/search?")
    fun searchUserByEmailOrUid(
        @Query("keyword") keyword: String,
        @Query("api_key") apiKey: String = Key.WORK_ID_API_KEY,
        @Header("Authorization") token: String = "Bearer " + Common.currentAccount!!.serverToken!!
    ): Observable<ContactModel>

    //Load friend list of current user
    @GET("api/workid/list-friends")
    fun getCurrentUserFriendList(
        @Query("customer_id") customerId: String,
        @Query("api_key") apiKey: String = Key.WORK_ID_API_KEY
    ): Single<ContactModel>

    //Registration stage - Checking user whether existed on our database
    @GET("/api/workid/find-by-uid?")
    fun checkUserExistedOnDatabase(
        @Query("uid") uid: String,
        @Query("api_key") apiKey: String = Key.WORK_ID_API_KEY
    ): Single<ServerUserModel>

    //Send a call request to an user or a group
    @POST("/api/workid/make-call")
    fun sendRequestCall(
        @Body bodies: ServerCallModel,
        @Header("Authorization") token: String = "Bearer " + Common.currentAccount!!.serverToken!!,
        @Query("api_key") apiKey: String = Key.WORK_ID_API_KEY
    ): Single<ResponseModel>

    //Response to a request call
    @POST("/api/workid/action-call")
    fun sendResponseRequestCall(
        @Query("call_id") callId: String,
        @Query("action") responseAction: String,
        @Query("meeting_id") meetingId: String,
        @Header("Authorization") token: String = "Bearer " + Common.currentAccount!!.serverToken!!,
        @Query("api_key") apiKey: String = Key.WORK_ID_API_KEY
    ): Completable

    //Update Fcm token to server's database
    @PUT("/api/workid/update-fcm-token")
    fun updateFcmToken(
        @Query("fcm_token") fcmToken: String?,
        @Header("Authorization") token: String = "Bearer " + Common.currentAccount?.serverToken,
        @Query("api_key") apiKey: String = Key.WORK_ID_API_KEY
    ): Completable
}
