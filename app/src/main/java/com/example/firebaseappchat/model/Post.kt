package com.example.firebaseappchat.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Post(
    val uid: String,
    val date: String,
    val time: String,
    val status: String,
    val name: String,
    val Urlphoto: String
) : Parcelable {
    constructor() : this("", "", "", "", "", "")
}