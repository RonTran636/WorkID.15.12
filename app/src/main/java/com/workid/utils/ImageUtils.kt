package com.workid.utils

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.workid.R

fun getProgressDrawable(context: Context): CircularProgressDrawable {
        return CircularProgressDrawable(context).apply {
            strokeWidth = 10f
            centerRadius = 50f
            start()
        }
    }

fun ImageView.loadImage(url: String?, progressDrawable: CircularProgressDrawable?) {
        val option = RequestOptions()
            .transform(RoundedCorners(45))
            .placeholder(progressDrawable)
            .error(R.drawable.ic_avatar_default)

        Glide.with(this.context)
            .setDefaultRequestOptions(option)
            .load(url)
            .into(this)
    }

fun LottieAnimationView.setLottieImage(rawSource: Int){
    this.setAnimation(rawSource)
    this.playAnimation()
}

fun Context.loadImageToNotificationAvatar(url: String?): Bitmap? {
    return if (url != null) {
        Glide.with(applicationContext)
            .asBitmap()
            .circleCrop()
            .placeholder(R.drawable.ic_avatar_default)
            .load(url)
            .submit(512, 512)
            .get()
    } else {
        null
    }
}
