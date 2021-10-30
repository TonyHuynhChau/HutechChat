package com.example.firebaseappchat.model

class Post(
    val uid: String,
    val date: String,
    val time: String,
    val status: String,
    val name: String,
    val Urlphoto: String
) {
    constructor() : this("", "", "", "", "", "")
}