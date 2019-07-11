package com.example.kotlin_messaging_app.VO

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class UserVO (val uid: String, val username: String, val profileimageUrl: String): Parcelable
{
    constructor(): this("","","")
}