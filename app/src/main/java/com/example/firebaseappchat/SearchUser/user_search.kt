package com.example.firebaseappchat.SearchUser

class user_search {
    var username: String? = null
    var img: String? = null

    constructor()

    constructor(username: String?, img: String?) {
        this.img = img
        this.username = username
    }
}