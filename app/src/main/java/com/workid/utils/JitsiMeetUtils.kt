package com.workid.utils

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import com.workid.models.AccountModel
import com.workid.models.RequestCallItems
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import java.net.MalformedURLException
import java.net.URL

object JitsiMeetUtils {
    @SuppressLint("LogNotTimber")
    fun establishConnection() {
        val serverURL: URL
        serverURL = try {
            URL(Constant.MEET_URL)
        } catch (e: MalformedURLException) {
            Log.d("TAG", "establishConnection: ${e.message}")
            throw MalformedURLException(e.message)
        } catch (e: Exception){
            Log.d("TAG", "establishConnection: another error: ${e.message}")
            throw RuntimeException(e.message)
        }
        val defaultOptions = JitsiMeetConferenceOptions.Builder()
            .setServerURL(serverURL)
            .setWelcomePageEnabled(false)
            .build()
        JitsiMeet.setDefaultConferenceOptions(defaultOptions)
    }

    fun startMeeting(
        userData: AccountModel,
        meetingType: String,
        meetingID: String
    ): JitsiMeetConferenceOptions {
        val bundle = Bundle()
        bundle.putString(Constant.JITSI_DISPLAY_NAME, userData.customerName)
        bundle.putString(Constant.JITSI_EMAIL, userData.customerEmail)
        bundle.putString(Constant.JITSI_PHOTO_URL, userData.photoUrl)
        val jitsiUser = JitsiMeetUserInfo(bundle)
        val jitsiConnection = JitsiMeetConferenceOptions.Builder()
            .setUserInfo(jitsiUser)
            .setRoom(meetingID)

        if (meetingType == "audio") {
            jitsiConnection.setVideoMuted(true)
        }
        return jitsiConnection.build()
    }

    fun startMeeting(userData: RequestCallItems): JitsiMeetConferenceOptions {
        val bundle = Bundle()
        bundle.putString(Constant.JITSI_DISPLAY_NAME, userData.callerName)
        bundle.putString(Constant.JITSI_EMAIL, userData.callerEmail)
        bundle.putString(Constant.JITSI_PHOTO_URL, userData.callerPhotoURL)
        val jitsiUser = JitsiMeetUserInfo(bundle)
        val jitsiConnection = JitsiMeetConferenceOptions.Builder()
            .setUserInfo(jitsiUser)
            .setRoom(userData.meetingId)

        if (userData.meetingType == "audio") {
            jitsiConnection.setVideoMuted(true)
        }
        return jitsiConnection.build()
    }

}