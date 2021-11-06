package com.example.firebaseappchat.model

class ChatMessage(
    val id: String,
    var text: String,
    val formId: String,
    val toId: String,
    val timestamp: Long,
    val check: Boolean
) {
    constructor() : this("", "", "", "", -1, false)
}