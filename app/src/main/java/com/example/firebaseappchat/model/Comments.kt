package com.example.firebaseappchat.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
class Comments(
    val uid: String,
    val date: String,
    val time: String,
    val comment: String,
    val name: String,
    val Urlphoto: String
) : Parcelable {
    constructor() : this("", "", "", "", "", "")
}